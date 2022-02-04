/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

import io.opentelemetry.kotlin.api.common.Attributes

/**
 * A sample input measurement.
 *
 * Exemplars also hold information about the environment when the measurement was recorded, for
 * example the span and trace ID of the active span when the exemplar was recorded.
 */
interface ExemplarData {
    /**
     * The set of key/value pairs that were filtered out by the aggregator, but recorded alongside
     * the original measurement. Only key/value pairs that were filtered out by the aggregator
     * should be included
     */
    val filteredAttributes: Attributes

    /** Returns the timestamp in nanos when measurement was collected. */
    val epochNanos: Long

    /**
     * (Optional) Span ID of the exemplar trace.
     *
     * Span ID may be `null` if the measurement is not recorded inside a trace or the trace was not
     * sampled.
     */
    val spanId: String?

    /**
     * (Optional) Trace ID of the exemplar trace.
     *
     * Trace ID may be `null` if the measurement is not recorded inside a trace or if the trace is
     * not sampled.
     */
    val traceId: String?

    /**
     * Coerces this exemplar to a double value.
     *
     * Note: This could create a loss of precision from `long` measurements.
     */
    val valueAsDouble: Double
}
