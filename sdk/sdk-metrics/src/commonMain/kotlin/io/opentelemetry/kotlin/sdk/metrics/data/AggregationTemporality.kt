/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

/** An enumeration which describes the time period over which metrics should be aggregated. */
enum class AggregationTemporality {
    /** Metrics will be aggregated only over the most recent collection interval. */
    DELTA,

    /** Metrics will be aggregated over the lifetime of the associated Instrument. */
    CUMULATIVE
}
