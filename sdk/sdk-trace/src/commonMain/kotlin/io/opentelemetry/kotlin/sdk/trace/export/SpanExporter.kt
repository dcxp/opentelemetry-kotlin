/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.export

import io.opentelemetry.kotlin.Closeable
import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.trace.data.SpanData

/**
 * An interface that allows different tracing services to export recorded data for sampled spans in
 * their own format.
 *
 * To export data this MUST be register to the `TracerSdk` using a [ ] or a
 * `BatchSampledSpansProcessor`.
 */
interface SpanExporter : Closeable {
    /**
     * Called to export sampled `Span`s. Note that export operations can be performed simultaneously
     * depending on the type of span processor being used. However, the [ ] will ensure that only
     * one export can occur at a time.
     *
     * @param spans the collection of sampled Spans to be exported.
     * @return the result of the export, which is often an asynchronous operation.
     */
    fun export(spans: Collection<SpanData>): CompletableResultCode

    /**
     * Exports the collection of sampled `Span`s that have not yet been exported. Note that export
     * operations can be performed simultaneously depending on the type of span processor being
     * used. However, the [BatchSpanProcessor] will ensure that only one export can occur at a time.
     *
     * @return the result of the flush, which is often an asynchronous operation.
     */
    fun flush(): CompletableResultCode

    /**
     * Called when [SdkTracerProvider.shutdown] is called, if this `SpanExporter` is registered to a
     * [SdkTracerProvider] object.
     *
     * @return a [CompletableResultCode] which is completed when shutdown completes.
     */
    fun shutdown(): CompletableResultCode

    /** Closes this [SpanExporter], releasing any resources. */
    override fun close() {
        shutdown()
    }

    companion object {
        /**
         * Returns a [SpanExporter] which simply delegates all exports to the `exporters` in order.
         *
         * Can be used to export to multiple backends using the same `SpanProcessor` like a
         * `SimpleSampledSpansProcessor` or a `BatchSampledSpansProcessor`.
         */
        fun composite(vararg exporters: SpanExporter): SpanExporter {
            return composite(exporters.toList())
        }

        /**
         * Returns a [SpanExporter] which simply delegates all exports to the `exporters` in order.
         *
         * Can be used to export to multiple backends using the same `SpanProcessor` like a
         * `SimpleSampledSpansProcessor` or a `BatchSampledSpansProcessor`.
         */
        fun composite(exporters: List<SpanExporter>): SpanExporter {
            if (exporters.isEmpty()) {
                return NoopSpanExporter.instance
            }
            return if (exporters.size == 1) {
                exporters.single()
            } else MultiSpanExporter.create(exporters)
        }
    }
}
