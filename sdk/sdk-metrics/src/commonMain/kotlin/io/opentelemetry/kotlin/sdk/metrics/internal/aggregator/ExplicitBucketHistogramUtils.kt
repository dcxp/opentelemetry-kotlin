/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

/**
 * Utilities for interacting with explicit bucket histograms.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
object ExplicitBucketHistogramUtils {
    val DEFAULT_HISTOGRAM_BUCKET_BOUNDARIES: List<Double> =
        listOf(
            5.0,
            10.0,
            25.0,
            50.0,
            75.0,
            100.0,
            250.0,
            500.0,
            750.0,
            1000.0,
            2500.0,
            5000.0,
            7500.0,
            10000.0
        )

    /** Converts bucket boundary "convenient" configuration into the "more efficient" array. */
    fun createBoundaryArray(boundaries: List<Double>): DoubleArray {
        return validateBucketBoundaries(boundaries.toDoubleArray())
    }

    /**
     * Finds the bucket index for a histogram.
     *
     * @param boundaries the array of bucket boundaries.
     * @param value The current measurement value
     * @return The bucket index where the value should be recorded.
     */
    fun findBucketIndex(boundaries: DoubleArray, value: Double): Int {
        // Benchmark shows that linear search performs better than binary search with ordinary
        // buckets.
        for (i in boundaries.indices) {
            if (value <= boundaries[i]) {
                return i
            }
        }
        return boundaries.size
    }

    /**
     * Validates errors in boundary configuration.
     *
     * @param boundaries The array of bucket boundaries.
     * @return The original boundaries.
     * @throws IllegalArgumentException if boundaries are not specified correctly.
     */
    fun validateBucketBoundaries(boundaries: DoubleArray): DoubleArray {
        for (v in boundaries) {
            require(!v.isNaN()) { "invalid bucket boundary: NaN" }
        }
        for (i in 1 until boundaries.size) {
            require(boundaries[i - 1] < boundaries[i]) {
                ("Bucket boundaries must be in increasing order: " +
                    boundaries[i - 1] +
                    " >= " +
                    boundaries[i])
            }
        }
        if (boundaries.size > 0) {
            require(boundaries[0] != Double.NEGATIVE_INFINITY) { "invalid bucket boundary: -Inf" }
            require(boundaries[boundaries.size - 1] != Double.POSITIVE_INFINITY) {
                "invalid bucket boundary: +Inf"
            }
        }
        return boundaries
    }
}
