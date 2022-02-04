/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

import io.opentelemetry.kotlin.api.common.Attributes

/**
 * DoubleHistogramPointData represents an approximate representation of the distribution of
 * measurements.
 */
interface DoubleHistogramPointData : PointData {
    /**
     * The sum of all measurements recorded.
     *
     * @return the sum of recorded measurements.
     */
    val sum: Double

    /**
     * The number of measurements taken.
     *
     * @return the count of recorded measurements.
     */
    val count: Long

    /**
     * The bucket boundaries. For a Histogram with N defined boundaries, e.g, [x, y, z]. There are
     * N+1 counts: (-inf, x], (x, y], (y, z], (z, +inf).
     *
     * @return the read-only bucket boundaries in increasing order. **do not mutate** the returned
     * object.
     */
    val boundaries: List<Double>

    /**
     * The counts in each bucket.
     *
     * @return the read-only counts in each bucket. **do not mutate** the returned object.
     */
    val counts: List<Long>

    /**
     * Returns the lower bound of a bucket (all values would have been greater than).
     *
     * @param bucketIndex The bucket index, should match [.getCounts] index.
     */
    fun getBucketLowerBound(bucketIndex: Int): Double {
        return if (bucketIndex > 0) boundaries[bucketIndex - 1] else Double.NEGATIVE_INFINITY
    }

    /**
     * Returns the upper inclusive bound of a bucket (all values would have been less then or
     * equal).
     *
     * @param bucketIndex The bucket index, should match [.getCounts] index.
     */
    fun getBucketUpperBound(bucketIndex: Int): Double {
        return if (bucketIndex < boundaries.size) boundaries[bucketIndex]
        else Double.POSITIVE_INFINITY
    }

    companion object {
        /**
         * Creates a DoubleHistogramPointData. For a Histogram with N defined boundaries, there
         * should be N+1 counts.
         *
         * @return a DoubleHistogramPointData.
         * @throws IllegalArgumentException if the given boundaries/counts were invalid
         */
        /**
         * Creates a DoubleHistogramPointData. For a Histogram with N defined boundaries, there
         * should be N+1 counts.
         *
         * @return a DoubleHistogramPointData.
         * @throws IllegalArgumentException if the given boundaries/counts were invalid
         */
        fun create(
            startEpochNanos: Long,
            epochNanos: Long,
            attributes: Attributes,
            sum: Double,
            boundaries: List<Double>,
            counts: List<Long>,
            exemplars: List<ExemplarData> = emptyList()
        ): DoubleHistogramPointData {
            require(counts.size == boundaries.size + 1) {
                ("invalid counts: size should be " +
                    (boundaries.size + 1) +
                    " instead of " +
                    counts.size)
            }
            require(isStrictlyIncreasing(boundaries)) { "invalid boundaries: $boundaries" }
            require(
                !(!boundaries.isEmpty() &&
                    (boundaries[0].isInfinite() || boundaries[boundaries.size - 1].isInfinite()))
            ) { "invalid boundaries: contains explicit +/-Inf" }
            var totalCount: Long = 0
            for (c in counts) {
                totalCount += c
            }
            return Implementation(
                startEpochNanos,
                epochNanos,
                attributes,
                exemplars,
                sum,
                totalCount,
                boundaries,
                counts
            )
        }

        private fun isStrictlyIncreasing(xs: List<Double>): Boolean {
            for (i in 0 until xs.size - 1) {
                if (xs[i].compareTo(xs[i + 1]) >= 0) {
                    return false
                }
            }
            return true
        }

        data class Implementation(
            override val startEpochNanos: Long,
            override val epochNanos: Long,
            override val attributes: Attributes,
            override val exemplars: List<ExemplarData>,
            override val sum: Double,
            override val count: Long,
            override val boundaries: List<Double>,
            override val counts: List<Long>,
        ) : DoubleHistogramPointData
    }
}
