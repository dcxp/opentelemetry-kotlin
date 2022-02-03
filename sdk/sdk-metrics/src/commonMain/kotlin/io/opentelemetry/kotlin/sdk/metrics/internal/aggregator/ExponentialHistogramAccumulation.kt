/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.opentelemetry.kotlin.sdk.metrics.data.ExemplarData

internal interface ExponentialHistogramAccumulation {
    val scale: Int
    val sum: Double
    val positiveBuckets: DoubleExponentialHistogramBuckets
    val negativeBuckets: DoubleExponentialHistogramBuckets
    val zeroCount: Long
    val exemplars: List<ExemplarData>

    companion object {
        /**
         * Creates a new [ExponentialHistogramAccumulation] with the given values.
         *
         * @param scale the scale of the exponential histogram.
         * @param sum the sum of all the recordings of the histogram.
         * @param positiveBuckets the buckets counting positive recordings.
         * @param negativeBuckets the buckets coutning negative recordings.
         * @param zeroCount The amount of time zero was recorded.
         * @param exemplars The exemplars.
         * @return a new [ExponentialHistogramAccumulation] with the given values.
         */
        fun create(
            scale: Int,
            sum: Double,
            positiveBuckets: DoubleExponentialHistogramBuckets,
            negativeBuckets: DoubleExponentialHistogramBuckets,
            zeroCount: Long,
            exemplars: List<ExemplarData>
        ): ExponentialHistogramAccumulation {
            return Implementation(
                scale,
                sum,
                positiveBuckets,
                negativeBuckets,
                zeroCount,
                exemplars
            )
        }

        class Implementation(
            override val scale: Int,
            override val sum: Double,
            override val positiveBuckets: DoubleExponentialHistogramBuckets,
            override val negativeBuckets: DoubleExponentialHistogramBuckets,
            override val zeroCount: Long,
            override val exemplars: List<ExemplarData>
        ) : ExponentialHistogramAccumulation
    }
}
