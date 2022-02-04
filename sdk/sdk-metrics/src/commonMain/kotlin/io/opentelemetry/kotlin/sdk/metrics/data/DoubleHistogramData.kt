/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

/**
 * A histogram metric point.
 *
 * See:
 * https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/metrics/datamodel.md#histogram
 *
 * *Note: This is called "DoubleHistogram" to reflect which primitives are used to record it,
 * however "Histogram" is the equivalent OTLP type.*
 */
interface DoubleHistogramData : Data<DoubleHistogramPointData> {
    /**
     * Returns the `AggregationTemporality` of this metric,
     *
     * AggregationTemporality describes if the aggregator reports delta changes since last report
     * time, or cumulative changes since a fixed start time.
     *
     * @return the `AggregationTemporality` of this metric
     */
    val aggregationTemporality: AggregationTemporality

    companion object {
        val EMPTY = create(AggregationTemporality.CUMULATIVE, emptyList())

        fun create(
            temporality: AggregationTemporality,
            points: Collection<DoubleHistogramPointData>
        ): DoubleHistogramData {
            return Implementation(temporality, points)
        }

        data class Implementation(
            override val aggregationTemporality: AggregationTemporality,
            override val points: Collection<DoubleHistogramPointData>,
        ) : DoubleHistogramData
    }
}
