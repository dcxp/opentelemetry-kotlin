/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.exemplar

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.trace.Span
import io.opentelemetry.kotlin.context.Context

/** Exemplar sampler that only samples measurements with associated sampled traces. */
internal class WithTraceExemplarFilter private constructor() :
    io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarFilter {
    override fun shouldSampleMeasurement(
        value: Long,
        attributes: Attributes,
        context: Context
    ): Boolean {
        return hasSampledTrace(context)
    }

    override fun shouldSampleMeasurement(
        value: Double,
        attributes: Attributes,
        context: Context
    ): Boolean {
        return hasSampledTrace(context)
    }

    companion object {
        val INSTANCE: io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarFilter =
            WithTraceExemplarFilter()
        private fun hasSampledTrace(context: Context): Boolean {
            return Span.fromContext(context).spanContext.isSampled()
        }
    }
}
