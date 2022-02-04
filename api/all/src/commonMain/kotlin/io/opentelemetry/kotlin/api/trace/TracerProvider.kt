/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.trace

/**
 * A registry for creating named [Tracer]s. The name *Provider* is for consistency with * other
 * languages and it is **NOT** loaded using reflection.
 *
 * @see Tracer
 */
interface TracerProvider {
    /**
     * Gets or creates a named tracer instance.
     *
     * @param instrumentationName The name of the instrumentation library, not the name of the
     * instrument*ed* library (e.g., "io.opentelemetry.kotlin.contrib.mongodb"). Must not be null. If the
     * instrumented library is providing its own instrumentation, this should match the library
     * name.
     * @return a tracer instance.
     */
    operator fun get(instrumentationName: String): Tracer

    /**
     * Gets or creates a named and versioned tracer instance.
     *
     * @param instrumentationName The name of the instrumentation library, not the name of the
     * instrument*ed* library (e.g., "io.opentelemetry.kotlin.contrib.mongodb"). Must not be null. If the
     * instrumented library is providing its own instrumentation, this should match the library
     * name.
     * @param instrumentationVersion The version of the instrumentation library (e.g., "1.0.0").
     * @return a tracer instance.
     */
    operator fun get(instrumentationName: String, instrumentationVersion: String): Tracer

    /**
     * Creates a TracerBuilder for a named [Tracer] instance.
     *
     * @param instrumentationName The name of the instrumentation library, not the name of the
     * instrument*ed* library.
     * @return a TracerBuilder instance.
     * @since 1.4.0
     */
    fun tracerBuilder(instrumentationName: String): TracerBuilder {
        return DefaultTracerBuilder.instance
    }

    companion object {
        /**
         * Returns a no-op [TracerProvider] which only creates no-op [Span]s which do not record nor
         * are emitted.
         */
        fun noop(): TracerProvider {
            return DefaultTracerProvider.instance
        }
    }
}
