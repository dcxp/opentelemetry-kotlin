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
import kotlinx.atomicfu.update
import kotlin.math.max
import kotlin.math.min

/**
 * Aggregator that produces summary metrics.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
internal class LongMinMaxSumCountAggregator(
    private val reservoirSupplier: Supplier<ExemplarReservoir>
) : AbstractMinMaxSumCountAggregator() {

    override fun createHandle(): AggregatorHandle<MinMaxSumCountAccumulation> {
        return Handle(reservoirSupplier.get())
    }

    internal class Handle(exemplarReservoir: ExemplarReservoir) :
        AggregatorHandle<MinMaxSumCountAccumulation>(exemplarReservoir) {
        // The current value. This controls its own internal thread-safety via method access. Don't
        // try to use its fields directly.
        private val current = atomic(LongState())
        override fun doAccumulateThenReset(
            exemplars: List<ExemplarData>
        ): MinMaxSumCountAccumulation {
            val currentState = current.getAndUpdate { it.reset() }
            val toReturn: MinMaxSumCountAccumulation =
                MinMaxSumCountAccumulation.create(
                    currentState.count,
                    currentState.sum.toDouble(),
                    currentState.min.toDouble(),
                    currentState.max.toDouble()
                )
            return toReturn
        }

        override fun doRecordLong(value: Long) {
            current.update { it.record(value) }
        }

        private data class LongState(
            val sum: Long = 0,
            val count: Long = 0,
            val min: Long = Long.MAX_VALUE,
            val max: Long = Long.MIN_VALUE,
        ) {

            fun reset(): LongState {
                return LongState()
            }

            fun record(value: Long): LongState {
                return this.copy(
                    count = count + 1,
                    sum = sum + value,
                    min = min(value, min),
                    max = max(value, max)
                )
            }
        }
    }
}
