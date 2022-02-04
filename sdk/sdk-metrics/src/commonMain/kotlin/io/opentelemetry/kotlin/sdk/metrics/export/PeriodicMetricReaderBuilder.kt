/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.export

import io.opentelemetry.kotlin.api.common.normalizeToNanos
import kotlinx.atomicfu.atomic
import kotlinx.datetime.DateTimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds

/** A builder for [PeriodicMetricReader]. */
class PeriodicMetricReaderBuilder constructor(private val metricExporter: MetricExporter) {
    private val interval = atomic(DEFAULT_SCHEDULE_DELAY_MINUTES.minutes)
    /**
     * Sets the interval of reads. If unset, defaults to {@value DEFAULT_SCHEDULE_DELAY_MINUTES}min.
     */
    fun setInterval(interval: Long, unit: DateTimeUnit): PeriodicMetricReaderBuilder {
        require(interval > 0) { "interval must be positive" }
        return setInterval(unit.normalizeToNanos(interval).nanoseconds)
    }

    /**
     * Sets the interval of reads. If unset, defaults to {@value DEFAULT_SCHEDULE_DELAY_MINUTES}min.
     */
    fun setInterval(interval: Duration): PeriodicMetricReaderBuilder {
        require(interval.isPositive()) { "interval must be positive" }
        this.interval.lazySet(interval)
        return this
    }

    /**
     * Returns a new [MetricReaderFactory] with the configuration of this builder which can be
     * registered with a [io.opentelemetry.kotlin.sdk.metrics.SdkMeterProvider].
     */
    fun newMetricReaderFactory(): MetricReaderFactory {
        return PeriodicMetricReaderFactory(
            metricExporter,
            interval.value,
        )
    }

    companion object {
        const val DEFAULT_SCHEDULE_DELAY_MINUTES: Long = 1
    }
}
