/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.samplers

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.trace.SpanKind
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.trace.data.LinkData

/** A Sampler is used to make decisions on [Span] sampling. */
interface Sampler {
    /**
     * Called during [Span] creation to make a sampling samplingResult.
     *
     * @param parentContext the parent span's [SpanContext]. This can be `SpanContext.INVALID` if
     * this is a root span.
     * @param traceId the [TraceId] for the new `Span`. This will be identical to that in the
     * parentContext, unless this is a root span.
     * @param name the name of the new `Span`.
     * @param spanKind the [SpanKind] of the `Span`.
     * @param attributes [Attributes] associated with the span.
     * @param parentLinks the parentLinks associated with the new `Span`.
     * @return sampling samplingResult whether span should be sampled or not.
     */
    fun shouldSample(
        parentContext: Context,
        traceId: String,
        name: String,
        spanKind: SpanKind,
        attributes: Attributes,
        parentLinks: List<LinkData>
    ): SamplingResult

    /**
     * Returns the description of this `Sampler`. This may be displayed on debug pages or in the
     * logs.
     *
     * Example: "TraceIdRatioBased{0.000100}"
     *
     * @return the description of this `Sampler`.
     */
    val description: String

    companion object {
        /**
         * Returns a [Sampler] that always makes a "yes" [SamplingResult] for [Span] sampling.
         *
         * @return a `Sampler` that always makes a "yes" [SamplingResult] for `Span` sampling.
         */
        fun alwaysOn(): Sampler {
            return AlwaysOnSampler.INSTANCE
        }

        /**
         * Returns a [Sampler] that always makes a "no" [SamplingResult] for [Span] sampling.
         *
         * @return a `Sampler` that always makes a "no" [SamplingResult] for `Span` sampling.
         */
        fun alwaysOff(): Sampler {
            return AlwaysOffSampler.INSTANCE
        }

        /**
         * Returns a [Sampler] that always makes the same decision as the parent [Span] to whether
         * or not to sample. If there is no parent, the Sampler uses the provided Sampler delegate
         * to determine the sampling decision.
         *
         * This method is equivalent to calling `#parentBasedBuilder(Sampler).build()`
         *
         * @param root the `Sampler` which is used to make the sampling decisions if the parent does
         * not exist.
         * @return a `Sampler` that follows the parent's sampling decision if one exists, otherwise
         * following the root sampler's decision.
         */
        fun parentBased(root: Sampler): Sampler {
            return parentBasedBuilder(root).build()
        }

        /**
         * Returns a [ParentBasedSamplerBuilder] that enables configuration of the parent-based
         * sampling strategy. The parent's sampling decision is used if a parent span exists,
         * otherwise this strategy uses the root sampler's decision. There are a several options
         * available on the builder to control the precise behavior of how the decision will be
         * made.
         *
         * @param root the required `Sampler` which is used to make the sampling decisions if the
         * parent does not exist.
         * @return a `ParentBasedSamplerBuilder`
         */
        fun parentBasedBuilder(root: Sampler): ParentBasedSamplerBuilder {
            return ParentBasedSamplerBuilder(root)
        }

        /**
         * Returns a new TraceIdRatioBased [Sampler]. The ratio of sampling a trace is equal to that
         * of the specified ratio.
         *
         * The algorithm used by the [Sampler] is undefined, notably it may or may not use parts of
         * the trace ID when generating a sampling decision. Currently, only the ratio of traces
         * that are sampled can be relied on, not how the sampled traces are determined. As such, it
         * is recommended to only use this [Sampler] for root spans using [ ][Sampler.parentBased].
         *
         * @param ratio The desired ratio of sampling. Must be within [0.0, 1.0].
         * @return a new TraceIdRatioBased [Sampler].
         * @throws IllegalArgumentException if `ratio` is out of range
         */
        fun traceIdRatioBased(ratio: Double): Sampler? {
            return TraceIdRatioBasedSampler.create(ratio)
        }
    }
}
