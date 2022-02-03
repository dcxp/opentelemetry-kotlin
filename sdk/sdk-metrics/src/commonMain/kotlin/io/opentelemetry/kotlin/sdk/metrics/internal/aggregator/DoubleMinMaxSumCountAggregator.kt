/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.opentelemetry.kotlin.Supplier
import io.opentelemetry.kotlin.sdk.metrics.data.ExemplarData
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarReservoir
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.getAndUpdate
import kotlin.math.max
import kotlin.math.min

/**
 * Aggregator that produces summary metrics.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
internal class DoubleMinMaxSumCountAggregator(
    private val reservoirSupplier: Supplier<ExemplarReservoir>
) : AbstractMinMaxSumCountAggregator() {

    override fun createHandle(): AggregatorHandle<MinMaxSumCountAccumulation> {
        return Handle(reservoirSupplier.get())
    }

    internal class Handle(exemplarReservoir: ExemplarReservoir) :
        AggregatorHandle<MinMaxSumCountAccumulation>(exemplarReservoir) {

        // The current value. This controls its own internal thread-safety via method access. Don't
        // try to use its fields directly.
        private val current = atomic(DoubleState())

        override fun doAccumulateThenReset(
            exemplars: List<ExemplarData>
        ): MinMaxSumCountAccumulation {
            val currentState = current.getAndUpdate { it.reset() }
            val toReturn: MinMaxSumCountAccumulation =
                MinMaxSumCountAccumulation.create(
                    currentState.count,
                    currentState.sum,
                    currentState.min,
                    currentState.max
                )

            return toReturn
        }

        override fun doRecordDouble(value: Double) {
            current.getAndUpdate { it.record(value) }
        }

        private data class DoubleState(
            var count: Long = 0,
            var sum: Double = 0.0,
            var min: Double = Double.POSITIVE_INFINITY,
            var max: Double = Double.NEGATIVE_INFINITY,
        ) {
            fun reset(): DoubleState {
                return DoubleState()
            }

            fun record(value: Double): DoubleState {
                return this.copy(
                    count = ++count,
                    sum = sum + value,
                    min = min(value, min),
                    max = max(value, max)
                )
            }
        }
    }
}
