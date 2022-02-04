/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.metrics.internal.export.CollectionHandle

/**
 * Synchronous recording of delta-accumulated measurements.
 *
 * This stores in-progress metric values that haven't been exported yet.
 */
internal class DeltaAccumulation<T>(private val recording: Map<Attributes, T>) {
    private val readers: MutableSet<CollectionHandle> = CollectionHandle.mutableSet()

    /** Returns true if this accumulation was read by the [CollectionHandle]. */
    fun wasReadBy(handle: CollectionHandle): Boolean {
        return readers.contains(handle)
    }

    /** Returns true if all readers in the given set have read this accumulation. */
    fun wasReadByAll(handles: Set<CollectionHandle>): Boolean {
        return readers.containsAll(handles)
    }

    /**
     * Reads the current delta accumulation.
     *
     * @param handle The reader of the accumulation.
     * @return the accumulation.
     */
    fun read(handle: CollectionHandle): Map<Attributes, T> {
        readers.add(handle)
        return recording
    }
}
