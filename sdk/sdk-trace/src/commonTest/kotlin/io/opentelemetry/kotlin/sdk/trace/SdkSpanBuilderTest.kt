/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.booleanArrayKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.booleanKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.doubleArrayKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.doubleKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.longArrayKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.longKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.stringArrayKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.stringKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.common.normalizeToNanos
import io.opentelemetry.kotlin.api.trace.Span
import io.opentelemetry.kotlin.api.trace.SpanContext
import io.opentelemetry.kotlin.api.trace.SpanId
import io.opentelemetry.kotlin.api.trace.SpanKind
import io.opentelemetry.kotlin.api.trace.StatusCode
import io.opentelemetry.kotlin.api.trace.TraceFlags
import io.opentelemetry.kotlin.api.trace.TraceState
import io.opentelemetry.kotlin.api.trace.TracerProvider
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.trace.data.LinkData
import io.opentelemetry.kotlin.sdk.trace.samplers.Sampler
import io.opentelemetry.kotlin.sdk.trace.samplers.SamplingDecision
import io.opentelemetry.kotlin.sdk.trace.samplers.SamplingResult
import io.opentelemetry.kotlin.use
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlin.test.Test

class SdkSpanBuilderTest {
    private val sampledSpanContext =
        SpanContext.create(
            "12345678876543211234567887654321",
            "8765432112345678",
            TraceFlags.sampled,
            TraceState.default
        )

    private val mockedSpanProcessor = MockFactory.createSpanProcessor()
    private val sdkTracer: SdkTracer

    init {
        val tracerSdkFactory =
            SdkTracerProvider.builder().addSpanProcessor(mockedSpanProcessor).build()
        sdkTracer = tracerSdkFactory["SpanBuilderSdkTest"] as SdkTracer
    }

    @Test
    fun addLink() {
        // Verify methods do not crash.
        val spanBuilder = sdkTracer.spanBuilder(SPAN_NAME)
        spanBuilder.addLink(sampledSpanContext)
        spanBuilder.addLink(sampledSpanContext, Attributes.empty())
        val span = spanBuilder.startSpan() as RecordEventsReadableSpan
        try {
            span.toSpanData().links shouldHaveSize 2
        } finally {
            span.end()
        }
    }

    @Test
    fun addLink_invalid() {
        // Verify methods do not crash.
        val spanBuilder = sdkTracer.spanBuilder(SPAN_NAME)
        spanBuilder.addLink(Span.invalid().spanContext)
        spanBuilder.addLink(Span.invalid().spanContext, Attributes.empty())
        val span = spanBuilder.startSpan() as RecordEventsReadableSpan
        try {
            span.toSpanData().links.shouldBeEmpty()
        } finally {
            span.end()
        }
    }

    @Test
    fun truncateLink() {
        val maxNumberOfLinks = 8
        val spanLimits = SpanLimits.builder().setMaxNumberOfLinks(maxNumberOfLinks).build()
        val tracerProvider: TracerProvider =
            SdkTracerProvider.builder().setSpanLimits(spanLimits).build()
        // Verify methods do not crash.
        val spanBuilder = tracerProvider["test"].spanBuilder(SPAN_NAME)
        for (i in 0 until 2 * maxNumberOfLinks) {
            spanBuilder.addLink(sampledSpanContext)
        }
        val span = spanBuilder.startSpan() as RecordEventsReadableSpan
        try {
            val spanData = span.toSpanData()
            val links = spanData.links
            links shouldHaveSize maxNumberOfLinks
            for (i in 0 until maxNumberOfLinks) {
                links[i] shouldBe LinkData.create(sampledSpanContext)
                spanData.totalRecordedLinks shouldBe 2 * maxNumberOfLinks
            }
        } finally {
            span.end()
        }
    }

