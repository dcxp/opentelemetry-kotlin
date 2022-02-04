/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.samplers

import io.opentelemetry.kotlin.api.common.Attributes

internal abstract class ImmutableSamplingResult : SamplingResult {
    abstract override val decision: SamplingDecision
    abstract override val attributes: Attributes

    companion object {
        val EMPTY_RECORDED_AND_SAMPLED_SAMPLING_RESULT: SamplingResult =
            createWithoutAttributes(SamplingDecision.RECORD_AND_SAMPLE)
        val EMPTY_NOT_SAMPLED_OR_RECORDED_SAMPLING_RESULT: SamplingResult =
            createWithoutAttributes(SamplingDecision.DROP)
        val EMPTY_RECORDED_SAMPLING_RESULT: SamplingResult =
            createWithoutAttributes(SamplingDecision.RECORD_ONLY)

        fun createSamplingResult(
            decision: SamplingDecision,
            attributes: Attributes
        ): SamplingResult {
            return Instance(decision, attributes)
        }

        private fun createWithoutAttributes(decision: SamplingDecision): SamplingResult {
            return Instance(decision, Attributes.empty())
        }

        private class Instance(
            override val decision: SamplingDecision,
            override val attributes: Attributes
        ) : ImmutableSamplingResult()
    }
}
