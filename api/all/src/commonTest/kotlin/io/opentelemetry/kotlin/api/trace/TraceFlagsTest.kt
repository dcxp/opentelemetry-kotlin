/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.kotlin.api.trace

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.test.Test

/** Unit tests for {@link TraceFlags}. */
class TraceFlagsTest {

    @Test
    fun defaultInstances() {
        TraceFlags.default.asHex() shouldBe "00"
        TraceFlags.sampled.asHex() shouldBe "01"
    }

    @Test
    fun isSampled() {
        TraceFlags.fromByte((0xff).toByte()).isSampled().shouldBeTrue()
        TraceFlags.fromByte(0x01).isSampled().shouldBeTrue()
        TraceFlags.fromByte(0x05).isSampled().shouldBeTrue()
        TraceFlags.fromByte(0x00).isSampled().shouldBeFalse()
    }

    @Test
    fun toFromHex() {
        for (i in 0..255) {
            var hex = i.toString(16)
            if (hex.length == 1) {
                hex = "0$hex"
            }
            TraceFlags.fromHex(hex, 0).asHex() shouldBe hex
        }
    }

    @Test
    fun toFromByte() {
        for (i in 0..255) {
            TraceFlags.fromByte(i.toByte()).asByte() shouldBe i.toByte()
        }
    }
}
