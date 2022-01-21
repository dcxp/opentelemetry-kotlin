/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api

import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.api.trace.TracerBuilder
import io.opentelemetry.api.trace.TracerProvider
import io.opentelemetry.context.propagation.ContextPropagators
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.reentrantLock

/**
 * A global singleton for the entrypoint to telemetry functionality for tracing, metrics and
 * baggage.
 *
 * If using the OpenTelemetry SDK, you may want to instantiate the [OpenTelemetry] to provide
 * configuration, for example of `Resource` or `Sampler`. See `OpenTelemetrySdk` and
 * `OpenTelemetrySdk.builder` for information on how to construct the SDK [OpenTelemetry].
 *
 * @see TracerProvider
 *
 * @see ContextPropagators
 */
object GlobalOpenTelemetry {
    private val lock = reentrantLock()
    private val globalOpenTelemetry = atomic<ObfuscatedOpenTelemetry?>(null)
    private val setGlobalCaller = atomic<Throwable?>(null)

    /**
     * Returns the registered global [OpenTelemetry].
     *
     * @throws IllegalStateException if a provider has been specified by system property using the
     * interface FQCN but the specified provider cannot be found.
     */
    fun get(): OpenTelemetry {
        if (globalOpenTelemetry.value == null) {
            lock.lock()
            try {
                if (globalOpenTelemetry.value == null) {
                    val autoConfigured: OpenTelemetry? = maybeAutoConfigure()
                    if (autoConfigured != null) {
                        return autoConfigured
                    }
                    set(OpenTelemetry.noop())
                    return OpenTelemetry.noop()
                }
            } finally {
                lock.unlock()
            }
        }
        return globalOpenTelemetry.value!!
    }

    /**
     * Sets the [OpenTelemetry] that should be the global instance. Future calls to [ ][.get] will
     * return the provided [OpenTelemetry] instance. This should be called once as early as possible
     * in your application initialization logic, often in a `static` block in your main class. It
     * should only be called once - an attempt to call it a second time will result in an error. If
     * trying to set the global [OpenTelemetry] multiple times in tests, use
     * [GlobalOpenTelemetry.resetForTest] between them.
     *
     * If you are using the OpenTelemetry SDK, you should generally use
     * `OpenTelemetrySdk.builder().buildAndRegisterGlobal()` instead of calling this method
     * directly.
     */
    fun set(openTelemetry: OpenTelemetry) {
        lock.lock()
        try {
            if (globalOpenTelemetry.value != null) {
                throw IllegalStateException(
                    "GlobalOpenTelemetry.set has already been called. GlobalOpenTelemetry.set must be " +
                        "called only once before any calls to GlobalOpenTelemetry.get. If you are using " +
                        "the OpenTelemetrySdk, use OpenTelemetrySdkBuilder.buildAndRegisterGlobal " +
                        "instead. Previous invocation set to cause of this exception.",
                    setGlobalCaller.value
                )
            }
            globalOpenTelemetry.lazySet(ObfuscatedOpenTelemetry(openTelemetry))
            setGlobalCaller.lazySet(Throwable())
        } finally {
            lock.unlock()
        }
    }

    /** Returns the globally registered [TracerProvider]. */
    @kotlin.jvm.JvmStatic
    val tracerProvider: TracerProvider
        get() = get().tracerProvider

    /**
     * Gets or creates a named tracer instance from the globally registered [TracerProvider].
     *
     * This is a shortcut method for `getTracerProvider().get(instrumentationName)`
     *
     * @param instrumentationName The name of the instrumentation library, not the name of the
     * instrument*ed* library (e.g., "io.opentelemetry.contrib.mongodb"). Must not be null.
     * @return a tracer instance.
     */
    fun getTracer(instrumentationName: String): Tracer {
        return get().getTracer(instrumentationName)
    }

    /**
     * Gets or creates a named and versioned tracer instance from the globally registered [ ].
     *
     * This is a shortcut method for `getTracerProvider().get(instrumentationName,
     * instrumentationVersion)`
     *
     * @param instrumentationName The name of the instrumentation library, not the name of the
     * instrument*ed* library (e.g., "io.opentelemetry.contrib.mongodb"). Must not be null.
     * @param instrumentationVersion The version of the instrumentation library (e.g., "1.0.0").
     * @return a tracer instance.
     */
    fun getTracer(instrumentationName: String, instrumentationVersion: String): Tracer {
        return get().getTracer(instrumentationName, instrumentationVersion)
    }

    /**
     * Creates a TracerBuilder for a named [Tracer] instance.
     *
     * This is a shortcut method for `get().tracerBuilder(instrumentationName)`
     *
     * @param instrumentationName The name of the instrumentation library, not the name of the
     * instrument*ed* library.
     * @return a TracerBuilder instance.
     * @since 1.4.0
     */
    fun tracerBuilder(instrumentationName: String): TracerBuilder {
        return get().tracerBuilder(instrumentationName)
    }

    /**
     * Unsets the global [OpenTelemetry]. This is only meant to be used from tests which need to
     * reconfigure [OpenTelemetry].
     */
    fun resetForTest() {
        globalOpenTelemetry.lazySet(null)
    }

    /** Returns the globally registered [ContextPropagators] for remote propagation of a context. */
    val propagators: ContextPropagators
        get() = get().propagators

    private fun maybeAutoConfigure(): OpenTelemetry? {
        // Todo enable auto configuration
        return null
    }

    /**
     * Static global instances are obfuscated when they are returned from the API to prevent users
     * from casting them to their SDK-specific implementation. For example, we do not want users to
     * use patterns like `(OpenTelemetrySdk) GlobalOpenTelemetry.get()`.
     */
    internal class ObfuscatedOpenTelemetry(delegate: OpenTelemetry) : OpenTelemetry {
        private val delegate: OpenTelemetry

        init {
            this.delegate = delegate
        }

        override val tracerProvider: TracerProvider
            get() = delegate.tracerProvider
        override val propagators: ContextPropagators
            get() = delegate.propagators

        override fun tracerBuilder(instrumentationName: String): TracerBuilder {
            return delegate.tracerBuilder(instrumentationName)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other is ObfuscatedOpenTelemetry) {
                if (delegate != other.delegate) return false
            } else if (other is OpenTelemetry) {
                if (delegate != other) return false
            } else {
                return false
            }
            return true
        }

        override fun hashCode(): Int {
            return delegate.hashCode()
        }
    }
}
