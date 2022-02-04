/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.Aggregator
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.AggregatorHandle
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.export.CollectionHandle
import io.opentelemetry.kotlin.sdk.metrics.internal.state.MetricStorageUtils.MAX_ACCUMULATIONS

/**
 * Allows synchronous collection of metrics and reports delta values isolated by collection handle.
 *
 * This storage should allow allocation of new aggregation cells for metrics and unique reporting of
 * delta accumulations per-collection-handle.
 */
internal class DeltaMetricStorage<T>(
    private val aggregator: Aggregator<T>,
    private val instrument: InstrumentDescriptor
) {
    private val activeCollectionStorage: MutableMap<Attributes, AggregatorHandle<T>> =
        mutableMapOf()
    private val unreportedDeltas: MutableList<DeltaAccumulation<T>> = mutableListOf()

    /**
     * Allocates memory for a new metric stream, and returns a handle for synchronous recordings.
     *
     * @param attributes The identifying attributes for the metric stream.
     * @return A handle that will (efficiently) record synchronous measurements.
     */
    fun bind(attributes: Attributes): BoundStorageHandle {
        var aggregatorHandle: AggregatorHandle<T>? = activeCollectionStorage[attributes]
        if (aggregatorHandle != null && aggregatorHandle.acquire()) {
            // At this moment it is guaranteed that the Bound is in the map and will not be removed.
            return aggregatorHandle
        }

        // Missing entry or no longer mapped. Try to add a new one if not exceeded cardinality
        // limits.
        aggregatorHandle = aggregator.createHandle()
        while (true) {
            if (activeCollectionStorage.size >= MAX_ACCUMULATIONS) {
                /*logger.log(
                    java.util.logging.Level.WARNING,
                    "Instrument " +
                        instrument.name +
                        " has exceeded the maximum allowed accumulations (" +
                        MAX_ACCUMULATIONS +
                        ")."
                )*/
                return NOOP_STORAGE_HANDLE
            }
            val boundAggregatorHandle: AggregatorHandle<T> =
                activeCollectionStorage.getOrPut(attributes) { aggregatorHandle }
            if (boundAggregatorHandle != null) {
                if (boundAggregatorHandle.acquire()) {
                    // At this moment it is guaranteed that the Bound is in the map and will not be
                    // removed.
                    return boundAggregatorHandle
                }
                // Try to remove the boundAggregator. This will race with the collect method, but
                // only one
                // will succeed.
                activeCollectionStorage.remove(attributes)
                continue
            }
            return aggregatorHandle
        }
    }

    /**
     * Returns the latest delta accumulation for a specific collection handle.
     *
     * @param collector The current reader of metrics.
     * @param collectors All possible readers of metrics.
     * @param suppressCollection If true, don't actively pull synchronous instruments, measurements
     * should be up to date.
     * @return The delta accumulation of metrics since the last read of the specified reader.
     */
    fun collectFor(
        collector: CollectionHandle,
        collectors: Set<CollectionHandle>,
        suppressCollection: Boolean
    ): Map<Attributes, T> {
        // First we force a collection
        if (!suppressCollection) {
            collectSynchronousDeltaAccumulationAndReset()
        }
        // Now build a delta result.
        val result: MutableMap<Attributes, T> = mutableMapOf()
        for (point in unreportedDeltas) {
            if (!point.wasReadBy(collector)) {
                MetricStorageUtils.mergeInPlace<T>(result, point.read(collector), aggregator)
            }
        }
        // Now run a quick cleanup of deltas before returning.
        unreportedDeltas.removeAll { delta: DeltaAccumulation<T> -> delta.wasReadByAll(collectors) }
        return result
    }

    /**
     * Collects the currently accumulated measurements from the concurrent-friendly synchronous
     * storage.
     *
     * All synchronous handles will be collected + reset during this method. Additionally cleanup
     * related stale concurrent-map handles will occur. Any `null` measurements are ignored.
     */
    private fun collectSynchronousDeltaAccumulationAndReset() {
        // Grab accumulated measurements.
        val result: MutableMap<Attributes, T> = HashMap()
        for ((key, value) in activeCollectionStorage.entries) {
            val unmappedEntry: Boolean = value.tryUnmap()
            if (unmappedEntry) {
                // If able to unmap then remove the record from the current Map. This can race with
                // the
                // acquire but because we requested a specific value only one will succeed.
                activeCollectionStorage.remove(key)
            }
            val accumulation: T = value.accumulateThenReset(key) ?: continue
            // Feed latest batch to the aggregator.
            result[key] = accumulation
        }
        if (result.isNotEmpty()) {
            unreportedDeltas.add(DeltaAccumulation<T>(result))
        }
    }

    /** An implementation of [BoundStorageHandle] that does not record. */
    private class NoopBoundHandle : BoundStorageHandle {
        override fun recordLong(value: Long, attributes: Attributes, context: Context) {}
        override fun recordDouble(value: Double, attributes: Attributes, context: Context) {}
        override fun release() {}
    }

    companion object {
        //        private val logger: ThrottlingLogger =
        //            ThrottlingLogger(
        //
        // java.util.logging.Logger.getLogger(DeltaMetricStorage::class.java.getName())
        //            )
        private val NOOP_STORAGE_HANDLE: BoundStorageHandle = NoopBoundHandle()
    }
}