    @Test
    fun truncateLinkAttributes() {
        val spanLimits = SpanLimits.builder().setMaxNumberOfAttributesPerLink(1).build()
        val tracerProvider: TracerProvider =
            SdkTracerProvider.builder().setSpanLimits(spanLimits).build()
        // Verify methods do not crash.
        val spanBuilder = tracerProvider["test"].spanBuilder(SPAN_NAME)
        val attributes =
            Attributes.of(
                stringKey("key0"),
                "str",
                stringKey("key1"),
                "str",
                stringKey("key2"),
                "str"
            )
        spanBuilder.addLink(sampledSpanContext, attributes)
        val span = spanBuilder.startSpan() as RecordEventsReadableSpan
        try {
            span.toSpanData().links shouldBe
                listOf(
                    LinkData.create(sampledSpanContext, Attributes.of(stringKey("key0"), "str"), 3)
                )
        } finally {
            span.end()
        }
    }

    @Test
    fun linkAttributeLength() {
        val maxLength = 25
        val tracerProvider: TracerProvider =
            SdkTracerProvider.builder()
                .setSpanLimits(SpanLimits.builder().setMaxAttributeValueLength(maxLength).build())
                .build()
        val spanBuilder = tracerProvider["test"].spanBuilder(SPAN_NAME)
        val strVal: String = (0 until maxLength).joinToString(separator = "") { "a" }
        val tooLongStrVal = strVal + strVal
        var attributes: Attributes =
            Attributes.builder()
                .put("string", tooLongStrVal)
                .put("boolean", true)
                .put("long", 1L)
                .put("double", 1.0)
                .put(stringArrayKey("stringArray"), listOf(strVal, tooLongStrVal))
                .put(booleanArrayKey("booleanArray"), listOf(true, false))
                .put(longArrayKey("longArray"), listOf(1L, 2L))
                .put(doubleArrayKey("doubleArray"), listOf(1.0, 2.0))
                .build()
        spanBuilder.addLink(sampledSpanContext, attributes)
        val span = spanBuilder.startSpan() as RecordEventsReadableSpan
        try {
            attributes = span.toSpanData().links[0].attributes
            attributes[stringKey("string")] shouldBe strVal
            attributes[booleanKey("boolean")] shouldBe true
            attributes[longKey("long")] shouldBe 1L
            attributes[doubleKey("double")] shouldBe 1.0
            attributes[stringArrayKey("stringArray")] shouldBe listOf(strVal, strVal)
            attributes[booleanArrayKey("booleanArray")] shouldBe listOf(true, false)
            attributes[longArrayKey("longArray")] shouldBe listOf(1L, 2L)
            attributes[doubleArrayKey("doubleArray")] shouldBe listOf(1.0, 2.0)
        } finally {
            span.end()
        }
    }

    @Test
    fun addLink_NoEffectAfterStartSpan() {
        val spanBuilder = sdkTracer.spanBuilder(SPAN_NAME)
        spanBuilder.addLink(sampledSpanContext)
        val span = spanBuilder.startSpan() as RecordEventsReadableSpan
        try {
            span.toSpanData().links shouldBe
                listOf(LinkData.create(sampledSpanContext, Attributes.empty()))
            // Use a different sampledSpanContext to ensure no logic that avoids duplicate links
            // makes
            // this test to pass.
            spanBuilder.addLink(
                SpanContext.create(
                    "00000000000004d20000000000001a85",
                    "0000000000002694",
                    TraceFlags.sampled,
                    TraceState.default
                )
            )
            span.toSpanData().links shouldBe
                listOf(LinkData.create(sampledSpanContext, Attributes.empty()))
        } finally {
            span.end()
        }
    }

    @Test
    fun setAttribute() {
        val spanBuilder =
            sdkTracer
                .spanBuilder(SPAN_NAME)
                .setAttribute("string", "value")
                .setAttribute("long", 12345L)
                .setAttribute("double", .12345)
                .setAttribute("boolean", true)
                .setAttribute(stringKey("stringAttribute"), "attrvalue")
        val span = spanBuilder.startSpan() as RecordEventsReadableSpan
        try {
            val spanData = span.toSpanData()
            val attrs = spanData.attributes
            attrs.size shouldBe 5
            attrs[stringKey("string")] shouldBe "value"
            attrs[longKey("long")] shouldBe 12345L
            attrs[doubleKey("double")] shouldBe 0.12345
            attrs[booleanKey("boolean")] shouldBe true
            attrs[stringKey("stringAttribute")] shouldBe "attrvalue"
            spanData.totalAttributeCount shouldBe 5
        } finally {
            span.end()
        }
    }

