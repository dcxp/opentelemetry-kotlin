/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf

/**
 * Responsible for storing metrics (by name) and returning access to input pipeline for instrument
 * wiring.
 *
 * The rules of the registry:
 *
 * * Only one storage type may be registered per-name. Repeated look-ups per-name will return the
 * same storage.
 * * The metric descriptor should be "compatible", when returning an existing metric storage, i.e.
 * same type of metric, same name, description etc.
 * * The registered storage type MUST be either always Asynchronous or always Synchronous. No mixing
 * and matching.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
class MetricStorageRegistry {
    // TODO: Maybe we store metrics *and* instrument interfaces separately here...
    private val registry = atomic<PersistentMap<String, MetricStorage>>(persistentMapOf())

    /**
     * Returns a `Collection` view of the registered [MetricStorage].
     *
     * @return a `Collection` view of the registered [MetricStorage].
     */
    val metrics: Collection<MetricStorage>
        get() = registry.value.values

    /**
     * Registers the given `Metric` to this registry. Returns the registered storage if no other
     * metric with the same name is registered or a previously registered metric with same name and
     * equal with the current metric, otherwise throws an exception.
     *
     * @param storage the metric storage to use or discard.
     * @return the given metric storage if no metric with same name already registered, otherwise
     * the previous registered instrument.
     * @throws IllegalArgumentException if instrument cannot be registered.
     */
    @Suppress("UNCHECKED_CAST")
    fun <I : MetricStorage> register(storage: I): I {
        val descriptor: MetricDescriptor = storage.metricDescriptor
        val descriptorName = descriptor.name.lowercase()
        registry.update { map ->
            if (!map.containsKey(descriptorName)) {
                map.put(descriptorName, storage)
            } else {
                map
            }
        }
        val oldOrNewStorage: MetricStorage = registry.value[descriptorName]!!

        // Make sure the storage is compatible.
        if (!oldOrNewStorage.metricDescriptor.isCompatibleWith(descriptor)) {
            throw DuplicateMetricStorageException(
                oldOrNewStorage.metricDescriptor,
                descriptor,
                "Metric with same name and different descriptor already created."
            )
        }
        // Make sure we aren't mixing sync + async.
        if (storage::class != oldOrNewStorage::class) {
            throw DuplicateMetricStorageException(
                oldOrNewStorage.metricDescriptor,
                descriptor,
                "Metric with same name and different instrument already created."
            )
        }
        return oldOrNewStorage as I
    }
}
