/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

/**
 * A base-2 exponential histogram metric point, as defined by the OpenTelemetry Exponential
 * Histogram specification.
 *
 * See [ExponentialHistogramPointData] for more information.
 *
 * See:
 * https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/metrics/datamodel.md#exponentialhistogram
 *
 * *Note: This is called "ExponentialHistogramData" to reflect which primitives are used to record
 * it, however "ExponentialHistogram" is the equivalent OTLP type.*
 */
interface ExponentialHistogramData : Data<ExponentialHistogramPointData> {
    /**
     * Returns the `AggregationTemporality` of this metric.
     *
     * AggregationTemporality describes if the aggregator reports delta changes since last report
     * time, or cumulative changes since a fixed start time.
     *
     * @return the `AggregationTemporality` of this metric
     */
    val aggregationTemporality: io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality

    /**
     * Returns the collection of [ExponentialHistogramPointData] for this histogram.
     *
     * @return the collection of data points for this histogram.
     */
    override val points: Collection<ExponentialHistogramPointData>

    companion object {
        /**
         * Create a DoubleExponentialHistogramData.
         *
         * @return a DoubleExponentialHistogramData
         */
        fun create(
            temporality: AggregationTemporality,
            points: Collection<ExponentialHistogramPointData>
        ): ExponentialHistogramData {
            return DoubleExponentialHistogramData.create(temporality, points)
        }
    }
}