    @Test
    fun setAttribute_afterEnd() {
        val spanBuilder = sdkTracer.spanBuilder(SPAN_NAME)
        spanBuilder.setAttribute("string", "value")
        spanBuilder.setAttribute("long", 12345L)
        spanBuilder.setAttribute("double", .12345)
        spanBuilder.setAttribute("boolean", true)
        spanBuilder.setAttribute(stringKey("stringAttribute"), "attrvalue")
        val span = spanBuilder.startSpan() as RecordEventsReadableSpan
        try {
            val attrs = span.toSpanData().attributes
            attrs.size shouldBe 5
            attrs[stringKey("string")] shouldBe "value"
            attrs[longKey("long")] shouldBe 12345L
            attrs[doubleKey("double")] shouldBe 0.12345
            attrs[booleanKey("boolean")] shouldBe true
            attrs[stringKey("stringAttribute")] shouldBe "attrvalue"
        } finally {
            span.end()
        }
        span.setAttribute("string2", "value")
        span.setAttribute("long2", 12345L)
        span.setAttribute("double2", .12345)
        span.setAttribute("boolean2", true)
        span.setAttribute(stringKey("stringAttribute2"), "attrvalue")
        val attrs = span.toSpanData().attributes
        attrs.size shouldBe 5
        attrs[stringKey("string2")].shouldBeNull()
        attrs[longKey("long2")].shouldBeNull()
        attrs[doubleKey("double2")].shouldBeNull()
        attrs[booleanKey("boolean2")].shouldBeNull()
        attrs[stringKey("stringAttribute2")].shouldBeNull()
    }

    @Test
    fun setAttribute_emptyArrayAttributeValue() {
        val spanBuilder = sdkTracer.spanBuilder(SPAN_NAME)
        spanBuilder.setAttribute(stringArrayKey("stringArrayAttribute"), listOf())
        spanBuilder.setAttribute(booleanArrayKey("boolArrayAttribute"), listOf())
        spanBuilder.setAttribute(longArrayKey("longArrayAttribute"), listOf())
        spanBuilder.setAttribute(doubleArrayKey("doubleArrayAttribute"), listOf())
        val span = spanBuilder.startSpan() as RecordEventsReadableSpan
        span.toSpanData().attributes.size shouldBe 4
    }

    @Test
    fun setAttribute_nullStringValue() {
        val spanBuilder = sdkTracer.spanBuilder(SPAN_NAME)
        spanBuilder.setAttribute("emptyString", "")
        spanBuilder.setAttribute(stringKey("emptyStringAttributeValue"), "")
        val span = spanBuilder.startSpan() as RecordEventsReadableSpan
        span.toSpanData().attributes.size shouldBe 2
    }

    @Test
    fun setAttribute_NoEffectAfterStartSpan() {
        val spanBuilder = sdkTracer.spanBuilder(SPAN_NAME)
        spanBuilder.setAttribute("key1", "value1")
        spanBuilder.setAttribute("key2", "value2")
        val span = spanBuilder.startSpan() as RecordEventsReadableSpan
        val beforeAttributes = span.toSpanData().attributes
        beforeAttributes.size shouldBe 2
        beforeAttributes[stringKey("key1")] shouldBe "value1"
        beforeAttributes[stringKey("key2")] shouldBe "value2"
        spanBuilder.setAttribute("key3", "value3")
        val afterAttributes = span.toSpanData().attributes
        afterAttributes.size shouldBe 2
        afterAttributes[stringKey("key1")] shouldBe "value1"
        afterAttributes[stringKey("key2")] shouldBe "value2"
    }

    @Test
    fun droppingAttributes() {
        val maxNumberOfAttrs = 8
        val spanLimits = SpanLimits.builder().setMaxNumberOfAttributes(maxNumberOfAttrs).build()
        val tracerProvider: TracerProvider =
            SdkTracerProvider.builder().setSpanLimits(spanLimits).build()
        // Verify methods do not crash.
        val spanBuilder = tracerProvider["test"].spanBuilder(SPAN_NAME)
        for (i in 0 until 2 * maxNumberOfAttrs) {
            spanBuilder.setAttribute("key$i", i.toLong())
        }
        val span = spanBuilder.startSpan() as RecordEventsReadableSpan
        try {
            val attrs = span.toSpanData().attributes
            attrs.size shouldBe maxNumberOfAttrs
            for (i in 0 until maxNumberOfAttrs) {
                attrs[longKey("key$i")] shouldBe i
            }
        } finally {
            span.end()
        }
    }

