/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.samplers

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.trace.TraceState

/** Sampling result returned by [Sampler.shouldSample]. */
interface SamplingResult {
    /**
     * Return decision on whether a span should be recorded, recorded and sampled or not recorded.
     *
     * @return sampling result.
     */
    val decision: SamplingDecision

    /**
     * Return tags which will be attached to the span.
     *
     * @return attributes added to span. These attributes should be added to the span only when [the
     * sampling decision][.getDecision] is [SamplingDecision.RECORD_ONLY] or
     * [SamplingDecision.RECORD_AND_SAMPLE].
     */
    val attributes: Attributes

    /**
     * Return an optionally-updated [TraceState], based on the parent TraceState. This may return
     * the same [TraceState] that was provided originally, or an updated one.
     *
     * @param parentTraceState The TraceState from the parent span. Might be an empty TraceState, if
     * there is no parent. This will be the same TraceState that was passed in via the [ ] parameter
     * on the [Sampler.shouldSample] call.
     */
    fun getUpdatedTraceState(parentTraceState: TraceState): TraceState {
        return parentTraceState
    }

    companion object {
        /**
         * Returns a [SamplingResult] with no attributes and [SamplingResult.getDecision] returning
         * `decision`.
         *
         * This is meant for use by custom [Sampler] implementations.
         *
         * Use [.create] if you need attributes.
         *
         * @param decision The decision made on the span.
         * @return A [SamplingResult] with empty attributes and the provided `decision`.
         */
        fun create(decision: SamplingDecision): SamplingResult {
            return when (decision) {
                SamplingDecision.RECORD_AND_SAMPLE ->
                    ImmutableSamplingResult.EMPTY_RECORDED_AND_SAMPLED_SAMPLING_RESULT
                SamplingDecision.RECORD_ONLY ->
                    ImmutableSamplingResult.EMPTY_RECORDED_SAMPLING_RESULT
                SamplingDecision.DROP ->
                    ImmutableSamplingResult.EMPTY_NOT_SAMPLED_OR_RECORDED_SAMPLING_RESULT
            }
        }

        /**
         * Returns a [SamplingResult] with the given `attributes` and [ ]
         * [SamplingResult.getDecision] returning `decision`.
         *
         * This is meant for use by custom [Sampler] implementations.
         *
         * Using [.create] instead of this method is slightly faster and shorter if you don't need
         * attributes.
         *
         * @param decision The decision made on the span.
         * @param attributes The attributes to return from [SamplingResult.getAttributes]. A
         * different object instance with the same elements may be returned.
         * @return A [SamplingResult] with the attributes equivalent to `attributes` and the
         * provided `decision`.
         */
        fun create(decision: SamplingDecision, attributes: Attributes): SamplingResult {
            return if (attributes.isEmpty()) create(decision)
            else ImmutableSamplingResult.createSamplingResult(decision, attributes)
        }

        /**
         * Returns a [SamplingResult] corresponding to [SamplingDecision.RECORD_AND_SAMPLE] with no
         * attributes.
         *
         * This is meant for use by custom [Sampler] implementations and is equivalent to calling
         * `SamplingResult.create(SamplingDecision.RECORD_AND_SAMPLE)`.
         *
         * @return A "record and sample" [SamplingResult] with empty attributes.
         */
        fun recordAndSample(): SamplingResult {
            return ImmutableSamplingResult.EMPTY_RECORDED_AND_SAMPLED_SAMPLING_RESULT
        }

        /**
         * Returns a [SamplingResult] corresponding to [SamplingDecision.RECORD_ONLY] with no
         * attributes.
         *
         * This is meant for use by custom [Sampler] implementations and is equivalent to calling
         * `SamplingResult.create(SamplingDecision.RECORD_ONLY)`.
         *
         * @return A "record only" [SamplingResult] with empty attributes.
         */
        fun recordOnly(): SamplingResult {
            return ImmutableSamplingResult.EMPTY_RECORDED_SAMPLING_RESULT
        }

        /**
         * Returns a [SamplingResult] corresponding to [SamplingDecision.DROP] with no attributes.
         *
         * This is meant for use by custom [Sampler] implementations and is equivalent to calling
         * `SamplingResult.create(SamplingDecision.DROP)`.
         *
         * @return A "drop" [SamplingResult] with empty attributes.
         */
        fun drop(): SamplingResult {
            return ImmutableSamplingResult.EMPTY_NOT_SAMPLED_OR_RECORDED_SAMPLING_RESULT
        }
    }
}
