/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.sdk.metrics.exemplar

import io.opentelemetry.api.common.Attributes
import io.opentelemetry.context.Context

internal class NeverSampleFilter private constructor() : ExemplarFilter {
    override fun shouldSampleMeasurement(
        value: Long,
        attributes: Attributes,
        context: Context
    ): Boolean {
        return false
    }

    override fun shouldSampleMeasurement(
        value: Double,
        attributes: Attributes,
        context: Context
    ): Boolean {
        return false
    }

    companion object {
        val INSTANCE: io.opentelemetry.sdk.metrics.exemplar.ExemplarFilter = NeverSampleFilter()
    }
}
