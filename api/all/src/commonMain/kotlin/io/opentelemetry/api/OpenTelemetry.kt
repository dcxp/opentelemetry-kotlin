/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api

import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.api.trace.TracerBuilder
import io.opentelemetry.api.trace.TracerProvider
import io.opentelemetry.context.propagation.ContextPropagators

/**
 * The entrypoint to telemetry functionality for tracing, metrics and baggage.
 *
 * If using the OpenTelemetry SDK, you may want to instantiate the [OpenTelemetry] to provide
 * configuration, for example of `Resource` or `Sampler`. See `OpenTelemetrySdk` and
 * `OpenTelemetrySdk.builder` for information on how to construct the SDK [OpenTelemetry].
 *
 * @see TracerProvider
 *
 * @see ContextPropagators
 */
interface OpenTelemetry {
    /** Returns the [TracerProvider] for this [OpenTelemetry]. */
    val tracerProvider: TracerProvider

    /**
     * Gets or creates a named tracer instance from the [TracerProvider] for this [ ].
     *
     * @param instrumentationName The name of the instrumentation library, not the name of the
     * instrument*ed* library (e.g., "io.opentelemetry.contrib.mongodb"). Must not be null.
     * @return a tracer instance.
     */
    fun getTracer(instrumentationName: String): Tracer {
        return tracerProvider[instrumentationName]
    }

    /**
     * Gets or creates a named and versioned tracer instance from the [TracerProvider] in this
     * [OpenTelemetry].
     *
     * @param instrumentationName The name of the instrumentation library, not the name of the
     * instrument*ed* library (e.g., "io.opentelemetry.contrib.mongodb"). Must not be null.
     * @param instrumentationVersion The version of the instrumentation library (e.g., "1.0.0").
     * @return a tracer instance.
     */
    fun getTracer(instrumentationName: String, instrumentationVersion: String): Tracer {
        return tracerProvider[instrumentationName, instrumentationVersion]
    }

    /**
     * Creates a [TracerBuilder] for a named [Tracer] instance.
     *
     * @param instrumentationName The name of the instrumentation library, not the name of the
     * instrument*ed* library.
     * @return a TracerBuilder instance.
     * @since 1.4.0
     */
    fun tracerBuilder(instrumentationName: String): TracerBuilder {
        return tracerProvider.tracerBuilder(instrumentationName)
    }

    /** Returns the [ContextPropagators] for this [OpenTelemetry]. */
    val propagators: ContextPropagators

    companion object {
        /** Returns a completely no-op [OpenTelemetry]. */
        fun noop(): OpenTelemetry {
            return DefaultOpenTelemetry.noop
        }

        /**
         * Returns an [OpenTelemetry] which will do remote propagation of [ ] using the provided
         * [ContextPropagators] and is no-op otherwise.
         */
        fun propagating(propagators: ContextPropagators): OpenTelemetry {
            return DefaultOpenTelemetry.getPropagating(propagators)
        }
    }
}
