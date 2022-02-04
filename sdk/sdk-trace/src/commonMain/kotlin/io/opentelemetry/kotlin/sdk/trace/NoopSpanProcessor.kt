/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.opentelemetry.kotlin.context.Context

internal class NoopSpanProcessor private constructor() : SpanProcessor {
    override fun onStart(parentContext: Context, span: ReadWriteSpan) {}

    override fun isStartRequired(): Boolean {
        return false
    }

    override fun onEnd(span: ReadableSpan) {}

    override fun isEndRequired(): Boolean {
        return false
    }

    companion object {
        private val INSTANCE = NoopSpanProcessor()
        val instance: SpanProcessor
            get() = INSTANCE
    }
}
