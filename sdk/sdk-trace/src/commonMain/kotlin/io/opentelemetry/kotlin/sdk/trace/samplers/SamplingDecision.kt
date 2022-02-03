/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.samplers

/** A decision on whether a span should be recorded, recorded and sampled or dropped. */
enum class SamplingDecision {
    /** Span is dropped. The resulting span will be completely no-op. */
    DROP,

    /**
     * Span is recorded only. The resulting span will record all information like timings and
     * attributes but will not be exported. Downstream [ parent-based][Sampler.parentBased] samplers
     * will not sample the span.
     */
    RECORD_ONLY,

    /**
     * Span is recorded and sampled. The resulting span will record all information like timings and
     * attributes and will be exported.
     */
    RECORD_AND_SAMPLE
}
