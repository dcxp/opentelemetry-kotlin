/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

import io.opentelemetry.kotlin.api.common.Attributes

/**
 * A point in the "Metric stream" data model.
 *
 * This is distinguished from `Measurement` in that it may have aggregated data, and has its type
 * defined by the metric data model (no longer an instrument).
 */
interface PointData {
    /**
     * Returns the start epoch timestamp in nanos of this `Instrument`, usually the time when the
     * metric was created or an aggregation was enabled.
     *
     * @return the start epoch timestamp in nanos.
     */
    val startEpochNanos: Long

    /**
     * Returns the epoch timestamp in nanos when data were collected, usually it represents the
     * moment when `Instrument.getData()` was called.
     *
     * @return the epoch timestamp in nanos.
     */
    val epochNanos: Long

    /**
     * Returns the attributes associated with this `Point`.
     *
     * @return the attributes associated with this `Point`.
     */
    val attributes: Attributes

    /** List of exemplars collected from measurements that were used to form the data point. */
    val exemplars: List<ExemplarData>
}
