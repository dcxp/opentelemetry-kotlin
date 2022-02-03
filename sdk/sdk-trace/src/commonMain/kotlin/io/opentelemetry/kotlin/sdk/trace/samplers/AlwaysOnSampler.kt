/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.samplers

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.trace.SpanKind
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.trace.data.LinkData

internal enum class AlwaysOnSampler : io.opentelemetry.kotlin.sdk.trace.samplers.Sampler {
    INSTANCE;

    // Returns a "yes" {@link SamplingResult} on {@link Span} sampling.
    override fun shouldSample(
        parentContext: Context,
        traceId: String,
        name: String,
        spanKind: SpanKind,
        attributes: Attributes,
        parentLinks: List<LinkData>
    ): SamplingResult {
        return ImmutableSamplingResult.EMPTY_RECORDED_AND_SAMPLED_SAMPLING_RESULT
    }

    override val description: String
        get() = "AlwaysOnSampler"

    override fun toString(): String {
        return description
    }
}
