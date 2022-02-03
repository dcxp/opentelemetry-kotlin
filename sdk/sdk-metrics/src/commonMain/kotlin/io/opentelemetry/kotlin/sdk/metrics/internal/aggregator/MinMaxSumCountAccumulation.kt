/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.metrics.data.DoubleSummaryPointData
import io.opentelemetry.kotlin.sdk.metrics.data.ValueAtPercentile

interface MinMaxSumCountAccumulation {
    /**
     * Returns the count (number of measurements) stored by this accumulation.
     *
     * @return the count stored by this accumulation.
     */
    val count: Long

    /**
     * Returns the sum (sum of measurements) stored by this accumulation.
     *
     * @return the sum stored by this accumulation.
     */
    val sum: Double

    /**
     * Returns the min (minimum of all measurements) stored by this accumulation.
     *
     * @return the min stored by this accumulation.
     */
    val min: Double

    /**
     * Returns the max (maximum of all measurements) stored by this accumulation.
     *
     * @return the max stored by this accumulation.
     */
    val max: Double
    fun toPoint(
        startEpochNanos: Long,
        epochNanos: Long,
        labels: Attributes
    ): DoubleSummaryPointData {
        return DoubleSummaryPointData.create(
            startEpochNanos,
            epochNanos,
            labels,
            count,
            sum,
            listOf(ValueAtPercentile.create(0.0, min), ValueAtPercentile.create(100.0, max))
        )
    }

    companion object {
        /**
         * Creates a new [MinMaxSumCountAccumulation] with the given values.
         *
         * @param count the number of measurements.
         * @param sum the sum of the measurements.
         * @param min the min value out of all measurements.
         * @param max the max value out of all measurements.
         * @return a new [MinMaxSumCountAccumulation] with the given values.
         */
        fun create(count: Long, sum: Double, min: Double, max: Double): MinMaxSumCountAccumulation {
            return Implementation(count, sum, min, max)
        }
        data class Implementation(
            override val count: Long,
            override val sum: Double,
            override val min: Double,
            override val max: Double
        ) : MinMaxSumCountAccumulation
    }
}
