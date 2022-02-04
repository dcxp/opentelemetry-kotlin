/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.samplers

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.internal.OtelEncodingUtils
import io.opentelemetry.kotlin.api.trace.SpanKind
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.trace.data.LinkData
import kotlin.math.abs

/**
 * We assume the lower 64 bits of the traceId's are randomly distributed around the whole (long)
 * range. We convert an incoming probability into an upper bound on that value, such that we can
 * just compare the absolute value of the id and the bound to see if we are within the desired
 * probability range. Using the low bits of the traceId also ensures that systems that only use 64
 * bit ID's will also work with this sampler.
 */
internal class TraceIdRatioBasedSampler(
    ratio: Double, // Visible for testing
    val idUpperBound: Long
) : Sampler {
    override val description: String = "TraceIdRatioBased$ratio"

    override fun shouldSample(
        parentContext: Context,
        traceId: String,
        name: String,
        spanKind: SpanKind,
        attributes: Attributes,
        parentLinks: List<LinkData>
    ): SamplingResult {
        // Always sample if we are within probability range. This is true even for child spans (that
        // may have had a different sampling samplingResult made) to allow for different sampling
        // policies,
        // and dynamic increases to sampling probabilities for debugging purposes.
        // Note use of '<' for comparison. This ensures that we never sample for probability == 0.0,
        // while allowing for a (very) small chance of *not* sampling if the id == Long.MAX_VALUE.
        // This is considered a reasonable tradeoff for the simplicity/performance requirements
        // (this
        // code is executed in-line for every Span creation).
        return if (abs(getTraceIdRandomPart(traceId)) < idUpperBound) POSITIVE_SAMPLING_RESULT
        else NEGATIVE_SAMPLING_RESULT
    }

    override fun equals(other: Any?): Boolean {
        if (other !is TraceIdRatioBasedSampler) {
            return false
        }
        return idUpperBound == other.idUpperBound
    }

    override fun hashCode(): Int {
        return idUpperBound.hashCode()
    }

    override fun toString(): String {
        return description
    }

    companion object {
        private val POSITIVE_SAMPLING_RESULT: SamplingResult = SamplingResult.recordAndSample()
        private val NEGATIVE_SAMPLING_RESULT: SamplingResult = SamplingResult.drop()

        fun create(ratio: Double): TraceIdRatioBasedSampler {
            require(!(ratio < 0.0 || ratio > 1.0)) { "ratio must be in range [0.0, 1.0]" }
            val idUpperBound: Long
            // Special case the limits, to avoid any possible issues with lack of precision across
            // double/long boundaries. For probability == 0.0, we use Long.MIN_VALUE as this
            // guarantees
            // that we will never sample a trace, even in the case where the id == Long.MIN_VALUE,
            // since
            // Math.Abs(Long.MIN_VALUE) == Long.MIN_VALUE.
            idUpperBound =
                if (ratio == 0.0) {
                    Long.MIN_VALUE
                } else if (ratio == 1.0) {
                    Long.MAX_VALUE
                } else {
                    (ratio * Long.MAX_VALUE).toLong()
                }
            return TraceIdRatioBasedSampler(ratio, idUpperBound)
        }

        private fun getTraceIdRandomPart(traceId: String): Long {
            return OtelEncodingUtils.longFromBase16String(traceId, 16)
        }
    }
}
