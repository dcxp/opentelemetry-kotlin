/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.api.trace

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.opentelemetry.api.internal.OtelEncodingUtils
import kotlin.test.Test

/** Unit tests for {@link TraceId}. */
class TraceIdTest {
    private val first: String
        get() {
            return "00000000000000000000000000000061"
        }
    private val second: String
        get() {
            return "ff000000000000000000000000000041"
        }

    @Test
    fun invalid() {
        TraceId.invalid shouldBe "00000000000000000000000000000000"
    }

    @Test
    fun isValid() {
        TraceId.isValid(null).shouldBeFalse()
        TraceId.isValid("001").shouldBeFalse()
        TraceId.isValid("000000000000004z0000000000000016").shouldBeFalse()
        TraceId.isValid(TraceId.invalid).shouldBeFalse()

        TraceId.isValid(first).shouldBeTrue()
        TraceId.isValid(second).shouldBeTrue()
    }

    @Test
    fun fromLongs() {
        TraceId.fromLongs(0, 0) shouldBe TraceId.invalid
        TraceId.fromLongs(0, 0x61) shouldBe first
        TraceId.fromLongs(0xff00000000000000u.toLong(), 0x41) shouldBe second
        TraceId.fromLongs(0xff01020304050600u.toLong(), 0xff0a0b0c0d0e0f00u.toLong()) shouldBe
            "ff01020304050600ff0a0b0c0d0e0f00"
    }

    @Test
    fun fromBytes() {
        val traceId = "0102030405060708090a0b0c0d0e0f00"
        TraceId.fromBytes(OtelEncodingUtils.bytesFromBase16(traceId, TraceId.length)) shouldBe
            traceId
    }

    @Test
    fun fromBytes_Invalid() {
        TraceId.fromBytes(null) shouldBe TraceId.invalid
        TraceId.fromBytes(byteArrayOf(1, 2, 3, 4)) shouldBe TraceId.invalid
    }
}
