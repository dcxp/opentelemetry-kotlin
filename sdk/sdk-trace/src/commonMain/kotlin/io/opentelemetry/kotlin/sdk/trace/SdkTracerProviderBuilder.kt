/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.opentelemetry.kotlin.sdk.common.Clock
import io.opentelemetry.kotlin.sdk.resources.Resource
import io.opentelemetry.kotlin.sdk.trace.samplers.Sampler

/** Builder of [SdkTracerProvider]. */
class SdkTracerProviderBuilder internal constructor() {
    private val spanProcessors: MutableList<SpanProcessor> = mutableListOf()
    private var clock: Clock = Clock.default
    private var idsGenerator: IdGenerator = IdGenerator.random()
    private var resource: Resource = Resource.default
    private var spanLimitsSupplier: () -> SpanLimits = SpanLimits.Companion::default
    private var sampler: Sampler = DEFAULT_SAMPLER

    /**
     * Assign a [Clock]. [Clock] will be used each time a [ ] is started, ended or any event is
     * recorded.
     *
     * The `clock` must be thread-safe and return immediately (no remote calls, as contention free
     * as possible).
     *
     * @param clock The clock to use for all temporal needs.
     * @return this
     */
    fun setClock(clock: Clock): SdkTracerProviderBuilder {
        this.clock = clock
        return this
    }

    /**
     * Assign an [IdGenerator]. [IdGenerator] will be used each time a [ ] is started.
     *
     * The `idGenerator` must be thread-safe and return immediately (no remote calls, as contention
     * free as possible).
     *
     * @param idGenerator A generator for trace and span ids.
     * @return this
     */
    fun setIdGenerator(idGenerator: IdGenerator): SdkTracerProviderBuilder {
        idsGenerator = idGenerator
        return this
    }

    /**
     * Assign a [Resource] to be attached to all Spans created by Tracers.
     *
     * @param resource A Resource implementation.
     * @return this
     */
    fun setResource(resource: Resource): SdkTracerProviderBuilder {
        this.resource = resource
        return this
    }

    /**
     * Assign an initial [SpanLimits] that should be used with this SDK.
     *
     * This method is equivalent to calling [.setSpanLimits] like this `#setSpanLimits(() ->
     * spanLimits)`.
     *
     * @param spanLimits the limits that will be used for every [ ].
     * @return this
     */
    fun setSpanLimits(spanLimits: SpanLimits): SdkTracerProviderBuilder {
        spanLimitsSupplier = { spanLimits }
        return this
    }

    /**
     * Assign a [Supplier] of [SpanLimits]. [SpanLimits] will be retrieved each time a
     * [io.opentelemetry.kotlin.api.trace.Span] is started.
     *
     * The `spanLimitsSupplier` must be thread-safe and return immediately (no remote calls, as
     * contention free as possible).
     *
     * @param spanLimitsSupplier the supplier that will be used to retrieve the [SpanLimits] for
     * every [io.opentelemetry.kotlin.api.trace.Span].
     * @return this
     */
    fun setSpanLimits(spanLimitsSupplier: () -> SpanLimits): SdkTracerProviderBuilder {
        this.spanLimitsSupplier = spanLimitsSupplier
        return this
    }

    /**
     * Assign a [Sampler] to use for sampling traces. [Sampler] will be called each time a
     * [io.opentelemetry.kotlin.api.trace.Span] is started.
     *
     * The `sampler` must be thread-safe and return immediately (no remote calls, as contention free
     * as possible).
     *
     * @param sampler the [Sampler] to use for sampling traces.
     * @return this
     */
    fun setSampler(sampler: Sampler): SdkTracerProviderBuilder {
        this.sampler = sampler
        return this
    }

    /**
     * Add a SpanProcessor to the span pipeline that will be built. [SpanProcessor] will be called
     * each time a [io.opentelemetry.kotlin.api.trace.Span] is started or ended.
     *
     * The `spanProcessor` must be thread-safe and return immediately (no remote calls, as
     * contention free as possible).
     *
     * @param spanProcessor the processor to be added to the processing pipeline.
     * @return this
     */
    fun addSpanProcessor(spanProcessor: SpanProcessor): SdkTracerProviderBuilder {
        spanProcessors.add(spanProcessor)
        return this
    }

    /**
     * Create a new TraceSdkProvider instance.
     *
     * @return An initialized TraceSdkProvider.
     */
    fun build(): SdkTracerProvider {
        return SdkTracerProvider(
            clock,
            idsGenerator,
            resource,
            spanLimitsSupplier,
            sampler,
            spanProcessors
        )
    }

    companion object {
        private val DEFAULT_SAMPLER: Sampler = Sampler.parentBased(Sampler.alwaysOn())
    }
}
