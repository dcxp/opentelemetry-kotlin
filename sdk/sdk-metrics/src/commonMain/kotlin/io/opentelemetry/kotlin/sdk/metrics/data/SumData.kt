/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

/**
 * A sum metric point.
 *
 * See:
 * https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/metrics/datamodel.md#sums
 */
interface SumData<T : PointData> : Data<T> {
    /**
     * Returns "true" if the sum is monotonic.
     *
     * @return "true" if the sum is monotonic
     */
    val isMonotonic: Boolean

    /**
     * Returns the `AggregationTemporality` of this metric,
     *
     * AggregationTemporality describes if the aggregator reports delta changes since last report
     * time, or cumulative changes since a fixed start time.
     *
     * @return the `AggregationTemporality` of this metric
     */
    val aggregationTemporality: AggregationTemporality
}
