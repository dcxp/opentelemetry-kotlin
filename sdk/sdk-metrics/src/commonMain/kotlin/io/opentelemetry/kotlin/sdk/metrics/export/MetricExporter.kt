/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.export

import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData

/**
 * `MetricExporter` is the interface that all "push based" metric libraries should use to export
 * metrics to the OpenTelemetry exporters.
 *
 * All OpenTelemetry exporters should allow access to a `MetricExporter` instance.
 */
interface MetricExporter {
    /** Returns the set of all supported temporalities for this exporter. */
    val supportedTemporality: Set<AggregationTemporality>
        get() = AggregationTemporality.values().toSet()

    /** Returns the preferred temporality for metrics. */
    val preferredTemporality: AggregationTemporality?
        get() = null

    /**
     * Exports the collection of given [MetricData]. Note that export operations can be performed
     * simultaneously depending on the type of metric reader being used. However, the caller MUST
     * ensure that only one export can occur at a time.
     *
     * @param metrics the collection of [MetricData] to be exported.
     * @return the result of the export, which is often an asynchronous operation.
     */
    fun export(metrics: Collection<MetricData>): CompletableResultCode

    /**
     * Exports the collection of [MetricData] that have not yet been exported. Note that flush
     * operations can be performed simultaneously depending on the type of metric reader being used.
     * However, the caller MUST ensure that only one export can occur at a time.
     *
     * @return the result of the flush, which is often an asynchronous operation.
     */
    fun flush(): CompletableResultCode

    /**
     * Called when the associated IntervalMetricReader is shutdown.
     *
     * @return a [CompletableResultCode] which is completed when shutdown completes.
     */
    fun shutdown(): CompletableResultCode
}
