/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.opentelemetry.kotlin.Closeable
import io.opentelemetry.kotlin.api.trace.Tracer
import io.opentelemetry.kotlin.api.trace.TracerBuilder
import io.opentelemetry.kotlin.api.trace.TracerProvider
import io.opentelemetry.kotlin.sdk.common.Clock
import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.internal.ComponentRegistry
import io.opentelemetry.kotlin.sdk.resources.Resource
import io.opentelemetry.kotlin.sdk.trace.samplers.Sampler

/**
 * `Tracer` provider implementation for [TracerProvider].
 *
 * This class is not intended to be used in application code and it is used only by [ ]. However, if
 * you need a custom implementation of the factory, you can create one as needed.
 */
class SdkTracerProvider
internal constructor(
    clock: Clock,
    idsGenerator: IdGenerator,
    resource: Resource,
    spanLimitsSupplier: () -> SpanLimits,
    sampler: Sampler,
    spanProcessors: List<SpanProcessor>
) : TracerProvider, Closeable {
    internal val sharedState: TracerSharedState
    private val tracerSdkComponentRegistry: ComponentRegistry<SdkTracer>

    init {
        sharedState =
            TracerSharedState(
                clock,
                idsGenerator,
                resource,
                spanLimitsSupplier,
                sampler,
                spanProcessors
            )
        tracerSdkComponentRegistry =
            ComponentRegistry { instrumentationLibraryInfo ->
                SdkTracer(sharedState, instrumentationLibraryInfo)
            }
    }

    override operator fun get(instrumentationName: String): Tracer {
        return tracerBuilder(instrumentationName).build()
    }

    override operator fun get(instrumentationName: String, instrumentationVersion: String): Tracer {
        return tracerBuilder(instrumentationName)
            .setInstrumentationVersion(instrumentationVersion)
            .build()
    }

    override fun tracerBuilder(instrumentationName: String): TracerBuilder {
        // Per the spec, both null and empty are "invalid" and a default value should be used.
        var instrumentationName = instrumentationName
        if (instrumentationName.isEmpty()) {
            // logger.fine("Tracer requested without instrumentation name.")
            instrumentationName = DEFAULT_TRACER_NAME
        }
        return SdkTracerBuilder(tracerSdkComponentRegistry, instrumentationName)
    }

    /** Returns the [SpanLimits] that are currently applied to created spans. */
    val spanLimits: SpanLimits
        get() = sharedState.spanLimits

    /** Returns the configured [Sampler]. */
    val sampler: Sampler
        get() = sharedState.getSampler()

    /**
     * Attempts to stop all the activity for this [Tracer]. Calls [ ][SpanProcessor.shutdown] for
     * all registered [SpanProcessor]s.
     *
     * The returned [CompletableResultCode] will be completed when all the Spans are processed.
     *
     * After this is called, newly created `Span`s will be no-ops.
     *
     * After this is called, further attempts at re-using or reconfiguring this instance will result
     * in undefined behavior. It should be considered a terminal operation for the SDK
     * implementation.
     *
     * @return a [CompletableResultCode] which is completed when all the span processors have been
     * shut down.
     */
    fun shutdown(): CompletableResultCode {
        if (sharedState.hasBeenShutdown()) {
            // logger.log(java.util.logging.Level.WARNING, "Calling shutdown() multiple times.")
            return CompletableResultCode.ofSuccess()
        }
        return sharedState.shutdown()
    }

    /**
     * Requests the active span processor to process all span events that have not yet been
     * processed and returns a [CompletableResultCode] which is completed when the flush is
     * finished.
     *
     * @see SpanProcessor.forceFlush
     */
    fun forceFlush(): CompletableResultCode {
        return sharedState.getActiveSpanProcessor().forceFlush()
    }

    /**
     * Attempts to stop all the activity for this [Tracer]. Calls [ ][SpanProcessor.shutdown] for
     * all registered [SpanProcessor]s.
     *
     * This operation may block until all the Spans are processed. Must be called before turning off
     * the main application to ensure all data are processed and exported.
     *
     * After this is called, newly created `Span`s will be no-ops.
     *
     * After this is called, further attempts at re-using or reconfiguring this instance will result
     * in undefined behavior. It should be considered a terminal operation for the SDK
     * implementation.
     */
    override fun close() {
        shutdown()
        // .join(10, TimeUnit.SECONDS)
    }

    companion object {
        // private val logger: java.util.logging.Logger =
        //    java.util.logging.Logger.getLogger(SdkTracerProvider::class.java.getName())
        const val DEFAULT_TRACER_NAME = ""

        /**
         * Returns a new [SdkTracerProviderBuilder] for [SdkTracerProvider].
         *
         * @return a new [SdkTracerProviderBuilder] for [SdkTracerProvider].
         */
        fun builder(): SdkTracerProviderBuilder {
            return SdkTracerProviderBuilder()
        }
    }
}
