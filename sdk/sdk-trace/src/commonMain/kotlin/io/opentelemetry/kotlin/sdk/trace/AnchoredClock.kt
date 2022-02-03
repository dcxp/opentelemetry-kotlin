/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.opentelemetry.kotlin.sdk.common.Clock

/**
 * A utility for returning wall times anchored to a given point in time. Wall time measurements will
 * not be taken from the system, but instead are computed by adding [ monotonic time]
 * [System.nanoTime] to the anchor point.
 *
 * This is needed because Java has lower granularity for epoch times and tracing events are recorded
 * more often. There is also a performance improvement in avoiding referencing the system's wall
 * time where possible. Instead of computing a true wall time for every timestamp within a trace, we
 * compute it once at the local root and then anchor all descendant span timestamps to this root's
 * timestamp.
 */
internal class AnchoredClock
private constructor(
    private val clock: Clock,
    private val epochNanos: Long,
    private val nanoTime: Long
) {

    /**
     * Returns the current epoch timestamp in nanos calculated using [System.nanoTime] since the
     * reference time read in the constructor. This time can be used for computing durations.
     *
     * @return the current epoch timestamp in nanos.
     */
    fun now(): Long {
        val deltaNanos: Long = clock.nanoTime() - nanoTime
        return epochNanos + deltaNanos
    }

    /** Returns the start time in nanos of this [AnchoredClock]. */
    fun startTime(): Long {
        return epochNanos
    }

    companion object {
        /**
         * Returns a `AnchoredClock`.
         *
         * @param clock the `Clock` to be used to read the current epoch time and nanoTime.
         * @return a `MonotonicClock`.
         */
        fun create(clock: Clock): AnchoredClock {
            return AnchoredClock(clock, clock.now(), clock.nanoTime())
        }
    }
}
