/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.common

import io.kotest.matchers.ints.shouldNotBeExactly
import io.kotest.matchers.longs.shouldBeExactly
import kotlin.test.Test

// This test is placed in the all artifact instead of the common one so it uses the dependency jar
// instead of the classes directly, which allows verifying mrjar behavior.
internal class SystemClockTest {
    // @Test
    fun millisPrecision() {
        // If we test many times, we can be fairly sure we didn't just get lucky with having a
        // rounded
        // result on a higher than expected precision timestamp.
        for (i in 0..99) {
            val now: Long = SystemClock.instance.now()
            (now % 1000000) shouldBeExactly 0
        }
    }

    @Test
    fun microsPrecision() {
        // If we test many times, we can be fairly sure we get at least one timestamp that isn't
        // coincidentally rounded to millis precision.
        var numHasMicros = 0
        for (i in 0..99) {
            val now: Long = SystemClock.instance.now()
            if (now % 1000000 != 0L) {
                numHasMicros++
            }
        }
        numHasMicros shouldNotBeExactly 0
    }
}
