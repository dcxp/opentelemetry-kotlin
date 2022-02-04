/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.samplers

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.trace.SpanKind
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.trace.data.LinkData

internal enum class AlwaysOffSampler : Sampler {
    INSTANCE;

    // Returns a "no" {@link SamplingResult} on {@link Span} sampling.
    override fun shouldSample(
        parentContext: Context,
        traceId: String,
        name: String,
        spanKind: SpanKind,
        attributes: Attributes,
        parentLinks: List<LinkData>
    ): SamplingResult {
        return ImmutableSamplingResult.EMPTY_NOT_SAMPLED_OR_RECORDED_SAMPLING_RESULT
    }

    override val description: String
        get() = "AlwaysOffSampler"

    override fun toString(): String {
        return description
    }
}
