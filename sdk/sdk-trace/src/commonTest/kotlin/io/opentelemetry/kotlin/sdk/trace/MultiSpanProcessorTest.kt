/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.trace.MockFactory.createReadWriteSpan
import io.opentelemetry.kotlin.sdk.trace.MockFactory.createSpanProcessor
import kotlin.test.Test

class MultiSpanProcessorTest {

    @Test
    fun empty() {
        val readWriteSpan: ReadWriteSpan = createReadWriteSpan()
        val readableSpan: ReadableSpan = createReadWriteSpan()
        val multiSpanProcessor = SpanProcessor.composite(emptyList())
        multiSpanProcessor.onStart(Context.root(), readWriteSpan)
        multiSpanProcessor.onEnd(readableSpan)
        multiSpanProcessor.shutdown()
    }

    @Test
    fun oneSpanProcessor() {
        val spanProcessor1 = createSpanProcessor()
        val multiSpanProcessor: SpanProcessor = SpanProcessor.composite(listOf(spanProcessor1))
        multiSpanProcessor shouldBe spanProcessor1
    }

    @Test
    fun twoSpanProcessor() {
        val spanProcessor1 = createSpanProcessor()
        val spanProcessor2 = createSpanProcessor()
        val readWriteSpan: ReadWriteSpan = createReadWriteSpan()
        val readableSpan: ReadableSpan = createReadWriteSpan()
        val multiSpanProcessor: SpanProcessor =
            SpanProcessor.composite(spanProcessor1, spanProcessor2)
        multiSpanProcessor.onStart(Context.root(), readWriteSpan)
        spanProcessor1.startContext shouldBe Context.root()
        spanProcessor1.startSpan shouldBe readWriteSpan
        spanProcessor2.startContext shouldBe Context.root()
        spanProcessor2.startSpan shouldBe readWriteSpan
        multiSpanProcessor.onEnd(readableSpan)
        spanProcessor1.endSpan shouldBe readableSpan
        spanProcessor2.endSpan shouldBe readableSpan
        multiSpanProcessor.forceFlush()
        spanProcessor1.flushCalled.shouldBeTrue()
        spanProcessor2.flushCalled.shouldBeTrue()
        multiSpanProcessor.shutdown()
        spanProcessor1.shutdownCalled.shouldBeTrue()
        spanProcessor2.shutdownCalled.shouldBeTrue()
    }

    @Test
    fun twoSpanProcessor_DifferentRequirements() {
        val spanProcessor1 = createSpanProcessor(isEndRequired = false)
        val spanProcessor2 = createSpanProcessor(isStartRequired = false)
        val readWriteSpan = createReadWriteSpan()
        val readableSpan = createReadWriteSpan()
        val multiSpanProcessor: SpanProcessor =
            SpanProcessor.composite(spanProcessor1, spanProcessor2)
        multiSpanProcessor.isStartRequired().shouldBeTrue()
        multiSpanProcessor.isEndRequired().shouldBeTrue()
        multiSpanProcessor.onStart(Context.root(), readWriteSpan)
        spanProcessor1.startContext shouldBe Context.root()
        spanProcessor1.startSpan shouldBe readWriteSpan
        spanProcessor2.startContext.shouldBeNull()
        spanProcessor2.startSpan.shouldBeNull()
        multiSpanProcessor.onEnd(readableSpan)
        spanProcessor1.endSpan.shouldBeNull()
        spanProcessor2.endSpan shouldBe readableSpan
        multiSpanProcessor.forceFlush()
        spanProcessor1.flushCalled.shouldBeTrue()
        spanProcessor2.flushCalled.shouldBeTrue()
        multiSpanProcessor.shutdown()
        spanProcessor1.shutdownCalled.shouldBeTrue()
        spanProcessor2.shutdownCalled.shouldBeTrue()
    }
}
