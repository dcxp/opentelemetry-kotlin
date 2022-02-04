/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

import io.opentelemetry.kotlin.api.common.Attributes

/**
 * SummaryPoint is a single data point that summarizes the values in a time series of numeric
 * values.
 */
interface DoubleSummaryPointData : PointData {
    /**
     * The number of values that are being summarized.
     *
     * @return the number of values that are being summarized.
     */
    val count: Long

    /**
     * The sum of all the values that are being summarized.
     *
     * @return the sum of the values that are being summarized.
     */
    val sum: Double

    /**
     * Percentile values in the summarization. Note: a percentile 0.0 represents the minimum value
     * in the distribution.
     *
     * @return the percentiles values.
     */
    val percentileValues: List<ValueAtPercentile>

    companion object {
        /**
         * Creates a [DoubleSummaryPointData].
         *
         * @param startEpochNanos (optional) The starting time for the period where this point was
         * sampled.
         * @param epochNanos The ending time for the period when this value was sampled.
         * @param attributes The set of attributes associated with this point.
         * @param count The number of measurements being sumarized.
         * @param sum The sum of measuremnts being sumarized.
         * @param percentileValues Calculations of percentile values from measurements.
         */
        fun create(
            startEpochNanos: Long,
            epochNanos: Long,
            attributes: Attributes,
            count: Long,
            sum: Double,
            percentileValues: List<ValueAtPercentile>
        ): DoubleSummaryPointData {
            return Implementation(
                startEpochNanos,
                epochNanos,
                attributes,
                emptyList(),
                count,
                sum,
                percentileValues
            )
        }

        data class Implementation(
            override val startEpochNanos: Long,
            override val epochNanos: Long,
            override val attributes: Attributes,
            override val exemplars: List<ExemplarData>,
            override val count: Long,
            override val sum: Double,
            override val percentileValues: List<ValueAtPercentile>,
        ) : DoubleSummaryPointData
    }
}
