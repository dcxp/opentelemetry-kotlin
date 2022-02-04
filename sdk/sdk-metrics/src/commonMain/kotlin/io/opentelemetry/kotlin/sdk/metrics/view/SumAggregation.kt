/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.view

import io.opentelemetry.kotlin.Supplier
import io.opentelemetry.kotlin.sdk.common.Clock
import io.opentelemetry.kotlin.sdk.internal.RandomSupplier
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentValueType
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarFilter
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarReservoir
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.Aggregator
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.DoubleSumAggregator
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.LongSumAggregator
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor

/** A sum aggregation configuration. */
internal class SumAggregation private constructor() :
    io.opentelemetry.kotlin.sdk.metrics.view.Aggregation() {
    @Suppress("UNCHECKED_CAST")
    override fun <T> createAggregator(
        instrumentDescriptor: InstrumentDescriptor,
        exemplarFilter: ExemplarFilter
    ): Aggregator<T> {
        val reservoirFactory: Supplier<ExemplarReservoir> =
            Supplier<ExemplarReservoir> {
                ExemplarReservoir.filtered(
                    exemplarFilter,
                    ExemplarReservoir.fixedSizeReservoir(
                        Clock.default,
                        // TODO find way of getting number of CPU cores
                        4,
                        RandomSupplier.platformDefault()
                    )
                )
            }
        return when (instrumentDescriptor.valueType) {
            InstrumentValueType.LONG ->
                LongSumAggregator(instrumentDescriptor, reservoirFactory) as Aggregator<T>
            InstrumentValueType.DOUBLE ->
                DoubleSumAggregator(instrumentDescriptor, reservoirFactory) as Aggregator<T>
            else -> throw IllegalArgumentException("Invalid instrument value type")
        }
    }

    override fun toString(): String {
        return "SumAggregation"
    }

    companion object {
        val DEFAULT = SumAggregation()
    }
}
