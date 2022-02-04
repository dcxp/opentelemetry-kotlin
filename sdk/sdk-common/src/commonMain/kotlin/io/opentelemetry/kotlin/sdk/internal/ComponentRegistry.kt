/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.internal

import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.updateAndGet
import kotlinx.collections.immutable.persistentMapOf

/**
 * Base class for all the provider classes (TracerProvider, MeterProvider, etc.).
 *
 * @param <V> the type of the registered value. </V>
 */
class ComponentRegistry<V>(private val factory: (InstrumentationLibraryInfo) -> V) {
    private val registry = atomic(persistentMapOf<InstrumentationLibraryInfo, V>())

    /**
     * Returns the registered value associated with this name and version if any, otherwise creates
     * a new instance and associates it with the given name and version.
     *
     * @param instrumentationName the name of the instrumentation library.
     * @param instrumentationVersion the version of the instrumentation library.
     * @param schemaUrl the URL of the OpenTelemetry schema used by the instrumentation library.
     * @return the registered value associated with this name and version.
     * @since 1.4.0
     */
    /**
     * Returns the registered value associated with this name and version if any, otherwise creates
     * a new instance and associates it with the given name and version. The schemaUrl will be set
     * to null.
     *
     * @param instrumentationName the name of the instrumentation library.
     * @param instrumentationVersion the version of the instrumentation library.
     * @return the registered value associated with this name and version.
     */
    /**
     * Returns the registered value associated with this name and `null` version if any, otherwise
     * creates a new instance and associates it with the given name and `null` version and
     * schemaUrl.
     *
     * @param instrumentationName the name of the instrumentation library.
     * @return the registered value associated with this name and `null` version.
     */
    operator fun get(
        instrumentationName: String,
        instrumentationVersion: String? = null,
        schemaUrl: String? = null
    ): V {
        val instrumentationLibraryInfo: InstrumentationLibraryInfo =
            InstrumentationLibraryInfo.create(
                instrumentationName,
                instrumentationVersion,
                schemaUrl
            )

        // Optimistic lookup, before creating the new component.

        return registry.updateAndGet { oldMap ->
            if (oldMap.containsKey(instrumentationLibraryInfo)) {
                oldMap
            } else {
                oldMap.put(instrumentationLibraryInfo, factory(instrumentationLibraryInfo))
            }
        }[instrumentationLibraryInfo]!!
    }

    /**
     * Returns a `Collection` view of the registered components.
     *
     * @return a `Collection` view of the registered components.
     */
    val components: Collection<V>
        get() = registry.value.values.toList()
}
