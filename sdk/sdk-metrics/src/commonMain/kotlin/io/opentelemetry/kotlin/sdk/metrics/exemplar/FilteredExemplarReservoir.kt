/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.exemplar

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.metrics.data.ExemplarData

/** Implementation of a reservoir that has a pre-filter on measurements. */
internal class FilteredExemplarReservoir(filter: ExemplarFilter, reservoir: ExemplarReservoir) :
    ExemplarReservoir {
    private val filter: ExemplarFilter
    private val reservoir: ExemplarReservoir

    init {
        this.filter = filter
        this.reservoir = reservoir
    }

    override fun offerMeasurement(value: Long, attributes: Attributes, context: Context) {
        if (filter.shouldSampleMeasurement(value, attributes, context)) {
            reservoir.offerMeasurement(value, attributes, context)
        }
    }

    override fun offerMeasurement(value: Double, attributes: Attributes, context: Context) {
        if (filter.shouldSampleMeasurement(value, attributes, context)) {
            reservoir.offerMeasurement(value, attributes, context)
        }
    }

    override fun collectAndReset(pointAttributes: Attributes): List<ExemplarData> {
        return reservoir.collectAndReset(pointAttributes)
    }
}
