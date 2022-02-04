/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.view

import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentValueType
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarFilter
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarReservoir
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.Aggregator
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.DoubleLastValueAggregator
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.LongLastValueAggregator
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor

/** Last-value aggregation configuration. */
internal class LastValueAggregation private constructor() :
    io.opentelemetry.kotlin.sdk.metrics.view.Aggregation() {
    override fun <T> createAggregator(
        instrumentDescriptor: InstrumentDescriptor,
        exemplarFilter: ExemplarFilter
    ): Aggregator<T> {

        // For the initial version we do not sample exemplars on gauges.
        return when (instrumentDescriptor.valueType) {
            InstrumentValueType.LONG ->
                LongLastValueAggregator(ExemplarReservoir::noSamples) as Aggregator<T>
            InstrumentValueType.DOUBLE ->
                DoubleLastValueAggregator(ExemplarReservoir::noSamples) as Aggregator<T>
            else -> throw IllegalArgumentException("Invalid instrument value type")
        }
    }

    override fun toString(): String {
        return "LastValueAggregation"
    }

    companion object {
        val INSTANCE: Aggregation = LastValueAggregation()
    }
}
