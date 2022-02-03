/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.exemplar

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context

class AlwaysSampleFilter : ExemplarFilter {
    override fun shouldSampleMeasurement(
        value: Long,
        attributes: Attributes,
        context: Context
    ): Boolean {
        return true
    }

    override fun shouldSampleMeasurement(
        value: Double,
        attributes: Attributes,
        context: Context
    ): Boolean {
        return true
    }

    companion object {
        val INSTANCE: ExemplarFilter = AlwaysSampleFilter()
    }
}