    @Test
    fun addAttributes_OnlyViaSampler() {
        val sampler: Sampler =
            object : Sampler {
                override fun shouldSample(
                    parentContext: Context,
                    traceId: String,
                    name: String,
                    spanKind: SpanKind,
                    attributes: Attributes,
                    parentLinks: List<LinkData>
                ): SamplingResult {
                    return SamplingResult.create(
                        SamplingDecision.RECORD_AND_SAMPLE,
                        Attributes.builder().put("cat", "meow").build()
                    )
                }

                override val description: String
                    get() = "test"
            }
        val tracerProvider: TracerProvider = SdkTracerProvider.builder().setSampler(sampler).build()
        // Verify methods do not crash.
        val spanBuilder = tracerProvider["test"].spanBuilder(SPAN_NAME)
        val span = spanBuilder.startSpan() as RecordEventsReadableSpan
        span.end()
        span.toSpanData().attributes.size shouldBe 1
        span.toSpanData().attributes[stringKey("cat")] shouldBe "meow"
    }

    @Test
    fun setAllAttributes() {
        val attributes =
            Attributes.builder()
                .put("string", "value")
                .put("long", 12345L)
                .put("double", .12345)
                .put("boolean", true)
                .put(stringKey("stringAttribute"), "attrvalue")
                .build()
        val spanBuilder = sdkTracer.spanBuilder(SPAN_NAME).setAllAttributes(attributes)
        val span = spanBuilder.startSpan() as RecordEventsReadableSpan
        try {
            val spanData = span.toSpanData()
            val attrs = spanData.attributes
            attrs.size shouldBe 5
            attrs[stringKey("string")] shouldBe "value"
            attrs[longKey("long")] shouldBe 12345L
            attrs[doubleKey("double")] shouldBe 0.12345
            attrs[booleanKey("boolean")] shouldBe true
            attrs[stringKey("stringAttribute")] shouldBe "attrvalue"
            spanData.totalAttributeCount shouldBe 5
        } finally {
            span.end()
        }
    }

    @Test
    fun setAllAttributes_mergesAttributes() {
        val attributes =
            Attributes.builder()
                .put("string", "value")
                .put("long", 12345L)
                .put("double", .12345)
                .put("boolean", true)
                .put(stringKey("stringAttribute"), "attrvalue")
                .build()
        val spanBuilder =
            sdkTracer
                .spanBuilder(SPAN_NAME)
                .setAttribute("string", "otherValue")
                .setAttribute("boolean", false)
                .setAttribute("existingString", "existingValue")
                .setAllAttributes(attributes)
        val span = spanBuilder.startSpan() as RecordEventsReadableSpan
        try {
            val spanData = span.toSpanData()
            val attrs = spanData.attributes
            attrs.size shouldBe 6
            attrs[stringKey("string")] shouldBe "value"
            attrs[stringKey("existingString")] shouldBe "existingValue"
            attrs[longKey("long")] shouldBe 12345L
            attrs[doubleKey("double")] shouldBe 0.12345
            attrs[booleanKey("boolean")] shouldBe true
            attrs[stringKey("stringAttribute")] shouldBe "attrvalue"
            spanData.totalAttributeCount shouldBe 8
        } finally {
            span.end()
        }
    }

    @Test
    fun setAllAttributes_emptyAttributes() {
        val spanBuilder = sdkTracer.spanBuilder(SPAN_NAME).setAllAttributes(Attributes.empty())
        val span = spanBuilder.startSpan() as RecordEventsReadableSpan
        try {
            val spanData = span.toSpanData()
            val attrs = spanData.attributes
            attrs.size shouldBe 0
            spanData.totalAttributeCount shouldBe 0
        } finally {
            span.end()
        }
    }

