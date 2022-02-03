/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.common

import io.opentelemetry.kotlin.api.common.getNanoseconds
import kotlinx.datetime.Clock as DateTimeClock

/** A [Clock] that uses [System.currentTimeMillis] and [System.nanoTime]. */
class SystemClock private constructor() : Clock {
    override fun now(): Long {
        return DateTimeClock.System.now().toEpochMilliseconds()
    }

    override fun nanoTime(): Long {
        return DateTimeClock.System.now().getNanoseconds()
    }

    companion object {
        private val INSTANCE = SystemClock()

        /**
         * Returns a `MillisClock`.
         *
         * @return a `MillisClock`.
         */
        val instance: Clock
            get() = INSTANCE
    }
}
