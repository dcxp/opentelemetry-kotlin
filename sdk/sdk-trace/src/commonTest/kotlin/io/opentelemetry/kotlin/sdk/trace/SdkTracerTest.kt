/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.trace.data.SpanData
import io.opentelemetry.kotlin.sdk.trace.export.SpanExporter
import io.opentelemetry.kotlin.use
import kotlinx.atomicfu.atomic
import kotlin.test.Test

internal class SdkTracerTest {
    private val tracer =
        SdkTracerProvider.builder()
            .build()
            .tracerBuilder(INSTRUMENTATION_LIBRARY_NAME)
            .setInstrumentationVersion(INSTRUMENTATION_LIBRARY_VERSION)
            .setSchemaUrl("http://schemaurl")
            .build() as
            SdkTracer

    @Test
    fun defaultSpanBuilder() {
        tracer.spanBuilder(SPAN_NAME)::class shouldBe SdkSpanBuilder::class
    }

    @Test
    fun instrumentationLibraryInfo() {
        tracer.getInstrumentationLibraryInfo() shouldBe Companion.instrumentationLibraryInfo
    }

    @Test
    fun propagatesInstrumentationLibraryInfoToSpan() {
        val readableSpan = tracer.spanBuilder("spanName").startSpan() as ReadableSpan
        readableSpan.instrumentationLibraryInfo shouldBe Companion.instrumentationLibraryInfo
    }

    @Test
    fun fallbackSpanName() {
        var readableSpan = tracer.spanBuilder("  ").startSpan() as ReadableSpan
        readableSpan.name shouldBe SdkTracer.FALLBACK_SPAN_NAME
    }
    // TODO Fix stress test
    /*
        @Test
        fun stressTest() = runTest {
            val spanProcessor = CountingSpanProcessor()
            val sdkTracerProvider = SdkTracerProvider.builder().addSpanProcessor(spanProcessor).build()
            val tracer =
                sdkTracerProvider[INSTRUMENTATION_LIBRARY_NAME, INSTRUMENTATION_LIBRARY_VERSION] as
                    SdkTracer
            var stressTestBuilder: StressTestRunner.Builder =
                StressTestRunner.builder().setTracer(tracer).setSpanProcessor(spanProcessor)
            for (i in 0..3) {
                stressTestBuilder = stressTestBuilder.addOperation(
                    StressTestRunner.Operation.create(200, 1, SimpleSpanOperation(tracer))
                )
            }
            stressTestBuilder.build().run()
            spanProcessor.numberOfSpansFinished.value shouldBe 800L
            spanProcessor.numberOfSpansStarted.value shouldBe 800L
        }
        @Test
        fun stressTest_withBatchSpanProcessor() = runTest {
            val countingSpanExporter = CountingSpanExporter()
            val spanProcessor: SpanProcessor = BatchSpanProcessor.builder(countingSpanExporter).build()
            val sdkTracerProvider = SdkTracerProvider.builder().addSpanProcessor(spanProcessor).build()
            val tracer =
                sdkTracerProvider[INSTRUMENTATION_LIBRARY_NAME, INSTRUMENTATION_LIBRARY_VERSION] as
                    SdkTracer
            var stressTestBuilder: StressTestRunner.Builder =
                StressTestRunner.builder().setTracer(tracer).setSpanProcessor(spanProcessor)
            for (i in 0..3) {
                stressTestBuilder = stressTestBuilder.addOperation(
                    StressTestRunner.Operation.create(2000, 1, SimpleSpanOperation(tracer))
                )
            }

            // Needs to correlate with the BatchSpanProcessor.Builder's default, which is the only thing
            // this test can guarantee
            val defaultMaxQueueSize = BatchSpanProcessorBuilder.DEFAULT_MAX_QUEUE_SIZE
            stressTestBuilder.build().run()
            countingSpanExporter.numberOfSpansExported.value shouldBeGreaterThanOrEqual
                defaultMaxQueueSize.toLong()
        }
    */
    private class CountingSpanProcessor : SpanProcessor {
        val numberOfSpansStarted = atomic(0L)
        val numberOfSpansFinished = atomic(0L)

        override fun onStart(parentContext: Context, span: ReadWriteSpan) {
            numberOfSpansStarted.incrementAndGet()
        }

        override fun isStartRequired(): Boolean {
            return true
        }

        override fun onEnd(span: ReadableSpan) {
            numberOfSpansFinished.incrementAndGet()
        }

        override fun isEndRequired(): Boolean {
            return true
        }
    }

    private class SimpleSpanOperation(private val tracer: SdkTracer) :
        StressTestRunner.OperationUpdater {
        override fun update() {
            val span = tracer.spanBuilder("testSpan").startSpan()
            try {
                span.makeCurrent().use { span.setAttribute("testAttribute", "testValue") }
            } finally {
                span.end()
            }
        }
    }

    private class CountingSpanExporter : SpanExporter {
        val numberOfSpansExported = atomic(0L)

        override fun export(spans: Collection<SpanData>): CompletableResultCode {
            numberOfSpansExported.addAndGet(spans.size.toLong())
            return CompletableResultCode.ofSuccess()
        }

        override fun flush(): CompletableResultCode {
            return CompletableResultCode.ofSuccess()
        }

        override fun shutdown(): CompletableResultCode {
            // no-op
            return CompletableResultCode.ofSuccess()
        }
    }

    companion object {
        private const val SPAN_NAME = "span_name"
        private const val INSTRUMENTATION_LIBRARY_NAME = "io.opentelemetry.kotlin.sdk.trace.TracerSdkTest"
        private const val INSTRUMENTATION_LIBRARY_VERSION = "0.2.0"
        private val instrumentationLibraryInfo: InstrumentationLibraryInfo =
            InstrumentationLibraryInfo.create(
                INSTRUMENTATION_LIBRARY_NAME,
                INSTRUMENTATION_LIBRARY_VERSION,
                "http://schemaurl"
            )
    }
}
