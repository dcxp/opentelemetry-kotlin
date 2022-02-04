/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import kotlin.js.JsName

/**
 * Interface for use as backing data structure for exponential histogram buckets.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
interface ExponentialCounter {
    /**
     * The first index with a recording. May be negative.
     *
     * @return the first index with a recording.
     */
    val indexStart: Int

    /**
     * The last index with a recording. May be negative.
     *
     * @return The last index with a recording.
     */
    val indexEnd: Int

    /**
     * Persist new data at index, incrementing by delta amount.
     *
     * @param index The index of where to perform the incrementation.
     * @param delta How much to increment the index by.
     * @return success status.
     */
    fun increment(index: Int, delta: Long): Boolean

    /**
     * Get the number of recordings for the given index.
     *
     * @return the number of recordings for the index.
     */
    operator fun get(index: Int): Long

    /**
     * Boolean denoting if the backing structure has recordings or not.
     *
     * @return true if no recordings, false if at least one recording.
     */
    @JsName("isExponentialCounterEmpty") fun isEmpty(): Boolean
}