    @Test
    fun recordEvents_default() {
        val span = sdkTracer.spanBuilder(SPAN_NAME).startSpan()
        try {
            span.isRecording().shouldBeTrue()
        } finally {
            span.end()
        }
    }

    @Test
    fun kind_default() {
        val span = sdkTracer.spanBuilder(SPAN_NAME).startSpan() as RecordEventsReadableSpan
        try {
            span.toSpanData().kind shouldBe SpanKind.INTERNAL
        } finally {
            span.end()
        }
    }

    @Test
    fun kind() {
        val span =
            sdkTracer.spanBuilder(SPAN_NAME).setSpanKind(SpanKind.CONSUMER).startSpan() as
                RecordEventsReadableSpan
        try {
            span.toSpanData().kind shouldBe SpanKind.CONSUMER
        } finally {
            span.end()
        }
    }

    @Test
    fun sampler() {
        val span =
            SdkTracerProvider.builder().setSampler(Sampler.alwaysOff()).build()["test"].spanBuilder(
                    SPAN_NAME
                )
                .startSpan()
        try {
            span.spanContext.isSampled().shouldBeFalse()
        } finally {
            span.end()
        }
    }

    @Test
    fun sampler_decisionAttributes() {
        val samplerAttributeName = "sampler-attribute"
        val samplerAttributeKey: AttributeKey<String> = stringKey(samplerAttributeName)
        val span =
            SdkTracerProvider.builder()
                    .setSampler(
                        object : Sampler {
                            override fun shouldSample(
                                parentContext: Context,
                                traceId: String,
                                name: String,
                                spanKind: SpanKind,
                                attributes: Attributes,
                                parentLinks: List<LinkData>
                            ): SamplingResult {
                                return object : SamplingResult {
                                    override val decision: SamplingDecision
                                        get() = SamplingDecision.RECORD_AND_SAMPLE
                                    override val attributes: Attributes
                                        get() = Attributes.of(samplerAttributeKey, "bar")
                                }
                            }
                            override val description: String
                                get() = "test sampler"
                        }
                    )
                    .addSpanProcessor(mockedSpanProcessor)
                    .build()["test"]
                .spanBuilder(SPAN_NAME)
                .setAttribute(samplerAttributeKey, "none")
                .startSpan() as
                RecordEventsReadableSpan
        try {
            span.spanContext.isSampled().shouldBeTrue()
            span.toSpanData().attributes[samplerAttributeKey].shouldNotBeNull()
            span.toSpanData().spanContext.traceState shouldBe TraceState.default
        } finally {
            span.end()
        }
    }

    @Test
    fun sampler_updatedTraceState() {
        val samplerAttributeName = "sampler-attribute"
        val samplerAttributeKey: AttributeKey<String> = stringKey(samplerAttributeName)
        val span =
            SdkTracerProvider.builder()
                    .setSampler(
                        object : Sampler {
                            override fun shouldSample(
                                parentContext: Context,
                                traceId: String,
                                name: String,
                                spanKind: SpanKind,
                                attributes: Attributes,
                                parentLinks: List<LinkData>
                            ): SamplingResult {
                                return object : SamplingResult {
                                    override val decision: SamplingDecision
                                        get() = SamplingDecision.RECORD_AND_SAMPLE
                                    override val attributes: Attributes
                                        get() = Attributes.empty()

                                    override fun getUpdatedTraceState(
                                        parentTraceState: TraceState
                                    ): TraceState {
                                        return parentTraceState
                                            .toBuilder()
                                            .put("newkey", "newValue")
                                            .build()
                                    }
                                }
                            }

                            override val description: String
                                get() = "test sampler"
                        }
                    )
                    .build()["test"]
                .spanBuilder(SPAN_NAME)
                .setAttribute(samplerAttributeKey, "none")
                .startSpan() as
                RecordEventsReadableSpan
        try {
            span.spanContext.isSampled().shouldBeTrue()
            span.toSpanData().attributes[samplerAttributeKey].shouldNotBeNull()
            span.toSpanData().spanContext.traceState shouldBe
                TraceState.builder().put("newkey", "newValue").build()
        } finally {
            span.end()
        }
    }

