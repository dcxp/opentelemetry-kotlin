/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.opentelemetry.kotlin.sdk.common.Clock
import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.resources.Resource
import io.opentelemetry.kotlin.sdk.trace.samplers.Sampler
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.updateAndGet

// Represents the shared state/config between all Tracers created by the same TracerProvider.
internal class TracerSharedState(
    private val clock: Clock,
    private val idGenerator: IdGenerator,
    private val resource: Resource,
    private val spanLimitsSupplier: () -> SpanLimits,
    private val sampler: Sampler,
    spanProcessors: List<SpanProcessor>
) {
    // tracks whether it is safe to skip id validation on ids from the above generator
    val isIdGeneratorSafeToSkipIdValidation: Boolean = idGenerator is RandomIdGenerator
    private val activeSpanProcessor = SpanProcessor.composite(spanProcessors)
    private val shutdownResult = atomic<CompletableResultCode?>(null)

    fun getClock(): Clock {
        return clock
    }

    fun getIdGenerator(): io.opentelemetry.kotlin.sdk.trace.IdGenerator {
        return idGenerator
    }

    fun getResource(): Resource {
        return resource
    }

    /** Returns the current [SpanLimits]. */
    val spanLimits: SpanLimits
        get() = spanLimitsSupplier()

    /** Returns the configured [Sampler]. */
    fun getSampler(): Sampler {
        return sampler
    }

    /**
     * Returns the active `SpanProcessor`.
     *
     * @return the active `SpanProcessor`.
     */
    fun getActiveSpanProcessor(): io.opentelemetry.kotlin.sdk.trace.SpanProcessor {
        return activeSpanProcessor
    }

    /**
     * Returns `true` if tracing has been shut down.
     *
     * @return `true` if tracing has been shut down.
     */
    fun hasBeenShutdown(): Boolean {
        return shutdownResult.value != null
    }

    /**
     * Stops tracing, including shutting down processors and set to `true` [ ][.hasBeenShutdown].
     *
     * @return a [CompletableResultCode] that will be completed when the span processor is shut
     * down.
     */
    fun shutdown(): CompletableResultCode {
        return shutdownResult.updateAndGet { it ?: activeSpanProcessor.shutdown() }!!
    }
}
