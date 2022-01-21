/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.trace

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import io.opentelemetry.api.common.AttributeKey.Companion.booleanKey
import io.opentelemetry.api.common.AttributeKey.Companion.longKey
import io.opentelemetry.api.common.AttributeKey.Companion.stringKey
import io.opentelemetry.api.common.Attributes
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlin.test.Test

class PropagatedSpanTest {
    @Test
    fun notRecording() {
        Span.invalid().isRecording().shouldBeFalse()
    }

    @Test
    fun hasInvalidContextAndDefaultSpanOptions() {
        val context: SpanContext = Span.invalid().spanContext
        context.traceFlags shouldBe TraceFlags.default
        context.traceState shouldBe TraceState.default
    }

    @Test
    fun doNotCrash() {
        val span: Span = Span.invalid()
        span.setAttribute(stringKey("MyStringAttributeKey"), "MyStringAttributeValue")
        span.setAttribute(booleanKey("MyBooleanAttributeKey"), true)
        span.setAttribute(longKey("MyLongAttributeKey"), 123L)
        span.setAttribute(longKey("MyLongAttributeKey"), 123)
        span.setAttribute("EmptyString", "")
        span.setAttribute("long", 1)
        span.setAttribute("double", 1.0)
        span.setAttribute("boolean", true)
        span.setAllAttributes(Attributes.empty())
        span.setAllAttributes(
            Attributes.of(stringKey("MyStringAttributeKey"), "MyStringAttributeValue")
        )
        span.addEvent("event")
        span.addEvent("event", 0, DateTimeUnit.NANOSECOND)
        span.addEvent("event", Instant.DISTANT_FUTURE)
        span.addEvent("event", Attributes.of(booleanKey("MyBooleanAttributeKey"), true))
        span.addEvent(
            "event",
            Attributes.of(booleanKey("MyBooleanAttributeKey"), true),
            0,
            DateTimeUnit.NANOSECOND
        )
        span.setStatus(StatusCode.OK)
        span.setStatus(StatusCode.OK, "null")
        span.recordException(IllegalStateException())
        span.recordException(IllegalStateException(), Attributes.empty())
        span.updateName("name")
        span.end()
        span.end(0, DateTimeUnit.NANOSECOND)
        span.end(Instant.DISTANT_FUTURE)
    }

    @Test
    fun defaultSpan_ToString() {
        val span: Span = Span.invalid()
        span.toString() shouldBe
            "PropagatedSpan(ImmutableSpanContext(traceId='00000000000000000000000000000000', "
                .plus("spanId='0000000000000000', traceFlags=00, ")
                .plus(
                    "traceState=ArrayBasedTraceState(entries=[]), isRemote=false, isValid=false))"
                )
    }
}
