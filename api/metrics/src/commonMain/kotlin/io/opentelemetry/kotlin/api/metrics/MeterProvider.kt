/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.metrics

import io.opentelemetry.kotlin.api.metrics.internal.NoopMeterProvider

/**
 * A registry for creating named [Meter]s.
 *
 * A MeterProvider represents a configured (or noop) Metric collection system that can be used to
 * instrument code.
 *
 * The name *Provider* is for consistency with other languages and it is **NOT** loaded using
 * reflection.
 *
 * @see io.opentelemetry.kotlin.api.metrics.Meter
 */
interface MeterProvider {
    /**
     * Gets or creates a named and versioned meter instance.
     *
     * @param instrumentationName The name of the instrumentation library, not the name of the
     * instrument*ed* library.
     * @return a meter instance.
     */
    operator fun get(instrumentationName: String): Meter {
        return meterBuilder(instrumentationName).build()
    }

    /**
     * Creates a MeterBuilder for a named meter instance.
     *
     * @param instrumentationName The name of the instrumentation library, not the name of the
     * instrument*ed* library.
     * @return a MeterBuilder instance.
     * @since 1.4.0
     */
    fun meterBuilder(instrumentationName: String): MeterBuilder

    companion object {
        /** Returns a no-op [MeterProvider] which provides meters which do not record or emit. */
        fun noop(): MeterProvider {
            return NoopMeterProvider.instance
        }
    }
}
