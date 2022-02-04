/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.common

/** Interface for getting the current time. */
interface Clock {

    /**
     * Returns the current epoch timestamp in nanos from this clock. This timestamp should only be
     * used to compute a current time. To compute a duration, timestamps should always be obtained
     * using [.nanoTime]. For example, this usage is correct.
     *
     * <pre>`long startNanos = clock.nanoTime(); // Spend time... long durationNanos =
     * clock.nanoTime() - startNanos; `</pre> *
     *
     * This usage is NOT correct.
     *
     * <pre>`long startNanos = clock.now(); // Spend time... long durationNanos = clock.now() -
     * startNanos; `</pre> *
     */
    fun now(): Long

    /**
     * Returns a time measurement with nanosecond precision that can only be used to calculate
     * elapsed time.
     */
    fun nanoTime(): Long

    companion object {
        /** Returns a default [Clock] which reads from [system time][System]. */
        val default: Clock
            get() = SystemClock.instance
    }
}
