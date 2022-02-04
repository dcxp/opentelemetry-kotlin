/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.trace

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.test.Test

/** Unit tests for [SpanContext]. */
class SpanContextTest {
    @Test
    fun invalidSpanContext() {
        SpanContext.invalid.traceId shouldBe TraceId.invalid
        SpanContext.invalid.spanId shouldBe SpanId.invalid
        SpanContext.invalid.traceFlags shouldBe TraceFlags.default
    }

    @Test
    fun isValid() {
        SpanContext.invalid.isValid.shouldBeFalse()

        SpanContext.create(FIRST_TRACE_ID, SpanId.invalid, TraceFlags.default, TraceState.default)
            .isValid
            .shouldBeFalse()

        SpanContext.create(TraceId.invalid, FIRST_SPAN_ID, TraceFlags.default, TraceState.default)
            .isValid
            .shouldBeFalse()
        first.isValid.shouldBeTrue()
        second.isValid.shouldBeTrue()
    }

    @Test
    fun traceId() {
        first.traceId shouldBe FIRST_TRACE_ID
        second.traceId shouldBe SECOND_TRACE_ID
    }

    @Test
    fun spanId() {
        first.spanId shouldBe FIRST_SPAN_ID
        second.spanId shouldBe SECOND_SPAN_ID
    }

    @Test
    fun traceFlags() {
        first.traceFlags shouldBe TraceFlags.default
        second.traceFlags shouldBe TraceFlags.sampled
    }

    @Test
    fun traceState() {
        first.traceState shouldBe FIRST_TRACE_STATE
        second.traceState shouldBe SECOND_TRACE_STATE
    }

    @Test
    fun isRemote() {
        first.isRemote.shouldBeFalse()
        second.isRemote.shouldBeFalse()
        remote.isRemote.shouldBeTrue()
    }

    companion object {
        private const val FIRST_TRACE_ID = "00000000000000000000000000000061"
        private const val SECOND_TRACE_ID = "00000000000000300000000000000000"
        private const val FIRST_SPAN_ID = "0000000000000061"
        private const val SECOND_SPAN_ID = "3000000000000000"
        private val FIRST_TRACE_STATE = TraceState.builder().put("foo", "bar").build()
        private val SECOND_TRACE_STATE = TraceState.builder().put("foo", "baz").build()
        private val first =
            SpanContext.create(FIRST_TRACE_ID, FIRST_SPAN_ID, TraceFlags.default, FIRST_TRACE_STATE)
        private val second =
            SpanContext.create(
                SECOND_TRACE_ID,
                SECOND_SPAN_ID,
                TraceFlags.sampled,
                SECOND_TRACE_STATE
            )
        private val remote =
            SpanContext.createFromRemoteParent(
                SECOND_TRACE_ID,
                SECOND_SPAN_ID,
                TraceFlags.sampled,
                TraceState.default
            )
    }
}
