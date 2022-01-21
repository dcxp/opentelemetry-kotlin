/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.sdk

import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.trace.Tracer
import io.opentelemetry.api.trace.TracerBuilder
import io.opentelemetry.api.trace.TracerProvider
import io.opentelemetry.context.propagation.ContextPropagators
import io.opentelemetry.sdk.trace.SdkTracerProvider

/** The SDK implementation of [OpenTelemetry]. */
class OpenTelemetrySdk
internal constructor(
    private val obfuscatedTracerProvider: ObfuscatedTracerProvider,
    override val propagators: ContextPropagators
) : OpenTelemetry {

    override val tracerProvider: TracerProvider
        get() = obfuscatedTracerProvider

    /** Returns the [SdkTracerProvider] for this [OpenTelemetrySdk]. */
    val sdkTracerProvider: SdkTracerProvider
        get() = obfuscatedTracerProvider.unobfuscate()

    /**
     * This class allows the SDK to unobfuscate an obfuscated static global provider.
     *
     * Static global providers are obfuscated when they are returned from the API to prevent users
     * from casting them to their SDK specific implementation. For example, we do not want users to
     * use patterns like `(TracerSdkProvider) OpenTelemetry.getGlobalTracerProvider()`.
     */
    internal class ObfuscatedTracerProvider(private val delegate: SdkTracerProvider) :
        TracerProvider {
        override fun get(instrumentationName: String): Tracer {
            return delegate[instrumentationName]
        }

        override fun get(instrumentationName: String, instrumentationVersion: String): Tracer {
            return delegate[instrumentationName, instrumentationVersion]
        }

        override fun tracerBuilder(instrumentationName: String): TracerBuilder {
            return delegate.tracerBuilder(instrumentationName)
        }

        fun unobfuscate(): SdkTracerProvider {
            return delegate
        }
    }

    companion object {
        /** Returns a new [OpenTelemetrySdkBuilder] for configuring an instance of [ ]. */
        fun builder(): io.opentelemetry.sdk.OpenTelemetrySdkBuilder {
            return io.opentelemetry.sdk.OpenTelemetrySdkBuilder()
        }
    }
}
