/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.trace

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.use
import kotlin.test.Test

class SpanTest {
    @Test
    fun testGetCurrentSpan_Default() {
        val span = Span.current()
        span shouldBe Span.invalid()
    }

    @Test
    fun testGetCurrentSpan_SetSpan() {
        val span = Span.wrap(SpanContext.invalid)
        Context.current().with(span).makeCurrent().use { Span.current() shouldBe span }
    }

    @Test
    fun testGetSpan_DefaultContext() {
        val span = Span.fromContext(Context.root())
        span shouldBe Span.invalid()
    }

    @Test
    fun testGetSpan_ExplicitContext() {
        val span = Span.wrap(SpanContext.invalid)
        val context = Context.root().with(span)
        Span.fromContext(context) shouldBe span
    }

    @Test
    fun testGetSpanWithoutDefault_DefaultContext() {
        val span = Span.fromContextOrNull(Context.root())
        span.shouldBeNull()
    }

    @Test
    fun testGetSpanWithoutDefault_ExplicitContext() {
        val span = Span.wrap(SpanContext.invalid)
        val context = Context.root().with(span)
        Span.fromContextOrNull(context) shouldBe span
    }

    @Test
    fun testInProcessContext() {
        val span = Span.wrap(SpanContext.invalid)
        span.makeCurrent().use {
            Span.current() shouldBe span
            val secondSpan = Span.wrap(SpanContext.invalid)
            try {
                secondSpan.makeCurrent().use { Span.current() shouldBe secondSpan }
            } finally {
                Span.current() shouldBe span
            }
        }
        Span.current().spanContext.isValid.shouldBeFalse()
    }
}
