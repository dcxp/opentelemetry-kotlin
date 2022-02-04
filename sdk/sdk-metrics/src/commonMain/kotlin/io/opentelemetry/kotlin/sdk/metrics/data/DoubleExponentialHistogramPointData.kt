/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

import io.opentelemetry.kotlin.api.common.Attributes

/**
 * Simple auto value implementation of [ExponentialHistogramPointData]. For detailed javadoc on the
 * type, see [ExponentialHistogramPointData].
 */
interface DoubleExponentialHistogramPointData : ExponentialHistogramPointData {
    override val scale: Int
    override val sum: Double
    override val count: Long
    override val zeroCount: Long
    override val positiveBuckets: ExponentialHistogramBuckets
    override val negativeBuckets: ExponentialHistogramBuckets
    override val startEpochNanos: Long
    override val epochNanos: Long
    override val attributes: Attributes
    override val exemplars: List<ExemplarData>

    companion object {
        /**
         * Create a DoubleExponentialHistogramPointData.
         *
         * @return a DoubleExponentialHistogramPointData.
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
        ): DoubleExponentialHistogramPointData {
            val count: Long = zeroCount + positiveBuckets.totalCount + negativeBuckets.totalCount
            return Implementation(
                scale,
                sum,
                count,
                zeroCount,
                positiveBuckets,
                negativeBuckets,
                startEpochNanos,
                epochNanos,
                attributes,
                exemplars
            )
        }
        data class Implementation(
            override val scale: Int,
            override val sum: Double,
            override val count: Long,
            override val zeroCount: Long,
            override val positiveBuckets: ExponentialHistogramBuckets,
            override val negativeBuckets: ExponentialHistogramBuckets,
            override val startEpochNanos: Long,
            override val epochNanos: Long,
            override val attributes: Attributes,
            override val exemplars: List<ExemplarData>
        ) : DoubleExponentialHistogramPointData
    }
}
