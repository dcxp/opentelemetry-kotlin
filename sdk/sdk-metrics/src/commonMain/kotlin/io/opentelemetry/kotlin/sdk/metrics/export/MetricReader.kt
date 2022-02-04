/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.export

import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality

/**
 * A registered reader of metrics.
 *
 * This interface provides the [io.opentelemetry.kotlin.sdk.metrics.SdkMeterProvider] a mechanism of
 * global control over metrics during shutdown or memory pressure scenarios.
 */
interface MetricReader {
    /** Return The set of all supported temporalities for this exporter. */
    val supportedTemporality: Set<AggregationTemporality>

    /** Return The preferred temporality for metrics. */
    val preferredTemporality: AggregationTemporality?

    /**
     * Flushes metrics read by this reader.
     *
     * In all scenarios, the associated [MetricProducer] should have its [ ]
     * [MetricProducer.collectAllMetrics] method called.
     *
     * For push endpoints, this should collect and report metrics as normal.
     *
     * @return the result of the shutdown.
     */
    fun flush(): CompletableResultCode

    /**
     * Shuts down the metric reader.
     *
     * For pull endpoints, like prometheus, this should shut down the metric hosting endpoint or
     * server doing such a job.
     *
     * For push endpoints, this should shut down any scheduler threads.
     *
     * @return the result of the shutdown.
     */
    fun shutdown(): CompletableResultCode
}