    // TODO(anuraaga): Is this test correct? It's not sampled
    @Test
    fun sampledViaParentLinks() {
        val span =
            SdkTracerProvider.builder().setSampler(Sampler.alwaysOff()).build()["test"].spanBuilder(
                    SPAN_NAME
                )
                .startSpan()
        try {
            span.spanContext.isSampled().shouldBeFalse()
        } finally {
            span.end()
        }
    }

    @Test
    fun noParent() {
        val parent = sdkTracer.spanBuilder(SPAN_NAME).startSpan()
        try {
            parent.makeCurrent().use {
                val span = sdkTracer.spanBuilder(SPAN_NAME).setNoParent().startSpan()
                try {
                    span.spanContext.traceId shouldNotBe parent.spanContext.traceId
                    mockedSpanProcessor.startContext shouldBe Context.root()
                    mockedSpanProcessor.startSpan shouldBe span
                    val spanNoParent =
                        sdkTracer
                            .spanBuilder(SPAN_NAME)
                            .setNoParent()
                            .setParent(Context.current())
                            .setNoParent()
                            .startSpan()
                    try {
                        span.spanContext.traceId shouldNotBe parent.spanContext.traceId
                        mockedSpanProcessor.startContext shouldBe Context.root()
                        mockedSpanProcessor.startSpan shouldBe spanNoParent
                    } finally {
                        spanNoParent.end()
                    }
                } finally {
                    span.end()
                }
            }
        } finally {
            parent.end()
        }
    }

    @Test
    fun noParent_override() {
        val parent = sdkTracer.spanBuilder(SPAN_NAME).startSpan()
        try {
            val parentContext = Context.current().with(parent)
            val span =
                sdkTracer
                    .spanBuilder(SPAN_NAME)
                    .setNoParent()
                    .setParent(parentContext)
                    .startSpan() as
                    RecordEventsReadableSpan
            try {
                mockedSpanProcessor.startContext shouldBe parentContext
                mockedSpanProcessor.startSpan shouldBe span
                span.spanContext.traceId shouldBe parent.spanContext.traceId
                span.toSpanData().parentSpanId shouldBe parent.spanContext.spanId
                val parentContext2 = Context.current().with(parent)
                val span2 =
                    sdkTracer
                        .spanBuilder(SPAN_NAME)
                        .setNoParent()
                        .setParent(parentContext2)
                        .startSpan() as
                        RecordEventsReadableSpan
                try {
                    mockedSpanProcessor.startContext shouldBe parentContext2
                    mockedSpanProcessor.startSpan shouldBe span2
                    span2.spanContext.traceId shouldBe parent.spanContext.traceId
                } finally {
                    span2.end()
                }
            } finally {
                span.end()
            }
        } finally {
            parent.end()
        }
    }

    @Test
    fun overrideNoParent_remoteParent() {
        val parent = sdkTracer.spanBuilder(SPAN_NAME).startSpan()
        try {
            val parentContext = Context.current().with(parent)
            val span =
                sdkTracer
                    .spanBuilder(SPAN_NAME)
                    .setNoParent()
                    .setParent(parentContext)
                    .startSpan() as
                    RecordEventsReadableSpan
            try {
                mockedSpanProcessor.startContext shouldBe parentContext
                mockedSpanProcessor.startSpan shouldBe span
                span.spanContext.traceId shouldBe parent.spanContext.traceId
                span.toSpanData().parentSpanId shouldBe parent.spanContext.spanId
            } finally {
                span.end()
            }
        } finally {
            parent.end()
        }
    }

    @Test
    fun parent_fromContext() {
        val parent = sdkTracer.spanBuilder(SPAN_NAME).startSpan()
        val context = Context.current().with(parent)
        try {
            val span =
                sdkTracer.spanBuilder(SPAN_NAME).setNoParent().setParent(context).startSpan() as
                    RecordEventsReadableSpan
            try {
                mockedSpanProcessor.startContext shouldBe context
                mockedSpanProcessor.startSpan shouldBe span
                span.spanContext.traceId shouldBe parent.spanContext.traceId
                span.toSpanData().parentSpanId shouldBe parent.spanContext.spanId
            } finally {
                span.end()
            }
        } finally {
            parent.end()
        }
    }

