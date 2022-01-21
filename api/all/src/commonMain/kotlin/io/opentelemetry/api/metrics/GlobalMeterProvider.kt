/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.metrics

import io.opentelemetry.api.metrics.internal.NoopMeterProvider
import kotlinx.atomicfu.atomic

/** This class is a temporary solution until metrics SDK is marked stable. */
object GlobalMeterProvider {
    private val globalMeterProvider = atomic(NoopMeterProvider.instance)

    /** Returns the globally registered [MeterProvider]. */
    fun get(): MeterProvider {
        return globalMeterProvider.value
    }

    /**
     * Sets the [MeterProvider] that should be the global instance. Future calls to [ ][.get] will
     * return the provided [MeterProvider] instance. This should be called once as early as possible
     * in your application initialization logic, often in a `static` block in your main class.
     */
    fun set(provider: MeterProvider) {
        globalMeterProvider.lazySet(provider)
    }

    fun disableGlobalMeterProvider() {
        globalMeterProvider.lazySet(NoopMeterProvider.instance)
    }
}
