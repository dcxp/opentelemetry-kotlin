/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

/**
 * ExponentialHistogramBuckets represents either the positive or negative measurements taken for a
 * [ExponentialHistogramPointData].
 *
 * The bucket boundaries are lower-bound inclusive, and are calculated using the [ ]
 * [ExponentialHistogramPointData.getScale] and the [.getOffset].
 *
 * For example, assume [ExponentialHistogramPointData.getScale] is 0, the base is 2.0. Then, if
 * `offset` is 0, the bucket lower bounds would be 1.0, 2.0, 4.0, 8.0, etc. If `offset` is -3, the
 * bucket lower bounds would be 0.125, 0.25, 0.5, 1.0, 2,0, etc. If `offset` is +3, the bucket lower
 * bounds would be 8.0, 16.0, 32.0, etc.
 */
interface ExponentialHistogramBuckets {
    /**
     * The offset shifts the bucket boundaries according to `lower_bound = base^(offset+i). ` * .
     *
     * @return the offset.
     */
    val offset: Int

    /**
     * The bucket counts is a of counts representing number of measurements that fall into each
     * bucket.
     *
     * @return the bucket counts.
     */
    val bucketCounts: List<Long>

    /**
     * The total count is the sum of all the values in the buckets.
     *
     * @return the total count.
     */
    val totalCount: Long
}
