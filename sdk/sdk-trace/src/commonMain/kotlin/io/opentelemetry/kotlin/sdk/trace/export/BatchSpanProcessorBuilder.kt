/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.export

import io.opentelemetry.kotlin.api.common.normalizeToNanos
import kotlinx.datetime.DateTimeUnit
import kotlin.time.Duration

/** Builder class for [BatchSpanProcessor]. */
class BatchSpanProcessorBuilder internal constructor(private val spanExporter: SpanExporter) {

    // Visible for testing
    var scheduleDelayNanos: Long =
        DateTimeUnit.MILLISECOND.normalizeToNanos(DEFAULT_SCHEDULE_DELAY_MILLIS)
        private set

    // Visible for testing
    var maxQueueSize = DEFAULT_MAX_QUEUE_SIZE
        private set

    // Visible for testing
    var maxExportBatchSize = DEFAULT_MAX_EXPORT_BATCH_SIZE
        private set

    // Visible for testing
    var exporterTimeoutNanos: Long =
        DateTimeUnit.MILLISECOND.normalizeToNanos(DEFAULT_EXPORT_TIMEOUT_MILLIS.toLong())
        private set

    // TODO: Consider to add support for constant Attributes and/or Resource.
    /**
     * Sets the delay interval between two consecutive exports. If unset, defaults to {@value
     * * DEFAULT_SCHEDULE_DELAY_MILLIS}ms.
     */
    fun setScheduleDelay(delay: Long, unit: DateTimeUnit): BatchSpanProcessorBuilder {
        require(delay >= 0) { "delay must be non-negative" }
        scheduleDelayNanos = unit.normalizeToNanos(delay)
        return this
    }

    /**
     * Sets the delay interval between two consecutive exports. If unset, defaults to {@value
     * * DEFAULT_SCHEDULE_DELAY_MILLIS}ms.
     */
    fun setScheduleDelay(delay: Duration): BatchSpanProcessorBuilder {
        return setScheduleDelay(delay.inWholeNanoseconds, DateTimeUnit.NANOSECOND)
    }

    /**
     * Sets the maximum time an export will be allowed to run before being cancelled. If unset,
     * defaults to {@value DEFAULT_EXPORT_TIMEOUT_MILLIS}ms.
     */
    fun setExporterTimeout(timeout: Long, unit: DateTimeUnit): BatchSpanProcessorBuilder {
        require(timeout >= 0) { "delay must be non-negative" }
        exporterTimeoutNanos = unit.normalizeToNanos(timeout)
        return this
    }

    /**
     * Sets the maximum time an export will be allowed to run before being cancelled. If unset,
     * defaults to {@value DEFAULT_EXPORT_TIMEOUT_MILLIS}ms.
     */
    fun setExporterTimeout(timeout: Duration): BatchSpanProcessorBuilder {
        return setExporterTimeout(timeout.inWholeNanoseconds, DateTimeUnit.NANOSECOND)
    }

    /**
     * Sets the maximum number of Spans that are kept in the queue before start dropping. More
     * memory than this value may be allocated to optimize queue access.
     *
     * See the BatchSampledSpansProcessor class description for a high-level design description of
     * this class.
     *
     * Default value is `2048`.
     *
     * @param maxQueueSize the maximum number of Spans that are kept in the queue before start
     * dropping.
     * @return this.
     * @see BatchSpanProcessorBuilder.DEFAULT_MAX_QUEUE_SIZE
     */
    fun setMaxQueueSize(maxQueueSize: Int): BatchSpanProcessorBuilder {
        this.maxQueueSize = maxQueueSize
        return this
    }

    /**
     * Sets the maximum batch size for every export. This must be smaller or equal to
     * `maxQueuedSpans`.
     *
     * Default value is `512`.
     *
     * @param maxExportBatchSize the maximum batch size for every export.
     * @return this.
     * @see BatchSpanProcessorBuilder.DEFAULT_MAX_EXPORT_BATCH_SIZE
     */
    fun setMaxExportBatchSize(maxExportBatchSize: Int): BatchSpanProcessorBuilder {
        require(maxExportBatchSize > 0) { "maxExportBatchSize must be positive." }
        this.maxExportBatchSize = maxExportBatchSize
        return this
    }

    /**
     * Returns a new [BatchSpanProcessor] that batches, then converts spans to proto and forwards
     * them to the given `spanExporter`.
     *
     * @return a new [BatchSpanProcessor].
     * @throws NullPointerException if the `spanExporter` is `null`.
     */
    fun build(): BatchSpanProcessor {
        return BatchSpanProcessor(
            spanExporter,
            scheduleDelayNanos,
            maxQueueSize,
            maxExportBatchSize,
            exporterTimeoutNanos
        )
    }

    companion object {
        // Visible for testing
        const val DEFAULT_SCHEDULE_DELAY_MILLIS: Long = 5000

        // Visible for testing
        const val DEFAULT_MAX_QUEUE_SIZE = 2048

        // Visible for testing
        const val DEFAULT_MAX_EXPORT_BATCH_SIZE = 512

        // Visible for testing
        const val DEFAULT_EXPORT_TIMEOUT_MILLIS = 30000
    }
}