    @Test
    fun parent_fromEmptyContext() {
        val emptyContext = Context.current()
        val parent = sdkTracer.spanBuilder(SPAN_NAME).startSpan()
        try {
            var span: RecordEventsReadableSpan? = null
            parent.makeCurrent().use {
                span =
                    sdkTracer.spanBuilder(SPAN_NAME).setParent(emptyContext).startSpan() as
                        RecordEventsReadableSpan
            }
            try {
                mockedSpanProcessor.startContext shouldBe emptyContext
                mockedSpanProcessor.startSpan shouldBe span!!
                span!!.spanContext.traceId shouldNotBe parent.spanContext.traceId
                span!!.toSpanData().parentSpanId shouldNotBe parent.spanContext.spanId
            } finally {
                span!!.end()
            }
        } finally {
            parent.end()
        }
    }

    @Test
    fun parentCurrentSpan() {
        val parent = sdkTracer.spanBuilder(SPAN_NAME).startSpan()
        try {
            parent.makeCurrent().use { ignored ->
                val implicitParent = Context.current()
                val span = sdkTracer.spanBuilder(SPAN_NAME).startSpan() as RecordEventsReadableSpan
                try {
                    mockedSpanProcessor.startContext shouldBe implicitParent
                    mockedSpanProcessor.startSpan shouldBe span
                    span.spanContext.traceId shouldBe parent.spanContext.traceId
                    span.toSpanData().parentSpanId shouldBe parent.spanContext.spanId
                } finally {
                    span.end()
                }
            }
        } finally {
            parent.end()
        }
    }

    @Test
    fun parent_invalidContext() {
        val parent: Span = Span.invalid()
        val parentContext = Context.current().with(parent)
        val span =
            sdkTracer.spanBuilder(SPAN_NAME).setParent(parentContext).startSpan() as
                RecordEventsReadableSpan
        try {
            mockedSpanProcessor.startContext shouldBe parentContext
            mockedSpanProcessor.startSpan shouldBe span
            span.spanContext.traceId shouldNotBe parent.spanContext.traceId
            SpanId.isValid(span.toSpanData().parentSpanId).shouldBeFalse()
        } finally {
            span.end()
        }
    }

    @Test
    fun startTimestamp_numeric() {
        val span =
            sdkTracer
                .spanBuilder(SPAN_NAME)
                .setStartTimestamp(10, DateTimeUnit.NANOSECOND)
                .startSpan() as
                RecordEventsReadableSpan
        span.end()
        span.toSpanData().startEpochNanos shouldBe 10
    }

    @Test
    fun startTimestamp_instant() {
        val span =
            sdkTracer
                .spanBuilder(SPAN_NAME)
                .setStartTimestamp(Instant.fromEpochMilliseconds(100))
                .startSpan() as
                RecordEventsReadableSpan
        span.end()
        span.toSpanData().startEpochNanos shouldBe DateTimeUnit.MILLISECOND.normalizeToNanos(100)
    }

    @Test
    fun parent_clockIsSame() {
        val parent = sdkTracer.spanBuilder(SPAN_NAME).startSpan()
        try {
            parent.makeCurrent().use {
                val span = sdkTracer.spanBuilder(SPAN_NAME).startSpan() as RecordEventsReadableSpan
                span.clock shouldBe (parent as RecordEventsReadableSpan).clock
            }
        } finally {
            parent.end()
        }
    }

    @Test
    fun parentCurrentSpan_clockIsSame() {
        val parent = sdkTracer.spanBuilder(SPAN_NAME).startSpan()
        try {
            parent.makeCurrent().use {
                val span = sdkTracer.spanBuilder(SPAN_NAME).startSpan() as RecordEventsReadableSpan
                span.clock shouldBe (parent as RecordEventsReadableSpan).clock
            }
        } finally {
            parent.end()
        }
    }

    @Test
    fun isSampled() {
        SdkSpanBuilder.isSampled(SamplingDecision.DROP).shouldBeFalse()
        SdkSpanBuilder.isSampled(SamplingDecision.RECORD_ONLY).shouldBeFalse()
        SdkSpanBuilder.isSampled(SamplingDecision.RECORD_AND_SAMPLE).shouldBeTrue()
    }

