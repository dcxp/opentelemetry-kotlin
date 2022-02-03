/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.common.normalizeToNanos
import io.opentelemetry.kotlin.api.trace.Span
import io.opentelemetry.kotlin.api.trace.SpanContext
import io.opentelemetry.kotlin.api.trace.SpanKind
import io.opentelemetry.kotlin.api.trace.StatusCode
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.Clock
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.resources.Resource
import io.opentelemetry.kotlin.sdk.trace.data.EventData
import io.opentelemetry.kotlin.sdk.trace.data.LinkData
import io.opentelemetry.kotlin.sdk.trace.data.SpanData
import io.opentelemetry.kotlin.sdk.trace.data.StatusData
import io.opentelemetry.kotlin.semconv.trace.attributes.SemanticAttributes
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.DateTimeUnit

/** Implementation for the [Span] class that records trace events. */
internal class RecordEventsReadableSpan
private constructor(
    // Contains the identifiers associated with this Span.
    override val spanContext: SpanContext,
    name: String,
    override val instrumentationLibraryInfo: InstrumentationLibraryInfo,
    override val kind: SpanKind,
    override val parentSpanContext: SpanContext,
    private val spanLimits: SpanLimits,
    private val spanProcessor: SpanProcessor,
    val clock: AnchoredClock,
    val resource: Resource,
    attributes: AttributesMap?,
    private val links: List<LinkData>,
    val totalRecordedLinks: Int,
    val startEpochNanos: Long
) : ReadWriteSpan {

    private val nameInternal = atomic(name)

    override val name: String
        get() = nameInternal.value

    // List of recorded events.
    private val events = atomic<PersistentList<EventData>>(persistentListOf())

    private val attributes = atomic(attributes)

    // Number of events recorded.
    private val totalRecordedEvents = atomic(0)

    // The status of the span.
    private val status = atomic(StatusData.unset())

    // The end time of the span.
    private val endEpochNanos = atomic(0L)

    // True if the span is ended.
    private val hasEnded = atomic(false)

    override fun toSpanData(): SpanData {
        return SpanWrapper.create(
            this,
            links,
            immutableTimedEvents,
            immutableAttributes,
            if (attributes.value == null) 0 else attributes.value!!.totalAddedValues,
            totalRecordedEvents.value,
            status.value,
            name,
            endEpochNanos.value,
            hasEnded.value
        )
    }

    override fun hasEnded(): Boolean {
        return hasEnded.value
    }

    /**
     * Returns the latency of the `Span` in nanos. If still active then returns now() - start time.
     *
     * @return the latency of the `Span` in nanos.
     */
    override val latencyNanos: Long
        get() {
            return (if (hasEnded()) endEpochNanos.value else clock.now()) - startEpochNanos
        }

    override fun <T : Any> setAttribute(key: AttributeKey<T>, value: T): ReadWriteSpan {
        if (key.key.isEmpty()) {
            return this
        }
        if (hasEnded()) {
            // logger.log(java.util.logging.Level.FINE, "Calling setAttribute() on an ended Span.")
            return this
        }
        attributes.compareAndSet(
            null,
            AttributesMap(spanLimits.maxNumberOfAttributes, spanLimits.maxAttributeValueLength)
        )
        attributes.value!![key] = value
        return this
    }

    override fun <T : Any> getAttribute(key: AttributeKey<T>): T? {
        if (attributes.value != null) {
            return attributes.value!![key] as T
        }
        return null
    }

    override fun addEvent(name: String, attributes: Attributes): ReadWriteSpan {
        val totalAttributeCount = attributes.size
        addTimedEvent(
            EventData.create(
                clock.now(),
                name,
                AttributeUtil.applyAttributesLimit(
                    attributes,
                    spanLimits.maxNumberOfAttributesPerEvent,
                    spanLimits.maxAttributeValueLength
                ),
                totalAttributeCount
            )
        )
        return this
    }

    override fun addEvent(
        name: String,
        attributes: Attributes,
        timestamp: Long,
        unit: DateTimeUnit
    ): Span {
        val totalAttributeCount = attributes.size
        addTimedEvent(
            EventData.create(
                unit.normalizeToNanos(timestamp),
                name,
                AttributeUtil.applyAttributesLimit(
                    attributes,
                    spanLimits.maxNumberOfAttributesPerEvent,
                    spanLimits.maxAttributeValueLength
                ),
                totalAttributeCount
            )
        )
        return this
    }
    private fun addTimedEvent(timedEvent: EventData) {
        if (hasEnded()) {
            // logger.log(java.util.logging.Level.FINE, "Calling addEvent() on an ended Span.")
            return
        }

        if (totalRecordedEvents.getAndIncrement() < spanLimits.maxNumberOfEvents) {
            events.update { it.add(timedEvent) }
        }
    }

    override fun setStatus(statusCode: StatusCode, description: String): Span {
        if (hasEnded()) {
            // logger.log(java.util.logging.Level.FINE, "Calling setStatus() on an ended Span.")
            return this
        }
        status.value = StatusData.create(statusCode, description)
        return this
    }

    fun recordException(exception: Throwable): ReadWriteSpan {
        recordException(exception, Attributes.empty())
        return this
    }

    override fun recordException(
        exception: Throwable,
        additionalAttributes: Attributes
    ): ReadWriteSpan {
        val timestampNanos: Long = clock.now()
        val attributes = Attributes.builder()
        attributes.put(SemanticAttributes.EXCEPTION_TYPE, exception::class.simpleName!!)
        if (exception.message != null) {
            attributes.put(SemanticAttributes.EXCEPTION_MESSAGE, exception.message!!)
        }
        attributes.put(SemanticAttributes.EXCEPTION_STACKTRACE, exception.stackTraceToString())
        attributes.putAll(additionalAttributes)
        addEvent(
            SemanticAttributes.EXCEPTION_EVENT_NAME,
            attributes.build(),
            timestampNanos,
            DateTimeUnit.NANOSECOND
        )
        return this
    }

    override fun updateName(name: String): ReadWriteSpan {
        if (hasEnded()) {
            // logger.log(java.util.logging.Level.FINE, "Calling updateName() on an ended Span.")
            return this
        }
        this.nameInternal.value = name
        return this
    }

    override fun end() {
        endInternal(clock.now())
    }

    override fun end(timestamp: Long, unit: DateTimeUnit) {
        endInternal(if (timestamp == 0L) clock.now() else unit.normalizeToNanos(timestamp))
    }

    private fun endInternal(endEpochNanos: Long) {
        if (hasEnded()) {
            // logger.log(java.util.logging.Level.FINE, "Calling end() on an ended Span.")
            return
        }
        this.endEpochNanos.value = endEpochNanos
        hasEnded.value = true
        spanProcessor.onEnd(this)
    }

    override fun isRecording(): Boolean {
        return !hasEnded()
    }
    // if the span has ended, then the events are unmodifiable
    // so we can return them directly and save copying all the data.
    private val immutableTimedEvents: List<EventData>
        get() {
            if (events.value.isEmpty()) {
                return emptyList()
            }

            // if the span has ended, then the events are unmodifiable
            // so we can return them directly and save copying all the data.
            return if (hasEnded()) {
                events.value
            } else events.value
        }

    // if the span has ended, then the attributes are unmodifiable,
    // so we can return them directly and save copying all the data.
    // otherwise, make a copy of the data into an immutable container.
    private val immutableAttributes: Attributes
        get() {
            if (attributes.value == null || attributes.value!!.isEmpty()) {
                return Attributes.empty()
            }
            // if the span has ended, then the attributes are unmodifiable,
            // so we can return them directly and save copying all the data.
            return if (hasEnded()) {
                attributes.value!!
            } else attributes.value!!.immutableCopy()
            // otherwise, make a copy of the data into an immutable container.
        }

    override fun toString(): String {
        val name: String = this.name
        val attributes: String = this.attributes.toString()
        val status: String = this.status.toString()
        val totalRecordedEvents: Long = this.totalRecordedEvents.value.toLong()
        val endEpochNanos: Long = this.endEpochNanos.value
        return ("RecordEventsReadableSpan{traceId=" +
            spanContext.traceId +
            ", spanId=" +
            spanContext.spanId +
            ", parentSpanContext=" +
            parentSpanContext +
            ", name=" +
            name +
            ", kind=" +
            kind +
            ", attributes=" +
            attributes +
            ", status=" +
            status +
            ", totalRecordedEvents=" +
            totalRecordedEvents +
            ", totalRecordedLinks=" +
            totalRecordedLinks +
            ", startEpochNanos=" +
            startEpochNanos +
            ", endEpochNanos=" +
            endEpochNanos +
            "}")
    }

    companion object {
        // private val logger: java.util.logging.Logger =
        //    java.util.logging.Logger.getLogger(RecordEventsReadableSpan::class.java.getName())

        /**
         * Creates and starts a span with the given configuration.
         *
         * @param context supplies the trace_id and span_id for the newly started span.
         * @param name the displayed name for the new span.
         * @param kind the span kind.
         * @param parentSpan the parent span, or [Span.getInvalid] if this span is a root span.
         * @param spanLimits trace parameters like sampler and probability.
         * @param spanProcessor handler called when the span starts and ends.
         * @param tracerClock the tracer's clock
         * @param resource the resource associated with this span.
         * @param attributes the attributes set during span creation.
         * @param links the links set during span creation, may be truncated. The list MUST be
         * immutable.
         * @return a new and started span.
         */
        fun startSpan(
            context: SpanContext,
            name: String,
            instrumentationLibraryInfo: InstrumentationLibraryInfo,
            kind: SpanKind,
            parentSpan: Span,
            parentContext: Context,
            spanLimits: SpanLimits,
            spanProcessor: SpanProcessor,
            tracerClock: Clock,
            resource: Resource,
            attributes: AttributesMap?,
            links: List<LinkData>,
            totalRecordedLinks: Int,
            userStartEpochNanos: Long
        ): RecordEventsReadableSpan {
            val createdAnchoredClock: Boolean
            val clock: AnchoredClock
            if (parentSpan is RecordEventsReadableSpan) {
                clock = parentSpan.clock
                createdAnchoredClock = false
            } else {
                clock = AnchoredClock.create(tracerClock)
                createdAnchoredClock = true
            }
            val startEpochNanos: Long =
                if (userStartEpochNanos != 0L) {
                    userStartEpochNanos
                } else if (createdAnchoredClock) {
                    // If this is a new AnchoredClock, the start time is now, so just use it to
                    // avoid
                    // recomputing current time.
                    clock.startTime()
                } else {
                    // AnchoredClock created in the past, so need to compute now.
                    clock.now()
                }
            val span =
                RecordEventsReadableSpan(
                    context,
                    name,
                    instrumentationLibraryInfo,
                    kind,
                    parentSpan.spanContext,
                    spanLimits,
                    spanProcessor,
                    clock,
                    resource,
                    attributes,
                    links,
                    totalRecordedLinks,
                    startEpochNanos
                )
            // Call onStart here instead of calling in the constructor to make sure the span is
            // completely
            // initialized.
            spanProcessor.onStart(parentContext, span)
            return span
        }
    }
}
