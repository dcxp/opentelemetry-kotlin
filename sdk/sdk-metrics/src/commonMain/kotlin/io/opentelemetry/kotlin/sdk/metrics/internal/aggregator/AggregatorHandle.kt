/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.metrics.data.ExemplarData
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarReservoir
import io.opentelemetry.kotlin.sdk.metrics.internal.state.BoundStorageHandle
import kotlinx.atomicfu.AtomicLong
import kotlinx.atomicfu.atomic

/**
 * Aggregator represents the abstract class that is used for synchronous instruments. It must be
 * thread-safe and avoid locking when possible, because values are recorded synchronously on the
 * calling thread.
 *
 * An [AggregatorHandle] must be created for every unique `LabelSet` recorded, and can be referenced
 * by the bound instruments.
 *
 * It atomically counts the number of references (usages) while also keeping a state of
 * mapped/unmapped into an external map. It uses an atomic value where the least significant bit is
 * used to keep the state of mapping ('1' is used for unmapped and '0' is for mapped) and the rest
 * of the bits are used for reference (usage) counting.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
abstract class AggregatorHandle<T>
protected constructor(private val exemplarReservoir: ExemplarReservoir) : BoundStorageHandle {
    // Atomically counts the number of references (usages) while also keeping a state of
    // mapped/unmapped into a registry map.
    private val refCountMapped: AtomicLong = atomic(2L)

    // Note: This is not 100% thread-safe. There is a race condition where recordings can
    // be made in the moment between the reset and the setting of this field's value. In those
    // cases, it is possible that a recording could be missed in a given recording interval, but
    // it should be picked up in the next, assuming that more recordings are being made.
    private val hasRecordings = atomic(false)

    /**
     * Acquires this `Aggregator` for use. Returns `true` if the entry is still mapped and increases
     * the reference usages, if unmapped returns `false`.
     *
     * @return `true` if successful.
     */
    fun acquire(): Boolean {
        // Every reference adds/removes 2 instead of 1 to avoid changing the mapping bit.
        return refCountMapped.addAndGet(2L) and 1L == 0L
    }

    /** Release this `Aggregator`. It decreases the reference usage. */
    override fun release() {
        // Every reference adds/removes 2 instead of 1 to avoid changing the mapping bit.
        refCountMapped.getAndAdd(-2L)
    }

    /**
     * Flips the mapped bit to "unmapped" state and returns true if both of the following conditions
     * are true upon entry to this function: 1) There are no active references; 2) The mapped bit is
     * in "mapped" state; otherwise no changes are done to mapped bit and false is returned.
     *
     * @return `true` if successful.
     */
    fun tryUnmap(): Boolean {
        return if (refCountMapped.value != 0L) {
            // Still references (usages) to this bound or already unmapped.
            false
        } else refCountMapped.compareAndSet(0L, 1L)
    }

    /** Returns the current value into as [T] and resets the current value in this `Aggregator`. */
    fun accumulateThenReset(attributes: Attributes): T? {
        if (!hasRecordings.value) {
            return null
        }
        hasRecordings.lazySet(false)
        return doAccumulateThenReset(exemplarReservoir.collectAndReset(attributes))
    }

    /** Implementation of the `accumulateThenReset`. */
    abstract fun doAccumulateThenReset(exemplars: List<ExemplarData>): T

    override fun recordLong(value: Long, attributes: Attributes, context: Context) {
        exemplarReservoir.offerMeasurement(value, attributes, context)
        recordLong(value)
    }

    /**
     * Updates the current aggregator with a newly recorded `long` value.
     *
     * Visible for Testing
     *
     * @param value the new `long` value to be added.
     */
    fun recordLong(value: Long) {
        doRecordLong(value)
        hasRecordings.lazySet(true)
    }

    /**
     * Concrete Aggregator instances should implement this method in order support recordings of
     * long values.
     */
    protected open fun doRecordLong(value: Long) {
        throw UnsupportedOperationException(
            "This aggregator does not support recording long values."
        )
    }

    override fun recordDouble(value: Double, attributes: Attributes, context: Context) {
        exemplarReservoir.offerMeasurement(value, attributes, context)
        recordDouble(value)
    }

    /**
     * Updates the current aggregator with a newly recorded `double` value.
     *
     * Visible for Testing
     *
     * @param value the new `double` value to be added.
     */
    fun recordDouble(value: Double) {
        doRecordDouble(value)
        hasRecordings.lazySet(true)
    }

    /**
     * Concrete Aggregator instances should implement this method in order support recordings of
     * double values.
     */
    protected open fun doRecordDouble(value: Double) {
        throw UnsupportedOperationException(
            "This aggregator does not support recording double values."
        )
    }
}
