/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.samplers

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.trace.Span
import io.opentelemetry.kotlin.api.trace.SpanContext
import io.opentelemetry.kotlin.api.trace.SpanKind
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.trace.data.LinkData

/**
 * A Sampler that uses the sampled flag of the parent Span, if present. If the span has no parent,
 * this Sampler will use the "root" sampler that it is built with. See documentation on the [ ]
 * methods for the details on the various configurable options.
 */
internal class ParentBasedSampler(
    private val root: Sampler,
    remoteParentSampled: Sampler?,
    remoteParentNotSampled: Sampler?,
    localParentSampled: Sampler?,
    localParentNotSampled: Sampler?
) : Sampler {
    private val remoteParentSampled: Sampler
    private val remoteParentNotSampled: Sampler
    private val localParentSampled: Sampler
    private val localParentNotSampled: Sampler

    init {
        this.remoteParentSampled = remoteParentSampled ?: Sampler.alwaysOn()
        this.remoteParentNotSampled = remoteParentNotSampled ?: Sampler.alwaysOff()
        this.localParentSampled = localParentSampled ?: Sampler.alwaysOn()
        this.localParentNotSampled = localParentNotSampled ?: Sampler.alwaysOff()
    }

    // If a parent is set, always follows the same sampling decision as the parent.
    // Otherwise, uses the delegateSampler provided at initialization to make a decision.
    override fun shouldSample(
        parentContext: Context,
        traceId: String,
        name: String,
        spanKind: SpanKind,
        attributes: Attributes,
        parentLinks: List<LinkData>
    ): SamplingResult {
        val parentSpanContext: SpanContext = Span.fromContext(parentContext).spanContext
        if (!parentSpanContext.isValid) {
            return root.shouldSample(
                parentContext,
                traceId,
                name,
                spanKind,
                attributes,
                parentLinks
            )
        }
        if (parentSpanContext.isRemote) {
            return if (parentSpanContext.isSampled())
                remoteParentSampled.shouldSample(
                    parentContext,
                    traceId,
                    name,
                    spanKind,
                    attributes,
                    parentLinks
                )
            else
                remoteParentNotSampled.shouldSample(
                    parentContext,
                    traceId,
                    name,
                    spanKind,
                    attributes,
                    parentLinks
                )
        }
        return if (parentSpanContext.isSampled())
            localParentSampled.shouldSample(
                parentContext,
                traceId,
                name,
                spanKind,
                attributes,
                parentLinks
            )
        else
            localParentNotSampled.shouldSample(
                parentContext,
                traceId,
                name,
                spanKind,
                attributes,
                parentLinks
            )
    }

    override val description: String
        get() =
            "ParentBased{" +
                "root:${root.description}," +
                "remoteParentSampled:${remoteParentSampled.description}," +
                "remoteParentNotSampled:${remoteParentNotSampled.description}," +
                "localParentSampled:${localParentSampled.description}," +
                "localParentNotSampled:${localParentNotSampled.description}" +
                "}"

    override fun toString(): String {
        return description
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is ParentBasedSampler) {
            return false
        }
        val that = other
        return root == that.root &&
            remoteParentSampled == that.remoteParentSampled &&
            remoteParentNotSampled == that.remoteParentNotSampled &&
            localParentSampled == that.localParentSampled &&
            localParentNotSampled == that.localParentNotSampled
    }

    override fun hashCode(): Int {
        var result: Int = root.hashCode()
        result = 31 * result + remoteParentSampled.hashCode()
        result = 31 * result + remoteParentNotSampled.hashCode()
        result = 31 * result + localParentSampled.hashCode()
        result = 31 * result + localParentNotSampled.hashCode()
        return result
    }
}
