/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.sdk.metrics.export
/*
import io.opentelemetry.api.internal.Utils.checkArgument

/** A builder for [PeriodicMetricReader]. */
class PeriodicMetricReaderBuilder
internal constructor(metricExporter: io.opentelemetry.sdk.metrics.export.MetricExporter) {
    private val metricExporter: io.opentelemetry.sdk.metrics.export.MetricExporter
    private var intervalNanos: Long = TimeUnit.MINUTES.toNanos(DEFAULT_SCHEDULE_DELAY_MINUTES)

    @Nullable private var executor: ScheduledExecutorService? = null

    init {
        this.metricExporter = metricExporter
    }

    /**
     * Sets the interval of reads. If unset, defaults to {@value DEFAULT_SCHEDULE_DELAY_MINUTES}min.
     */
    fun setInterval(interval: Long, unit: TimeUnit): PeriodicMetricReaderBuilder {
        Objects.requireNonNull<TimeUnit>(unit, "unit")
        checkArgument(interval > 0, "interval must be positive")
        intervalNanos = unit.toNanos(interval)
        return this
    }

    /**
     * Sets the interval of reads. If unset, defaults to {@value DEFAULT_SCHEDULE_DELAY_MINUTES}min.
     */
    fun setInterval(interval: java.time.Duration): PeriodicMetricReaderBuilder {
        Objects.requireNonNull<java.time.Duration>(interval, "interval")
        return setInterval(interval.toNanos(), TimeUnit.NANOSECONDS)
    }

    /** Sets the [ScheduledExecutorService] to schedule reads on. */
    fun setExecutor(executor: ScheduledExecutorService?): PeriodicMetricReaderBuilder {
        Objects.requireNonNull<ScheduledExecutorService>(executor, "executor")
        this.executor = executor
        return this
    }

    /**
     * Returns a new [MetricReaderFactory] with the configuration of this builder which can be
     * registered with a [io.opentelemetry.sdk.metrics.SdkMeterProvider].
     */
    fun newMetricReaderFactory(): io.opentelemetry.sdk.metrics.export.MetricReaderFactory {
        var executor: ScheduledExecutorService? = executor
        if (executor == null) {
            executor =
                Executors.newScheduledThreadPool(1, DaemonThreadFactory("PeriodicMetricReader"))
        }
        return io.opentelemetry.sdk.metrics.export.PeriodicMetricReaderFactory(
            metricExporter,
            intervalNanos,
            executor
        )
    }

    companion object {
        const val DEFAULT_SCHEDULE_DELAY_MINUTES: Long = 1
    }
}
*/
