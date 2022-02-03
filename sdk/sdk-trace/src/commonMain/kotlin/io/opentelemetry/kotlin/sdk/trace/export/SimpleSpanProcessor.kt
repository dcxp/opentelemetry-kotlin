/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.export

import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.trace.ReadWriteSpan
import io.opentelemetry.kotlin.sdk.trace.ReadableSpan
import io.opentelemetry.kotlin.sdk.trace.SpanProcessor
import io.opentelemetry.kotlin.sdk.trace.data.SpanData
import kotlinx.atomicfu.atomic

/**
 * An implementation of the [SpanProcessor] that converts the [ReadableSpan] to [ ] and passes it
 * directly to the configured exporter.
 *
 * This processor will cause all spans to be exported directly as they finish, meaning each export
 * request will have a single span. Most backends will not perform well with a single span per
 * request so unless you know what you're doing, strongly consider using [ ] instead, including in
 * special environments such as serverless runtimes. [SimpleSpanProcessor] is generally meant to for
 * logging exporters only.
 */
class SimpleSpanProcessor
internal constructor(private val spanExporter: SpanExporter, private val sampled: Boolean) :
    SpanProcessor {
    private val pendingExports: MutableSet<CompletableResultCode> = mutableSetOf()
    private val isShutdown = atomic(false)

    override fun onStart(parentContext: Context, span: ReadWriteSpan) {
        // Do nothing.
    }

    override fun isStartRequired(): Boolean {
        return false
    }

    override fun onEnd(span: ReadableSpan) {
        if (sampled && !span.spanContext.isSampled()) {
            return
        }
        try {
            val spans: List<SpanData> = listOf(span.toSpanData())
            val result: CompletableResultCode = spanExporter.export(spans)
            pendingExports.add(result)
            result.whenComplete {
                pendingExports.remove(result)
                if (!result.isSuccess) {
                    // logger.log(java.util.logging.Level.FINE, "Exporter failed")
                }
            }
        } catch (e: Exception) {
            // logger.log(java.util.logging.Level.WARNING, "Exporter threw an Exception", e)
        }
    }

    override fun isEndRequired(): Boolean {
        return true
    }

    override fun shutdown(): CompletableResultCode {
        if (isShutdown.getAndSet(true)) {
            return CompletableResultCode.ofSuccess()
        }
        val result = CompletableResultCode()
        val flushResult: CompletableResultCode = forceFlush()
        flushResult.whenComplete {
            val shutdownResult: CompletableResultCode = spanExporter.shutdown()
            shutdownResult.whenComplete {
                if (!flushResult.isSuccess || !shutdownResult.isSuccess) {
                    result.fail()
                } else {
                    result.succeed()
                }
            }
        }
        return result
    }

    override fun forceFlush(): CompletableResultCode {
        return CompletableResultCode.ofAll(pendingExports)
    }

    companion object {
        // private val logger: java.util.logging.Logger =
        //    java.util.logging.Logger.getLogger(SimpleSpanProcessor::class.java.getName())

        /**
         * Returns a new [SimpleSpanProcessor] which exports spans to the [SpanExporter]
         * synchronously.
         *
         * This processor will cause all spans to be exported directly as they finish, meaning each
         * export request will have a single span. Most backends will not perform well with a single
         * span per request so unless you know what you're doing, strongly consider using [ ]
         * instead, including in special environments such as serverless runtimes.
         * [SimpleSpanProcessor] is generally meant to for logging exporters only.
         */
        fun create(exporter: SpanExporter): SpanProcessor {
            return SimpleSpanProcessor(exporter, true)
        }
    }
}
