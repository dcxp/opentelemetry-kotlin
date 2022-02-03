/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.common.Clock
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.resources.Resource
import io.opentelemetry.kotlin.sdk.trace.samplers.Sampler
import io.opentelemetry.kotlin.semconv.resource.attributes.ResourceAttributes
import kotlin.test.Test

/** Unit tests for [SdkTracerProvider]. */
internal class SdkTracerProviderTest {
    private val spanProcessor = MockFactory.createSpanProcessor()
    private val tracerFactory: SdkTracerProvider =
        SdkTracerProvider.builder().addSpanProcessor(spanProcessor).build()

    @Test
    fun builder_defaultResource() {
        val resourceWithDefaults: Resource = Resource.default
        val tracerProvider =
            SdkTracerProvider.builder()
                .setClock(Clock.default)
                .setIdGenerator(IdGenerator.Companion.random())
                .build()
        tracerProvider.shouldNotBeNull()
        tracerProvider.sharedState.getResource() shouldBe resourceWithDefaults
    }

    @Test
    fun builder_defaultSampler() {
        SdkTracerProvider.builder().build().sampler shouldBe Sampler.parentBased(Sampler.alwaysOn())
    }

    @Test
    fun builder_configureSampler() {
        SdkTracerProvider.builder().setSampler(Sampler.alwaysOff()).build().sampler shouldBe
            Sampler.alwaysOff()
    }

    @Test
    fun builder_serviceNameProvided() {
        val resource =
            Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, "mySpecialService"))
        val tracerProvider =
            SdkTracerProvider.builder()
                .setClock(Clock.default)
                .setResource(resource)
                .setIdGenerator(IdGenerator.random())
                .build()
        tracerProvider.shouldNotBeNull()
        tracerProvider.sharedState.getResource() shouldBe resource
    }

    @Test
    fun defaultGet() {
        tracerFactory["test"]::class shouldBe SdkTracer::class
    }

    @Test
    fun sameInstanceForSameName_WithoutVersion() {
        tracerFactory["test"] shouldBe tracerFactory["test"]
        tracerFactory["test"] shouldBe tracerFactory.tracerBuilder("test").build()
    }

    @Test
    fun sameInstanceForSameName_WithVersion() {
        tracerFactory["test", "version"] shouldBe tracerFactory["test", "version"]
        tracerFactory["test", "version"] shouldBe
            tracerFactory.tracerBuilder("test").setInstrumentationVersion("version").build()
    }

    @Test
    fun sameInstanceForSameName_WithVersionAndSchema() {
        tracerFactory
            .tracerBuilder("test")
            .setInstrumentationVersion("version")
            .setSchemaUrl("http://url")
            .build() shouldBeSameInstanceAs
            tracerFactory
                .tracerBuilder("test")
                .setInstrumentationVersion("version")
                .setSchemaUrl("http://url")
                .build()
    }

    @Test
    fun propagatesInstrumentationLibraryInfoToTracer() {
        val expected: InstrumentationLibraryInfo =
            InstrumentationLibraryInfo.create("theName", "theVersion", "http://url")
        val tracer =
            tracerFactory
                .tracerBuilder(expected.name)
                .setInstrumentationVersion(expected.version)
                .setSchemaUrl(expected.schemaUrl)
                .build()
        (tracer as SdkTracer).getInstrumentationLibraryInfo() shouldBe expected
    }

    @Test
    fun build_SpanLimits() {
        val initialSpanLimits = SpanLimits.builder().build()
        val sdkTracerProvider = SdkTracerProvider.builder().setSpanLimits(initialSpanLimits).build()
        sdkTracerProvider.spanLimits shouldBe initialSpanLimits
    }

    @Test
    fun shutdown() {
        tracerFactory.shutdown()
        spanProcessor.shutdownCalled.shouldBeTrue()
    }

    @Test
    fun close() {
        tracerFactory.close()
        spanProcessor.shutdownCalled.shouldBeTrue()
    }

    @Test
    fun forceFlush() {
        tracerFactory.forceFlush()
        spanProcessor.flushCalled.shouldBeTrue()
    }

    @Test
    fun shutdownTwice_OnlyFlushSpanProcessorOnce() {
        tracerFactory.shutdown()
        spanProcessor.shutdownCalled.shouldBeTrue()
        spanProcessor.reset()
        tracerFactory.shutdown() // the second call will be ignore
        spanProcessor.shutdownCalled.shouldBeFalse()
    }

    @Test
    fun returnNoopSpanAfterShutdown() {
        tracerFactory.shutdown()
        val span = tracerFactory["noop"].spanBuilder("span").startSpan()
        span.spanContext.isValid.shouldBeFalse()
        span.end()
    }

    @Test
    fun suppliesDefaultTracerForEmptyName() {
        var tracer = tracerFactory[""] as SdkTracer
        tracer.getInstrumentationLibraryInfo().name shouldBe SdkTracerProvider.DEFAULT_TRACER_NAME
        tracer = tracerFactory["", ""] as SdkTracer
        tracer.getInstrumentationLibraryInfo().name shouldBe SdkTracerProvider.DEFAULT_TRACER_NAME
    }
}
