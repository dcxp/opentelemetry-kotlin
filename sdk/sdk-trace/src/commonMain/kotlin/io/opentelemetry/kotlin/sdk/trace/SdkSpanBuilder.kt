/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.booleanKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.doubleKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.longKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.stringKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.common.normalizeToNanos
import io.opentelemetry.kotlin.api.internal.ImmutableSpanContext
import io.opentelemetry.kotlin.api.trace.Span
import io.opentelemetry.kotlin.api.trace.SpanBuilder
import io.opentelemetry.kotlin.api.trace.SpanContext
import io.opentelemetry.kotlin.api.trace.SpanKind
import io.opentelemetry.kotlin.api.trace.TraceFlags
import io.opentelemetry.kotlin.api.trace.TraceState
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.trace.data.LinkData
import io.opentelemetry.kotlin.sdk.trace.samplers.SamplingDecision
import io.opentelemetry.kotlin.sdk.trace.samplers.SamplingResult
import kotlinx.datetime.DateTimeUnit

/** [SdkSpanBuilder] is SDK implementation of [SpanBuilder]. */
internal class SdkSpanBuilder(
    private val spanName: String,
    private val instrumentationLibraryInfo: InstrumentationLibraryInfo,
    private val tracerSharedState: TracerSharedState,
    private val spanLimits: SpanLimits
) : SpanBuilder {
    private var parent: Context? = null
    private var spanKind = SpanKind.INTERNAL

    private var attributes: AttributesMap? = null

    private val links: MutableList<LinkData> by lazy { ArrayList(spanLimits.maxNumberOfLinks) }

    private var totalNumberOfLinksAdded = 0
    private var startEpochNanos: Long = 0

    override fun setParent(context: Context): SpanBuilder {
        parent = context
        return this
    }

    override fun setNoParent(): SpanBuilder {
        parent = Context.root()
        return this
    }

    override fun setSpanKind(spanKind: SpanKind): SpanBuilder {
        this.spanKind = spanKind
        return this
    }

    override fun addLink(spanContext: SpanContext): SpanBuilder {
        if (spanContext.isNotValid) {
            return this
        }
        addLink(LinkData.create(spanContext))
        return this
    }

    override fun addLink(spanContext: SpanContext, attributes: Attributes): SpanBuilder {
        if (spanContext.isNotValid) {
            return this
        }
        val totalAttributeCount = attributes.size
        addLink(
            LinkData.create(
                spanContext,
                AttributeUtil.applyAttributesLimit(
                    attributes,
                    spanLimits.maxNumberOfAttributesPerLink,
                    spanLimits.maxAttributeValueLength
                ),
                totalAttributeCount
            )
        )
        return this
    }

    fun addLink(link: LinkData) {
        totalNumberOfLinksAdded++
        // don't bother doing anything with any links beyond the max.
        if (links.size == spanLimits.maxNumberOfLinks) {
            return
        }
        links.add(link)
    }

    override fun setAttribute(key: String, value: String): SpanBuilder {
        return setAttribute(stringKey(key), value)
    }

    override fun setAttribute(key: String, value: Long): SpanBuilder {
        return setAttribute(longKey(key), value)
    }

    override fun setAttribute(key: String, value: Double): SpanBuilder {
        return setAttribute(doubleKey(key), value)
    }

    override fun setAttribute(key: String, value: Boolean): SpanBuilder {
        return setAttribute(booleanKey(key), value)
    }

    override fun <T : Any> setAttribute(key: AttributeKey<T>, value: T): SpanBuilder {
        if (key.key.isEmpty()) {
            return this
        }
        attributes()!![key] = value
        return this
    }

    override fun setStartTimestamp(startTimestamp: Long, unit: DateTimeUnit): SpanBuilder {
        if (startTimestamp < 0) {
            return this
        }
        startEpochNanos = unit.normalizeToNanos(startTimestamp)
        return this
    }

    override fun startSpan(): Span {
        val parentContext = if (parent == null) Context.current() else parent!!
        val parentSpan = Span.fromContext(parentContext)
        val parentSpanContext = parentSpan.spanContext
        val idGenerator: IdGenerator = tracerSharedState.getIdGenerator()
        val spanId: String = idGenerator.generateSpanId()
        val traceId =
            if (!parentSpanContext.isValid) {
                // New root span.
                idGenerator.generateTraceId()
            } else {
                // New child span.
                parentSpanContext.traceId
            }
        val immutableLinks: List<LinkData> = links.toList()
        val immutableAttributes = if (attributes == null) Attributes.empty() else attributes!!
        val samplingResult: SamplingResult =
            tracerSharedState
                .getSampler()
                .shouldSample(
                    parentContext,
                    traceId,
                    spanName,
                    spanKind,
                    immutableAttributes,
                    immutableLinks
                )
        val samplingDecision: SamplingDecision = samplingResult.decision
        val samplingResultTraceState: TraceState =
            samplingResult.getUpdatedTraceState(parentSpanContext.traceState)
        val spanContext =
            ImmutableSpanContext.create(
                traceId,
                spanId,
                if (isSampled(samplingDecision)) TraceFlags.sampled else TraceFlags.default,
                samplingResultTraceState,
                /* remote= */ false,
                tracerSharedState.isIdGeneratorSafeToSkipIdValidation
            )
        if (!isRecording(samplingDecision)) {
            return Span.wrap(spanContext)
        }
        val samplingAttributes: Attributes = samplingResult.attributes
        if (!samplingAttributes.isEmpty()) {
            samplingAttributes.forEach { key, value ->
                attributes()!![key as AttributeKey<Any>] = value
            }
        }

        // Avoid any possibility to modify the attributes by adding attributes to the Builder after
        // the
        // startSpan is called. If that happens all the attributes will be added in a new map.
        val recordedAttributes: AttributesMap? = attributes
        attributes = null
        return RecordEventsReadableSpan.startSpan(
            spanContext,
            spanName,
            instrumentationLibraryInfo,
            spanKind,
            parentSpan,
            parentContext,
            spanLimits,
            tracerSharedState.getActiveSpanProcessor(),
            tracerSharedState.getClock(),
            tracerSharedState.getResource(),
            recordedAttributes,
            immutableLinks,
            totalNumberOfLinksAdded,
            startEpochNanos
        )
    }

    private fun attributes(): AttributesMap? {
        var attributes: AttributesMap? = attributes
        if (attributes == null) {
            this.attributes =
                AttributesMap(spanLimits.maxNumberOfAttributes, spanLimits.maxAttributeValueLength)
            attributes = this.attributes
        }
        return attributes
    }

    companion object {
        // Visible for testing
        fun isRecording(decision: SamplingDecision): Boolean {
            return (SamplingDecision.RECORD_ONLY == decision ||
                SamplingDecision.RECORD_AND_SAMPLE == decision)
        }

        // Visible for testing
        fun isSampled(decision: SamplingDecision): Boolean {
            return SamplingDecision.RECORD_AND_SAMPLE == decision
        }
    }
}