    @Test
    fun isRecording() {
        SdkSpanBuilder.isRecording(SamplingDecision.DROP).shouldBeFalse()
        SdkSpanBuilder.isRecording(SamplingDecision.RECORD_ONLY).shouldBeTrue()
        SdkSpanBuilder.isRecording(SamplingDecision.RECORD_AND_SAMPLE).shouldBeTrue()
    }

    // SpanData is very commonly used in unit tests, we want the toString to make sure it's
    // relatively
    // easy to understand failure messages.
    // TODO(anuraaga): Currently it isn't - we even return the same (or maybe incorrect?) stuff
    // twice.
    // Improve the toString.
    // @Test
    // TODO fix stringify test
    fun spanDataToString() {
        val spanBuilder = sdkTracer.spanBuilder(SPAN_NAME)
        val span = spanBuilder.startSpan() as RecordEventsReadableSpan
        span.setAttribute("http.status_code", 500)
        span.setAttribute("http.url", "https://opentelemetry.io")
        span.setStatus(StatusCode.ERROR, "error")
        span.end()
        span.toSpanData().toString() shouldMatch
            "SpanData\\{spanContext=ImmutableSpanContext\\{" +
                "traceId=[0-9a-f]{32}, " +
                "spanId=[0-9a-f]{16}, " +
                "traceFlags=01, " +
                "traceState=ArrayBasedTraceState\\{entries=\\[]}, remote=false, valid=true}, " +
                "parentSpanContext=ImmutableSpanContext\\{" +
                "traceId=00000000000000000000000000000000, " +
                "spanId=0000000000000000, " +
                "traceFlags=00, " +
                "traceState=ArrayBasedTraceState\\{entries=\\[]}, remote=false, valid=false}, " +
                "resource=Resource\\{schemaUrl=null, " +
                "attributes=\\{service.name=\"unknown_service:java\", " +
                "telemetry.sdk.language=\"java\", telemetry.sdk.name=\"opentelemetry\", " +
                "telemetry.sdk.version=\"\\d+.\\d+.\\d+(-SNAPSHOT)?\"}}, " +
                "instrumentationLibraryInfo=InstrumentationLibraryInfo\\{" +
                "name=SpanBuilderSdkTest, version=null, schemaUrl=null}, " +
                "name=span_name, " +
                "kind=INTERNAL, " +
                "startEpochNanos=[0-9]+, " +
                "endEpochNanos=[0-9]+, " +
                "attributes=AttributesMap\\{data=\\{[^}]*}, capacity=128, totalAddedValues=2}, " +
                "totalAttributeCount=2, " +
                "events=\\[], " +
                "totalRecordedEvents=0, " +
                "links=\\[], " +
                "totalRecordedLinks=0, " +
                "status=ImmutableStatusData\\{statusCode=ERROR, description=error}, " +
                "hasEnded=true}"
    }

    @Test
    fun doNotCrash() {
        shouldNotThrowAny {
            val spanBuilder = sdkTracer.spanBuilder("")
            spanBuilder.setNoParent()
            spanBuilder.setAttribute(stringKey(""), "foo")
            spanBuilder.setStartTimestamp(-1, DateTimeUnit.NANOSECOND)
            spanBuilder.setParent(Context.root())
            spanBuilder.setNoParent()
            spanBuilder.addLink(Span.invalid().spanContext)
            spanBuilder.addLink(Span.invalid().spanContext, Attributes.empty())
            spanBuilder.setAttribute("key", "value")
            spanBuilder.setAttribute("key", 12345L)
            spanBuilder.setAttribute("key", .12345)
            spanBuilder.setAttribute("key", true)
            spanBuilder.setAttribute(stringKey("key"), "value")
            spanBuilder.setAllAttributes(Attributes.of(stringKey("key"), "value"))
            spanBuilder.setAllAttributes(Attributes.empty())
            spanBuilder.setStartTimestamp(12345L, DateTimeUnit.NANOSECOND)
            spanBuilder.setStartTimestamp(Instant.DISTANT_FUTURE)
            spanBuilder.startSpan().spanContext.isValid.shouldBeTrue()
        }
    }

    companion object {
        private const val SPAN_NAME = "span_name"
    }
}
