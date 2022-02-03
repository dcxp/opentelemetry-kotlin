/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

/**
 * A collection of data points associated to a metric.
 *
 * Loosely equivalent with "Metric" message in OTLP. See:
 * https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/metrics/datamodel.md#metric-points
 */
interface Data<T : PointData> {
    /**
     * Returns the data [PointData]s for this metric.
     *
     * @return the data [PointData]s for this metric, or empty `Collection` if no points.
     */
    val points: Collection<T>
}
