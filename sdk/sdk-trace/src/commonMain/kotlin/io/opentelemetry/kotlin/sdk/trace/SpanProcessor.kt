/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.opentelemetry.kotlin.Closeable
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.CompletableResultCode

/**
 * SpanProcessor is the interface `TracerSdk` uses to allow synchronous hooks for when a `Span` is
 * started or when a `Span` is ended.
 */
interface SpanProcessor : Closeable {
    /**
     * Called when a [io.opentelemetry.kotlin.api.trace.Span] is started, if the [ ]
     * [Span.isRecording] returns true.
     *
     * This method is called synchronously on the execution thread, should not throw or block the
     * execution thread.
     *
     * @param parentContext the parent `Context` of the span that just started.
     * @param span the `ReadableSpan` that just started.
     */
    fun onStart(parentContext: Context, span: ReadWriteSpan)

    /**
     * Returns `true` if this [SpanProcessor] requires start events.
     *
     * @return `true` if this [SpanProcessor] requires start events.
     */
    fun isStartRequired(): Boolean

    /**
     * Called when a [io.opentelemetry.kotlin.api.trace.Span] is ended, if the [ ][Span.isRecording]
     * returns true.
     *
     * This method is called synchronously on the execution thread, should not throw or block the
     * execution thread.
     *
     * @param span the `ReadableSpan` that just ended.
     */
    fun onEnd(span: ReadableSpan)

    /**
     * Returns `true` if this [SpanProcessor] requires end events.
     *
     * @return `true` if this [SpanProcessor] requires end events.
     */
    fun isEndRequired(): Boolean

    /**
     * Processes all span events that have not yet been processed and closes used resources.
     *
     * @return a [CompletableResultCode] which completes when shutdown is finished.
     */
    fun shutdown(): CompletableResultCode {
        return forceFlush()
    }

    /**
     * Processes all span events that have not yet been processed.
     *
     * @return a [CompletableResultCode] which completes when currently queued spans are finished
     * processing.
     */
    fun forceFlush(): CompletableResultCode {
        return CompletableResultCode()
    }

    /**
     * Closes this [SpanProcessor] after processing any remaining spans, releasing any resources.
     */
    override fun close() {
        shutdown()
    }

    companion object {
        /**
         * Returns a [SpanProcessor] which simply delegates all processing to the `processors` in
         * order.
         */
        fun composite(vararg processors: SpanProcessor): SpanProcessor {
            return composite(processors.asList())
        }

        /**
         * Returns a [SpanProcessor] which simply delegates all processing to the `processors` in
         * order.
         */
        fun composite(processors: List<SpanProcessor>): SpanProcessor {
            if (processors.isEmpty()) {
                return NoopSpanProcessor.instance
            }
            return if (processors.size == 1) {
                processors.single()
            } else MultiSpanProcessor.create(processors)
        }
    }
}
