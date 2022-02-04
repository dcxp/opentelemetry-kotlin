/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.trace

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.context.Context
import kotlin.test.Test

/** Unit tests for [DefaultTracer]. */
// Need to suppress warnings for MustBeClosed because Android 14 does not support
// try-with-resources.
internal class DefaultTracerTest {
    @Test
    fun defaultSpanBuilderWithName() {
        defaultTracer.spanBuilder(SPAN_NAME).startSpan().spanContext.isValid.shouldBeFalse()
    }

    @Test
    fun testSpanContextPropagationExplicitParent() {
        val span =
            defaultTracer
                .spanBuilder(SPAN_NAME)
                .setParent(Context.root().with(Span.wrap(spanContext)))
                .startSpan()
        span.spanContext shouldBe spanContext
    }

    @Test
    fun testSpanContextPropagation() {
        val parent = Span.wrap(spanContext)
        val span =
            defaultTracer.spanBuilder(SPAN_NAME).setParent(Context.root().with(parent)).startSpan()
        span.spanContext shouldBe spanContext
    }

    @Test
    fun noSpanContextMakesInvalidSpans() {
        val span = defaultTracer.spanBuilder(SPAN_NAME).startSpan()
        span.spanContext shouldBe SpanContext.invalid
    }

    @Test
    fun testSpanContextPropagation_fromContext() {
        val context = Context.current().with(Span.wrap(spanContext))
        val span = defaultTracer.spanBuilder(SPAN_NAME).setParent(context).startSpan()
        span.spanContext shouldBe spanContext
    }

    @Test
    fun testSpanContextPropagation_fromContextAfterNoParent() {
        val context = Context.current().with(Span.wrap(spanContext))
        val span = defaultTracer.spanBuilder(SPAN_NAME).setNoParent().setParent(context).startSpan()
        span.spanContext shouldBe spanContext
    }

    @Test
    fun testSpanContextPropagation_fromContextThenNoParent() {
        val context = Context.current().with(Span.wrap(spanContext))
        val span = defaultTracer.spanBuilder(SPAN_NAME).setParent(context).setNoParent().startSpan()
        span.spanContext shouldBe SpanContext.invalid
    }

    companion object {
        private val defaultTracer: Tracer = DefaultTracer.instance
        private const val SPAN_NAME = "MySpanName"
        private val spanContext =
            SpanContext.create(
                "00000000000000000000000000000061",
                "0000000000000061",
                TraceFlags.default,
                TraceState.default
            )
    }
}
