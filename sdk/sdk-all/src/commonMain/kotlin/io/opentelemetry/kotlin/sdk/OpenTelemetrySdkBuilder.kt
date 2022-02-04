/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk

import io.opentelemetry.kotlin.api.GlobalOpenTelemetry
import io.opentelemetry.kotlin.context.propagation.ContextPropagators
import io.opentelemetry.kotlin.sdk.trace.SdkTracerProvider

/** A builder for configuring an [OpenTelemetrySdk]. */
class OpenTelemetrySdkBuilder
/**
 * Package protected to disallow direct initialization.
 *
 * @see OpenTelemetrySdk.builder
 */
internal constructor() {
    private var propagators = ContextPropagators.noop()

    private var tracerProvider: SdkTracerProvider? = null

    /**
     * Sets the [SdkTracerProvider] to use. This can be used to configure tracing settings by
     * returning the instance created by a [SdkTracerProviderBuilder].
     *
     * If you use this method, it is assumed that you are providing a fully configured
     * TracerSdkProvider, and other settings will be ignored.
     *
     * Note: the parameter passed in here must be a [SdkTracerProvider] instance.
     *
     * @param tracerProvider A [SdkTracerProvider] to use with this instance.
     * @see SdkTracerProvider.builder
     */
    fun setTracerProvider(tracerProvider: SdkTracerProvider?): OpenTelemetrySdkBuilder {
        this.tracerProvider = tracerProvider
        return this
    }

    /** Sets the [ContextPropagators] to use. */
    fun setPropagators(propagators: ContextPropagators): OpenTelemetrySdkBuilder {
        this.propagators = propagators
        return this
    }

    /**
     * Returns a new [OpenTelemetrySdk] built with the configuration of this [ ] and registers it as
     * the global [ ]. An exception will be thrown if this method is attempted to be called multiple
     * times in the lifecycle of an application - ensure you have only one SDK for use as the global
     * instance. If you need to configure multiple SDKs for tests, use [ ]
     * [GlobalOpenTelemetry.resetForTest] between them.
     *
     * @see GlobalOpenTelemetry
     */
    fun buildAndRegisterGlobal(): OpenTelemetrySdk {
        val sdk: OpenTelemetrySdk = build()
        GlobalOpenTelemetry.set(sdk)
        return sdk
    }

    /**
     * Returns a new [OpenTelemetrySdk] built with the configuration of this [ ]. This SDK is not
     * registered as the global [ ]. It is recommended that you register one SDK using [ ]
     * [OpenTelemetrySdkBuilder.buildAndRegisterGlobal] for use by instrumentation that requires
     * access to a global instance of [io.opentelemetry.kotlin.api.OpenTelemetry].
     *
     * @see GlobalOpenTelemetry
     */
    fun build(): OpenTelemetrySdk {
        if (tracerProvider == null) {
            tracerProvider = SdkTracerProvider.builder().build()
        }
        return OpenTelemetrySdk(
            OpenTelemetrySdk.ObfuscatedTracerProvider(tracerProvider!!),
            propagators
        )
    }
}
