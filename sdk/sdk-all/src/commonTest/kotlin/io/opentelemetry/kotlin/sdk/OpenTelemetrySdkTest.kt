/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.opentelemetry.kotlin.api.GlobalOpenTelemetry
import io.opentelemetry.kotlin.api.OpenTelemetry
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.trace.SpanKind
import io.opentelemetry.kotlin.api.trace.TracerProvider
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.context.propagation.ContextPropagators
import io.opentelemetry.kotlin.context.propagation.TextMapGetter
import io.opentelemetry.kotlin.context.propagation.TextMapPropagator
import io.opentelemetry.kotlin.context.propagation.TextMapSetter
import io.opentelemetry.kotlin.sdk.common.Clock
import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.resources.Resource
import io.opentelemetry.kotlin.sdk.testing.time.TestClock
import io.opentelemetry.kotlin.sdk.trace.IdGenerator
import io.opentelemetry.kotlin.sdk.trace.SdkTracerProvider
import io.opentelemetry.kotlin.sdk.trace.SpanLimits
import io.opentelemetry.kotlin.sdk.trace.data.LinkData
import io.opentelemetry.kotlin.sdk.trace.data.SpanData
import io.opentelemetry.kotlin.sdk.trace.export.SimpleSpanProcessor
import io.opentelemetry.kotlin.sdk.trace.export.SpanExporter
import io.opentelemetry.kotlin.sdk.trace.samplers.Sampler
import io.opentelemetry.kotlin.sdk.trace.samplers.SamplingResult
import kotlin.test.AfterTest
import kotlin.test.Test

internal class OpenTelemetrySdkTest {
    private val tracerProvider: SdkTracerProvider = SdkTracerProvider.builder().build()

    private val propagators: ContextPropagators =
        object : ContextPropagators {
            override val textMapPropagator: TextMapPropagator
                get() = TODO("Not yet implemented")
        }

    private val clock = TestClock.create()

    @AfterTest
    fun tearDown() {
        GlobalOpenTelemetry.resetForTest()
    }

    @Test
    fun testRegisterGlobal() {
        val sdk = OpenTelemetrySdk.builder().setPropagators(propagators).buildAndRegisterGlobal()
        GlobalOpenTelemetry.get() shouldBe sdk
        sdk.tracerProvider[""] shouldBe GlobalOpenTelemetry.tracerProvider[""]
        sdk.tracerProvider[""] shouldBe GlobalOpenTelemetry.get().getTracer("")
        GlobalOpenTelemetry.propagators shouldBe GlobalOpenTelemetry.get().propagators
        GlobalOpenTelemetry.propagators shouldBe sdk.propagators
        GlobalOpenTelemetry.propagators shouldBe propagators
    }

    @Test
    fun castingGlobalToSdkFails() {
        OpenTelemetrySdk.builder().buildAndRegisterGlobal()
        shouldThrow<ClassCastException> {
            val telemetry = GlobalOpenTelemetry.get()
            telemetry as OpenTelemetrySdk
        }
    }

    @Test
    fun testShortcutVersions() {
        GlobalOpenTelemetry.getTracer("testTracer1") shouldBe
            GlobalOpenTelemetry.tracerProvider["testTracer1"]
        GlobalOpenTelemetry.getTracer("testTracer2", "testVersion") shouldBe
            GlobalOpenTelemetry.tracerProvider["testTracer2", "testVersion"]

        GlobalOpenTelemetry.tracerBuilder("testTracer2")
            .setInstrumentationVersion("testVersion")
            .setSchemaUrl("https://example.invalid")
            .build() shouldBe
            GlobalOpenTelemetry.tracerProvider
                .tracerBuilder("testTracer2")
                .setInstrumentationVersion("testVersion")
                .setSchemaUrl("https://example.invalid")
                .build()
    }

    @Test
    fun testBuilderDefaults() {
        val openTelemetry = OpenTelemetrySdk.builder().build()
        openTelemetry.tracerProvider.shouldBeInstanceOf<OpenTelemetrySdk.ObfuscatedTracerProvider>()
        (openTelemetry.tracerProvider as OpenTelemetrySdk.ObfuscatedTracerProvider)
            .unobfuscate()
            .shouldBeInstanceOf<SdkTracerProvider>()
    }

    @Test
    fun building() {
        val openTelemetry =
            OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .setPropagators(propagators)
                .build()
        (openTelemetry.tracerProvider as OpenTelemetrySdk.ObfuscatedTracerProvider)
            .unobfuscate() shouldBe tracerProvider
        openTelemetry.sdkTracerProvider shouldBe tracerProvider
        openTelemetry.propagators shouldBe propagators
    }

    @Test
    fun testConfiguration_tracerSettings() {
        val resource = Resource.create(Attributes.builder().put("cat", "meow").build())
        val idGenerator: IdGenerator =
            object : IdGenerator {
                override fun generateSpanId(): String {
                    TODO("Not yet implemented")
                }

                override fun generateTraceId(): String {
                    TODO("Not yet implemented")
                }
            }
        val spanLimits: SpanLimits = SpanLimits.default
        val openTelemetry =
            OpenTelemetrySdk.builder()
                .setTracerProvider(
                    SdkTracerProvider.builder()
                        .setClock(clock)
                        .setResource(resource)
                        .setIdGenerator(idGenerator)
                        .setSpanLimits(spanLimits)
                        .build()
                )
                .build()
        val unobfuscatedTracerProvider: TracerProvider =
            (openTelemetry.tracerProvider as OpenTelemetrySdk.ObfuscatedTracerProvider)
                .unobfuscate()
        unobfuscatedTracerProvider.shouldBeInstanceOf<SdkTracerProvider>()
        (unobfuscatedTracerProvider as SdkTracerProvider).spanLimits shouldBe spanLimits
        // Since TracerProvider is in a different package, the only alternative to this reflective
        // approach would be to make the fields public for testing which is worse than this.
        //        assertThat(unobfuscatedTracerProvider)
        //            .extracting("sharedState")
        //            .hasFieldOrPropertyWithValue("clock", clock)
        //            .hasFieldOrPropertyWithValue("resource", resource)
        //            .hasFieldOrPropertyWithValue("idGenerator", idGenerator)
    }

