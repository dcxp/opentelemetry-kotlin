/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.trace

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.booleans.shouldBeFalse
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.stringKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlin.test.Test

/** Unit tests for [SpanBuilder]. */
internal class SpanBuilderTest {
    private val tracer: Tracer = DefaultTracer.instance
    @Test
    fun doNotCrash_NoopImplementation() {
        shouldNotThrowAny {
            val spanBuilder = tracer.spanBuilder("")
            spanBuilder.setNoParent()
            spanBuilder.addLink(SpanContext.invalid, Attributes.empty())
            spanBuilder.setAttribute(stringKey(""), "foo")
            spanBuilder.setStartTimestamp(-1, DateTimeUnit.MILLISECOND)
            spanBuilder.setParent(Context.root())
            spanBuilder.setNoParent()
            spanBuilder.addLink(Span.invalid().spanContext)
            spanBuilder.addLink(Span.invalid().spanContext, Attributes.empty())
            spanBuilder.setAttribute("key", "value")
            spanBuilder.setAttribute("key", 12345L)
            spanBuilder.setAttribute("key", .12345)
            spanBuilder.setAttribute("key", true)
            spanBuilder.setAttribute(stringKey("key"), "value")
            spanBuilder.setAllAttributes(Attributes.of(stringKey("key"), "value"))
            spanBuilder.setAllAttributes(Attributes.empty())
            spanBuilder.setStartTimestamp(12345L, DateTimeUnit.NANOSECOND)
            spanBuilder.setStartTimestamp(Instant.DISTANT_FUTURE)
            spanBuilder.startSpan().spanContext.isValid.shouldBeFalse()
        }
    }
}
