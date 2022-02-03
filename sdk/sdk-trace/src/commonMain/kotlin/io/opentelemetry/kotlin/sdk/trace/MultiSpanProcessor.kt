/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import kotlinx.atomicfu.atomic

/**
 * Implementation of the `SpanProcessor` that simply forwards all received events to a list of
 * `SpanProcessor`s.
 */
internal class MultiSpanProcessor private constructor(spanProcessors: List<SpanProcessor>) :
    SpanProcessor {
    private val spanProcessorsStart: List<SpanProcessor>
    private val spanProcessorsEnd: List<SpanProcessor>
    private val spanProcessorsAll: List<SpanProcessor>
    private val isShutdown = atomic(false)

    override fun onStart(parentContext: Context, readableSpan: ReadWriteSpan) {
        for (spanProcessor in spanProcessorsStart) {
            spanProcessor.onStart(parentContext, readableSpan)
        }
    }

    override fun isStartRequired(): Boolean {
        return spanProcessorsStart.isNotEmpty()
    }

    override fun onEnd(readableSpan: ReadableSpan) {
        for (spanProcessor in spanProcessorsEnd) {
            spanProcessor.onEnd(readableSpan)
        }
    }

    override fun isEndRequired(): Boolean {
        return spanProcessorsEnd.isNotEmpty()
    }

    override fun shutdown(): CompletableResultCode {
        if (isShutdown.getAndSet(true)) {
            return CompletableResultCode.ofSuccess()
        }
        val results = spanProcessorsAll.map { processor -> processor.shutdown() }
        return CompletableResultCode.ofAll(results)
    }

    override fun forceFlush(): CompletableResultCode {
        val results = spanProcessorsAll.map { processor -> processor.forceFlush() }
        return CompletableResultCode.ofAll(results)
    }

    init {
        spanProcessorsAll = spanProcessors
        spanProcessorsStart = spanProcessorsAll.filter { it.isStartRequired() }.toList()
        spanProcessorsEnd = spanProcessorsAll.filter { it.isEndRequired() }.toList()
    }

    companion object {
        /**
         * Creates a new `MultiSpanProcessor`.
         *
         * @param spanProcessorList the `List` of `SpanProcessor`s.
         * @return a new `MultiSpanProcessor`.
         * @throws NullPointerException if the `spanProcessorList` is `null`.
         */
        fun create(spanProcessorList: List<SpanProcessor>): SpanProcessor {
            return MultiSpanProcessor(spanProcessorList)
        }
    }
}
