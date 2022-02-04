/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.trace

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.api.internal.OtelEncodingUtils
import kotlin.test.Test

/** Unit tests for [SpanId]. */
class SpanIdTest {
    @Test
    fun invalid() {
        SpanId.invalid shouldBe "0000000000000000"
    }

    @Test
    fun isValid() {
        SpanId.isValid(null).shouldBeFalse()
        SpanId.isValid("001").shouldBeFalse()
        SpanId.isValid("000000000000z000").shouldBeFalse()
        SpanId.isValid(SpanId.invalid).shouldBeFalse()
        SpanId.isValid(first).shouldBeTrue()
        SpanId.isValid(second).shouldBeTrue()
    }

    @Test
    fun fromLong() {
        SpanId.fromLong(0) shouldBe SpanId.invalid
        SpanId.fromLong(0x61) shouldBe first
        SpanId.fromLong(-0xffffffffffffbfL) shouldBe second
    }

    @Test
    fun fromBytes() {
        val spanId = "090a0b0c0d0e0f00"
        SpanId.fromBytes(OtelEncodingUtils.bytesFromBase16(spanId, SpanId.length)) shouldBe spanId
    }

    @Test
    fun fromBytes_Invalid() {
        SpanId.fromBytes(null) shouldBe SpanId.invalid
        SpanId.fromBytes(byteArrayOf(0, 1, 2, 3, 4)) shouldBe SpanId.invalid
    }

    companion object {
        private const val first = "0000000000000061"
        private const val second = "ff00000000000041"
    }
}
