/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.export

import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.trace.data.SpanData

/**
 * Implementation of the `SpanExporter` that simply forwards all received spans to a list of
 * `SpanExporter`.
 *
 * Can be used to export to multiple backends using the same `SpanProcessor` like a
 * `SimpleSampledSpansProcessor` or a `BatchSampledSpansProcessor`.
 */
internal class MultiSpanExporter
private constructor(private val spanExporters: Array<SpanExporter>) : SpanExporter {
    override fun export(spans: Collection<SpanData>): CompletableResultCode {
        val results: List<CompletableResultCode> =
            spanExporters.map { spanExporter ->
                try {
                    spanExporter.export(spans)
                } catch (_: Exception) {
                    // If an exception was thrown by the exporter
                    // logger.log(java.util.logging.Level.WARNING, "Exception thrown by the
                    // export.", e)
                    CompletableResultCode.ofFailure()
                }
            }
        return CompletableResultCode.ofAll(results)
    }

    /**
     * Flushes the data of all registered [SpanExporter]s.
     *
     * @return the result of the operation
     */
    override fun flush(): CompletableResultCode {
        val results: List<CompletableResultCode> =
            spanExporters.map { spanExporter ->
                try {
                    spanExporter.flush()
                } catch (_: Exception) {
                    // If an exception was thrown by the exporter
                    // logger.log(java.util.logging.Level.WARNING, "Exception thrown by the
                    // export.", e)
                    CompletableResultCode.ofFailure()
                }
            }
        return CompletableResultCode.ofAll(results)
    }

    override fun shutdown(): CompletableResultCode {
        val results: List<CompletableResultCode> =
            spanExporters.map { spanExporter ->
                try {
                    spanExporter.shutdown()
                } catch (_: Exception) {
                    // If an exception was thrown by the exporter
                    // logger.log(java.util.logging.Level.WARNING, "Exception thrown by the
                    // export.", e)
                    CompletableResultCode.ofFailure()
                }
            }
        return CompletableResultCode.ofAll(results)
    }

    companion object {
        // private val logger: java.util.logging.Logger =
        //     java.util.logging.Logger.getLogger(MultiSpanExporter::class.java.getName())

        /**
         * Constructs and returns an instance of this class.
         *
         * @param spanExporters the exporters spans should be sent to
         * @return the aggregate span exporter
         */
        fun create(spanExporters: List<SpanExporter>): SpanExporter {
            return MultiSpanExporter(spanExporters.toTypedArray())
        }
    }
}
