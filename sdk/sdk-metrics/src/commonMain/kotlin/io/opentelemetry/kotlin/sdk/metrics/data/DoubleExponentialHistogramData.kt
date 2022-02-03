/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

/**
 * A simple, autovalue implementation of [ExponentialHistogramData]. For more detailed javadoc on
 * the type, see [ExponentialHistogramData].
 *
 * See:
 * https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/metrics/datamodel.md#exponentialhistogram
 */
interface DoubleExponentialHistogramData : ExponentialHistogramData {

    companion object {
        val EMPTY: DoubleExponentialHistogramData =
            create(AggregationTemporality.CUMULATIVE, listOf())

        /**
         * Create a DoubleExponentialHistogramData.
         *
         * @return a DoubleExponentialHistogramData
         */
        fun create(
            temporality: AggregationTemporality,
            points: Collection<ExponentialHistogramPointData>
        ): DoubleExponentialHistogramData {
            return Implementation(temporality, points)
        }

        data class Implementation(
            override val aggregationTemporality: AggregationTemporality,
            override val points: Collection<ExponentialHistogramPointData>
        ) : DoubleExponentialHistogramData
    }
}
