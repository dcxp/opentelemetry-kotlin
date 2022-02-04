/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

import io.opentelemetry.kotlin.api.common.Attributes

/**
 * ExponentialHistogramPointData represents an approximate distribution of measurements across
 * exponentially increasing bucket boundaries, taken for a [ExponentialHistogramData]. It contains
 * the necessary information to calculate bucket boundaries and perform aggregation.
 *
 * The bucket boundaries are calculated using both the scale [.getScale], and the offset
 * [ExponentialHistogramBuckets.getOffset].
 *
 * See:
 * https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/metrics/datamodel.md#exponentialhistogram
 */
interface ExponentialHistogramPointData : PointData {
    /**
     * Scale characterises the resolution of the histogram, with larger values of scale offering
     * greater precision. Bucket boundaries of the histogram are located at integer powers of the
     * base, where `base = Math.pow(2, Math.pow(2, -scale))`.
     *
     * @return the scale.
     */
    val scale: Int

    /**
     * Returns the sum of all measurements in the data point. The sum should be disregarded if there
     * are both positive and negative measurements.
     *
     * @return the sum of all measurements in this data point.
     */
    val sum: Double

    /**
     * Returns the number of measurements taken for this data point, including the positive bucket
     * counts, negative bucket counts, and the zero count.
     *
     * @return the number of measurements in this data point.
     */
    val count: Long

    /**
     * Returns the number of measurements equal to zero in this data point.
     *
     * @return the number of values equal to zero.
     */
    val zeroCount: Long

    /**
     * Return the [ExponentialHistogramBuckets] representing the positive measurements taken for
     * this histogram.
     *
     * @return the positive buckets.
     */
    val positiveBuckets: io.opentelemetry.kotlin.sdk.metrics.data.ExponentialHistogramBuckets?

    /**
     * Return the [ExponentialHistogramBuckets] representing the negative measurements taken for
     * this histogram.
     *
     * @return the negative buckets.
     */
    val negativeBuckets: io.opentelemetry.kotlin.sdk.metrics.data.ExponentialHistogramBuckets?

    companion object {
        /**
         * Create an ExponentialHistogramPointData.
         *
         * @return an ExponentialHistogramPointData.
         */
        fun create(
            scale: Int,
            sum: Double,
            zeroCount: Long,
            positiveBuckets: ExponentialHistogramBuckets,
            negativeBuckets: ExponentialHistogramBuckets,
            startEpochNanos: Long,
            epochNanos: Long,
            attributes: Attributes,
            exemplars: List<ExemplarData>
        ): ExponentialHistogramPointData {
            return DoubleExponentialHistogramPointData.create(
                scale,
                sum,
                zeroCount,
                positiveBuckets,
                negativeBuckets,
                startEpochNanos,
                epochNanos,
                attributes,
                exemplars
            )
        }
    }
}
