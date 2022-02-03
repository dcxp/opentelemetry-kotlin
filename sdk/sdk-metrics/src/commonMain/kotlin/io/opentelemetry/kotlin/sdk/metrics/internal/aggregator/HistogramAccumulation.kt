/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.opentelemetry.kotlin.sdk.metrics.data.ExemplarData

interface HistogramAccumulation {
    /**
     * The sum of all measurements recorded.
     *
     * @return the sum of recorded measurements.
     */
    val sum: Double

    /**
     * The counts in each bucket. The returned type is a mutable object, but it should be fine
     * because the class is only used internally.
     *
     * @return the counts in each bucket. **do not mutate** the returned object.
     */
    val counts: LongArray

    /** Exemplars accumulated during this period. */
    val exemplars: List<ExemplarData>

    companion object {
        /**
         * Creates a new [HistogramAccumulation] with the given values. Assume `counts` is read-only
         * so we don't need a defensive-copy here.
         *
         * @return a new [HistogramAccumulation] with the given values.
         */
        fun create(
            sum: Double,
            counts: LongArray,
            exemplars: List<ExemplarData> = emptyList()
        ): HistogramAccumulation {
            return Implementation(sum, counts, exemplars)
        }
        data class Implementation(
            override val sum: Double,
            override val counts: LongArray,
            override val exemplars: List<ExemplarData>
        ) : HistogramAccumulation {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || this::class != other::class) return false

                other as Implementation

                if (sum != other.sum) return false
                if (!counts.contentEquals(other.counts)) return false
                if (exemplars != other.exemplars) return false

                return true
            }

            override fun hashCode(): Int {
                var result = sum.hashCode()
                result = 31 * result + counts.contentHashCode()
                result = 31 * result + exemplars.hashCode()
                return result
            }
        }
    }
}
