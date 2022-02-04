/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.export

import kotlin.time.Duration

internal class PeriodicMetricReaderFactory(
    private val exporter: MetricExporter,
    private val scheduleDelay: Duration
) : MetricReaderFactory {
    override fun apply(producer: MetricProducer): MetricReader {
        val result = PeriodicMetricReader(producer, exporter)
        // TODO - allow a different start delay.
        result.start(scheduleDelay)
        return result
    }
}
