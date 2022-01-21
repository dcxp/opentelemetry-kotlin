/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.sdk.metrics.export
/*
import java.util.concurrent.ScheduledExecutorService

internal class PeriodicMetricReaderFactory(
    exporter: io.opentelemetry.sdk.metrics.export.MetricExporter,
    scheduleDelayNanos: Long,
    scheduler: ScheduledExecutorService?
) : io.opentelemetry.sdk.metrics.export.MetricReaderFactory {
    private val exporter: io.opentelemetry.sdk.metrics.export.MetricExporter
    private val intervalNanos: Long
    private val scheduler: ScheduledExecutorService?
    init {
        this.exporter = exporter
        intervalNanos = scheduleDelayNanos
        this.scheduler = scheduler
    }

    override fun apply(
        producer: io.opentelemetry.sdk.metrics.export.MetricProducer
    ): io.opentelemetry.sdk.metrics.export.MetricReader {
        val result: io.opentelemetry.sdk.metrics.export.PeriodicMetricReader =
            io.opentelemetry.sdk.metrics.export.PeriodicMetricReader(producer, exporter, scheduler)
        // TODO - allow a different start delay.
        result.start(intervalNanos)
        return result
    }
}
*/