    @Test
    fun testTracerBuilder() {
        val openTelemetry = OpenTelemetrySdk.builder().build()
        openTelemetry.tracerBuilder("instr") shouldNotBe OpenTelemetry.noop().tracerBuilder("instr")
    }

    @Test
    fun testTracerBuilderViaProvider() {
        val openTelemetry = OpenTelemetrySdk.builder().build()
        openTelemetry.tracerProvider.tracerBuilder("instr") shouldNotBe
            OpenTelemetry.noop().tracerBuilder("instr")
    }

    @Test
    fun testTracerProviderAccess() {
        val openTelemetry = OpenTelemetrySdk.builder().setTracerProvider(tracerProvider).build()
        (openTelemetry.tracerProvider as OpenTelemetrySdk.ObfuscatedTracerProvider)
            .unobfuscate() shouldBe tracerProvider
        openTelemetry.sdkTracerProvider.shouldNotBeNull()
    }

    // This is just a demonstration of maximum that one can do with OpenTelemetry configuration.
    // Demonstrates how clear or confusing is SDK configuration
    @Test
    fun fullOpenTelemetrySdkConfigurationDemo() {
        val newConfig = SpanLimits.builder().setMaxNumberOfAttributes(512).build()
        val sdkBuilder =
            OpenTelemetrySdk.builder()
                .setTracerProvider(
                    SdkTracerProvider.builder()
                        .setSampler(SAMPLER)
                        .addSpanProcessor(SimpleSpanProcessor.create(SPAN_EXPORTER))
                        .addSpanProcessor(SimpleSpanProcessor.create(SPAN_EXPORTER))
                        .setClock(CLOCK)
                        .setIdGenerator(ID_GENERATOR)
                        .setResource(Resource.empty())
                        .setSpanLimits(newConfig)
                        .build()
                )
        sdkBuilder.build()
    }

    // This is just a demonstration of the bare minimal required configuration in order to get
    // useful
    // SDK.
    // Demonstrates how clear or confusing is SDK configuration
    @Test
    fun trivialOpenTelemetrySdkConfigurationDemo() {
        OpenTelemetrySdk.builder()
            .setTracerProvider(
                SdkTracerProvider.builder()
                    .addSpanProcessor(SimpleSpanProcessor.create(SPAN_EXPORTER))
                    .build()
            )
            .setPropagators(ContextPropagators.create(TEXT_MAP_PROPAGATOR))
            .build()
    }

    // This is just a demonstration of two small but not trivial configurations.
    // Demonstrates how clear or confusing is SDK configuration
    @Test
    fun minimalOpenTelemetrySdkConfigurationDemo() {
        OpenTelemetrySdk.builder()
            .setTracerProvider(
                SdkTracerProvider.builder()
                    .addSpanProcessor(SimpleSpanProcessor.create(SPAN_EXPORTER))
                    .setSampler(SAMPLER)
                    .build()
            )
            .setPropagators(ContextPropagators.create(TEXT_MAP_PROPAGATOR))
            .build()
        OpenTelemetrySdk.builder()
            .setTracerProvider(
                SdkTracerProvider.builder()
                    .addSpanProcessor(SimpleSpanProcessor.create(SPAN_EXPORTER))
                    .setSampler(SAMPLER)
                    .setIdGenerator(ID_GENERATOR)
                    .build()
            )
            .setPropagators(ContextPropagators.create(TEXT_MAP_PROPAGATOR))
            .build()
    }
    companion object {
        val CLOCK =
            object : Clock {
                override fun now(): Long {
                    TODO("Not yet implemented")
                }

                override fun nanoTime(): Long {
                    TODO("Not yet implemented")
                }
            }
        val SPAN_EXPORTER =
            object : SpanExporter {
                override fun export(spans: Collection<SpanData>): CompletableResultCode {
                    TODO("Not yet implemented")
                }

                override fun flush(): CompletableResultCode {
                    TODO("Not yet implemented")
                }

                override fun shutdown(): CompletableResultCode {
                    TODO("Not yet implemented")
                }
            }

        val TEXT_MAP_PROPAGATOR =
            object : TextMapPropagator {
                override fun fields(): Collection<String> {
                    TODO("Not yet implemented")
                }

                override fun <C> inject(context: Context, carrier: C, setter: TextMapSetter<C>) {
                    TODO("Not yet implemented")
                }

                override fun <C> extract(
                    context: Context,
                    carrier: C,
                    getter: TextMapGetter<C>
                ): Context {
                    TODO("Not yet implemented")
                }
            }
        val ID_GENERATOR =
            object : IdGenerator {
                override fun generateSpanId(): String {
                    TODO("Not yet implemented")
                }

                override fun generateTraceId(): String {
                    TODO("Not yet implemented")
                }
            }
        val SAMPLER =
            object : Sampler {
                override fun shouldSample(
                    parentContext: Context,
                    traceId: String,
                    name: String,
                    spanKind: SpanKind,
                    attributes: Attributes,
                    parentLinks: List<LinkData>
                ): SamplingResult {
                    TODO("Not yet implemented")
                }

                override val description: String
                    get() = TODO("Not yet implemented")
            }
    }
}
