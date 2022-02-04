/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.kotest.matchers.booleans.shouldBeFalse
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.trace.MockFactory.createReadWriteSpan
import kotlin.test.Test

internal class NoopSpanProcessorTest {

    @Test
    fun noCrash() {
        val readableSpan: ReadableSpan = createReadWriteSpan()
        val readWriteSpan: ReadWriteSpan = createReadWriteSpan()
        val noopSpanProcessor: SpanProcessor = NoopSpanProcessor.instance
        noopSpanProcessor.onStart(Context.root(), readWriteSpan)
        noopSpanProcessor.isStartRequired().shouldBeFalse()
        noopSpanProcessor.onEnd(readableSpan)
        noopSpanProcessor.isEndRequired().shouldBeFalse()
        noopSpanProcessor.forceFlush()
        noopSpanProcessor.shutdown()
    }
}
