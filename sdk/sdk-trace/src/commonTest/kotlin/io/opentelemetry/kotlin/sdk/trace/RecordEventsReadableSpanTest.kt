/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
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
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.resources.Resource
import io.opentelemetry.kotlin.sdk.testing.time.TestClock
import io.opentelemetry.kotlin.sdk.trace.data.EventData
import io.opentelemetry.kotlin.sdk.trace.data.LinkData
import io.opentelemetry.kotlin.sdk.trace.data.SpanData
import io.opentelemetry.kotlin.sdk.trace.data.StatusData
import io.opentelemetry.kotlin.semconv.trace.attributes.SemanticAttributes
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class RecordEventsReadableSpanTest {
    private val idsGenerator = IdGenerator.random()
    private val traceId = idsGenerator.generateTraceId()
    private val spanId = idsGenerator.generateSpanId()
    private val parentSpanId = idsGenerator.generateSpanId()
    private val spanContext: SpanContext =
        SpanContext.create(traceId, spanId, TraceFlags.default, TraceState.default)
    private val resource = Resource.empty()
    private val instrumentationLibraryInfo: InstrumentationLibraryInfo =
        InstrumentationLibraryInfo.create("theName", null)
    private val attributes: MutableMap<AttributeKey<*>, Any> = HashMap<AttributeKey<*>, Any>()
    private var expectedAttributes: Attributes? = null
    private val link: LinkData = LinkData.create(spanContext)
    private val testClock: TestClock
    private val spanProcessor = MockFactory.createSpanProcessor()
    init {
        attributes[stringKey("MyStringAttributeKey")] = "MyStringAttributeValue"
        attributes[longKey("MyLongAttributeKey")] = 123L
        attributes[booleanKey("MyBooleanAttributeKey")] = false
        val builder =
            Attributes.builder().put("MySingleStringAttributeKey", "MySingleStringAttributeValue")
        for ((key, value) in attributes) {
            builder.put(key as AttributeKey<Any>, value)
        }
        expectedAttributes = builder.build()
        testClock = TestClock.create(Instant.fromEpochSeconds(0, START_EPOCH_NANOS))
    }

    @Test
    fun nothingChangedAfterEnd() {
        val span = createTestSpan(SpanKind.INTERNAL)
        span.end()
        // Check that adding trace events or update fields after Span#end() does not throw any
        // thrown
        // and are ignored.
        spanDoWork(span, StatusCode.ERROR, "CANCELLED")
        val spanData: SpanData = span.toSpanData()
        verifySpanData(
            spanData,
            Attributes.empty(),
            emptyList(),
            listOf(link),
            SPAN_NAME,
            START_EPOCH_NANOS,
            START_EPOCH_NANOS,
            StatusData.unset(),
            true
        )
    }

    @Test
    fun endSpanTwice_DoNotCrash() {
        val span = createTestSpan(SpanKind.INTERNAL)
        span.hasEnded().shouldBeFalse()
        span.end()
        span.hasEnded().shouldBeTrue()
        span.end()
        span.hasEnded().shouldBeTrue()
    }

    @Test
    fun toSpanData_ActiveSpan() {
        val span = createTestSpan(SpanKind.INTERNAL)
        try {
            span.hasEnded().shouldBeFalse()
            spanDoWork(span, null, null)
            val spanData: SpanData = span.toSpanData()
            val event: EventData =
                EventData.create(
                    START_EPOCH_NANOS + NANOS_PER_SECOND,
                    "event2",
                    Attributes.empty(),
                    0
                )
            verifySpanData(
                spanData,
                expectedAttributes,
                listOf<EventData>(event),
                listOf<LinkData>(link),
                SPAN_NEW_NAME,
                START_EPOCH_NANOS,
                0,
                StatusData.unset(),
                /*hasEnded=*/ false
            )
            span.hasEnded().shouldBeFalse()
            span.isRecording().shouldBeTrue()
        } finally {
            span.end()
        }
        span.hasEnded().shouldBeTrue()
        span.isRecording().shouldBeFalse()
    }

    @Test
    fun toSpanData_EndedSpan() {
        val span = createTestSpan(SpanKind.INTERNAL)
        try {
            spanDoWork(span, StatusCode.ERROR, "CANCELLED")
        } finally {
            span.end()
        }
        spanProcessor.endSpan shouldBe span
        val spanData: SpanData = span.toSpanData()
        val event: EventData =
            EventData.create(START_EPOCH_NANOS + NANOS_PER_SECOND, "event2", Attributes.empty(), 0)
        verifySpanData(
            spanData,
            expectedAttributes,
            listOf(event),
            listOf(link),
            SPAN_NEW_NAME,
            START_EPOCH_NANOS,
            testClock.now(),
            StatusData.create(StatusCode.ERROR, "CANCELLED"),
            /*hasEnded=*/ true
        )
    }

    @Test
    fun toSpanData_RootSpan() {
        val span = createTestRootSpan()
        try {
            spanDoWork(span, null, null)
        } finally {
            span.end()
        }
        span.parentSpanContext.isValid.shouldBeFalse()
        val spanData: SpanData = span.toSpanData()
        SpanId.isValid(spanData.parentSpanId).shouldBeFalse()
    }

    @Test
    fun toSpanData_ChildSpan() {
        val span = createTestSpan(SpanKind.INTERNAL)
        try {
            spanDoWork(span, null, null)
        } finally {
            span.end()
        }
        span.parentSpanContext.isValid.shouldBeTrue()
        span.parentSpanContext.traceId shouldBe traceId
        span.parentSpanContext.spanId shouldBe parentSpanId
        val spanData: SpanData = span.toSpanData()
        spanData.parentSpanId shouldBe parentSpanId
    }

    @Test
    fun toSpanData_WithInitialAttributes() {
        val span = createTestSpanWithAttributes(attributes)
        span.setAttribute("anotherKey", "anotherValue")
        span.end()
        val spanData: SpanData = span.toSpanData()
        spanData.attributes.size shouldBe attributes.size + 1
        spanData.totalAttributeCount shouldBe attributes.size + 1
    }

    @Test
    fun toSpanData_SpanDataDoesNotChangeWhenModifyingSpan() {
        // Create a span
        val span = createTestSpanWithAttributes(attributes)

        // Convert it to a SpanData object -- this should be an immutable snapshot.
        var spanData: SpanData = span.toSpanData()

        // Now modify the span after creating the snapshot.
        span.setAttribute("anotherKey", "anotherValue")
        span.updateName("changedName")
        span.addEvent("newEvent")
        span.end()

        // Assert that the snapshot does not reflect the modified state, but the state of the time
        // when
        // toSpanData was called.
        spanData.attributes.size shouldBe attributes.size
        spanData.attributes[stringKey("anotherKey")].shouldBeNull()
        spanData.hasEnded().shouldBeFalse()
        spanData.endEpochNanos shouldBe 0
        spanData.name shouldBe SPAN_NAME
        spanData.events.shouldBeEmpty()

        // Sanity check: Calling toSpanData again after modifying the span should get us the
        // modified
        // state.
        spanData = span.toSpanData()
        spanData.attributes.size shouldBe attributes.size + 1
        spanData.attributes[stringKey("anotherKey")] shouldBe "anotherValue"
        spanData.hasEnded().shouldBeTrue()
        spanData.endEpochNanos.shouldBeGreaterThan(0)
        spanData.name shouldBe "changedName"
        spanData.events.shouldHaveSize(1)
    }

    @Test
    fun toSpanData_Status() {
        val span = createTestSpan(SpanKind.CONSUMER)
        try {
            testClock.advance(1.seconds)
            span.toSpanData().status shouldBe StatusData.unset()
            span.setStatus(StatusCode.ERROR, "CANCELLED")
            span.toSpanData().status shouldBe StatusData.create(StatusCode.ERROR, "CANCELLED")
        } finally {
            span.end()
        }
        span.toSpanData().status shouldBe StatusData.create(StatusCode.ERROR, "CANCELLED")
    }

    @Test
    fun toSpanData_Kind() {
        val span = createTestSpan(SpanKind.SERVER)
        try {
            span.toSpanData().kind shouldBe SpanKind.SERVER
        } finally {
            span.end()
        }
    }

    @Test
    fun kind() {
        val span = createTestSpan(SpanKind.SERVER)
        try {
            span.kind shouldBe SpanKind.SERVER
        } finally {
            span.end()
        }
    }

    @Test
    fun attribute() {
        val span = createTestSpanWithAttributes(attributes)
        try {
            span.getAttribute(longKey("MyLongAttributeKey")) shouldBe 123L
        } finally {
            span.end()
        }
    }

    @Test
    fun getInstrumentationLibraryInfo() {
        val span = createTestSpan(SpanKind.CLIENT)
        try {
            span.instrumentationLibraryInfo shouldBe instrumentationLibraryInfo
        } finally {
            span.end()
        }
    }

    @Test
    fun andUpdateSpanName() {
        val span = createTestRootSpan()
        try {
            span.name shouldBe SPAN_NAME
            span.updateName(SPAN_NEW_NAME)
            span.name shouldBe SPAN_NEW_NAME
        } finally {
            span.end()
        }
    }

    @Test
    fun latencyNs_ActiveSpan() {
        val span = createTestSpan(SpanKind.INTERNAL)
        try {
            testClock.advance(1.seconds)
            val elapsedTimeNanos1 = testClock.now() - START_EPOCH_NANOS
            span.latencyNanos shouldBe elapsedTimeNanos1
            testClock.advance(1.seconds)
            val elapsedTimeNanos2 = testClock.now() - START_EPOCH_NANOS
            span.latencyNanos shouldBe elapsedTimeNanos2
        } finally {
            span.end()
        }
    }

    @Test
    fun latencyNs_EndedSpan() {
        val span = createTestSpan(SpanKind.INTERNAL)
        testClock.advance(1.seconds)
        span.end()
        val elapsedTimeNanos = testClock.now() - START_EPOCH_NANOS
        span.latencyNanos shouldBe elapsedTimeNanos
        testClock.advance(1.seconds)
        span.latencyNanos shouldBe elapsedTimeNanos
    }

    @Test
    fun setAttribute() {
        val span = createTestRootSpan()
        try {
            span.setAttribute("StringKey", "StringVal")
            span.setAttribute("EmptyStringKey", "")
            span.setAttribute(stringKey("EmptyStringAttributeValue"), "")
            span.setAttribute("LongKey", 1000L)
            span.setAttribute(longKey("LongKey2"), 5)
            span.setAttribute(longKey("LongKey3"), 6L)
            span.setAttribute("DoubleKey", 10.0)
            span.setAttribute("BooleanKey", false)
            span.setAttribute(
                stringArrayKey("ArrayStringKey"),
                listOf("StringVal", "", "StringVal2")
            )
            span.setAttribute(longArrayKey("ArrayLongKey"), listOf(1L, 2L, 3L, 4L, 5L))
            span.setAttribute(doubleArrayKey("ArrayDoubleKey"), listOf(0.1, 2.3, 4.5, 6.7, 8.9))
            span.setAttribute(booleanArrayKey("ArrayBooleanKey"), listOf(true, false, false, true))
        } finally {
            span.end()
        }
        val spanData: SpanData = span.toSpanData()
        spanData.attributes.size shouldBe 12
        spanData.attributes[stringKey("StringKey")].shouldNotBeNull()
        spanData.attributes[stringKey("EmptyStringKey")].shouldNotBeNull()
        spanData.attributes[stringKey("EmptyStringAttributeValue")].shouldNotBeNull()
        spanData.attributes[longKey("LongKey")].shouldNotBeNull()
        spanData.attributes[longKey("LongKey2")] shouldBe 5L
        spanData.attributes[longKey("LongKey3")] shouldBe 6L
        spanData.attributes[doubleKey("DoubleKey")].shouldNotBeNull()
        spanData.attributes[booleanKey("BooleanKey")].shouldNotBeNull()
        spanData.attributes[stringArrayKey("ArrayStringKey")].shouldNotBeNull()
        spanData.attributes[longArrayKey("ArrayLongKey")].shouldNotBeNull()
        spanData.attributes[doubleArrayKey("ArrayDoubleKey")].shouldNotBeNull()
        spanData.attributes[booleanArrayKey("ArrayBooleanKey")].shouldNotBeNull()
        spanData.attributes[stringArrayKey("ArrayStringKey")]!!.size shouldBe 3
        spanData.attributes[longArrayKey("ArrayLongKey")]!!.size shouldBe 5
        spanData.attributes[doubleArrayKey("ArrayDoubleKey")]!!.size shouldBe 5
        spanData.attributes[booleanArrayKey("ArrayBooleanKey")]!!.size shouldBe 4
    }

    @Test
    fun setAttribute_emptyKeys() {
        val span = createTestRootSpan()
        span.setAttribute("", "")
        span.setAttribute("", 1000L)
        span.setAttribute("", 10.0)
        span.setAttribute("", false)
        span.setAttribute(stringArrayKey(""), emptyList())
        span.setAttribute(booleanArrayKey(""), emptyList())
        span.setAttribute(longArrayKey(""), emptyList())
        span.setAttribute(doubleArrayKey(""), emptyList())
        span.toSpanData().attributes.size shouldBe 0
    }

    @Test
    fun setAttribute_emptyArrayAttributeValue() {
        val span = createTestRootSpan()
        span.setAttribute(stringArrayKey("stringArrayAttribute"), emptyList())
        span.toSpanData().attributes.size shouldBe 1
        span.setAttribute(booleanArrayKey("boolArrayAttribute"), emptyList())
        span.toSpanData().attributes.size shouldBe 2
        span.setAttribute(longArrayKey("longArrayAttribute"), emptyList())
        span.toSpanData().attributes.size shouldBe 3
        span.setAttribute(doubleArrayKey("doubleArrayAttribute"), emptyList())
        span.toSpanData().attributes.size shouldBe 4
    }

    @Test
    fun setAttribute_nullStringValue() {
        val span = createTestRootSpan()
        span.setAttribute("emptyString", "")
        span.setAttribute(stringKey("emptyStringAttributeValue"), "")
        span.toSpanData().attributes.size shouldBe 2
    }

    @Test
    fun setAttribute_nullAttributeValue() {
        val span = createTestRootSpan()
        span.setAttribute("emptyString", "")
        span.setAttribute(stringKey("emptyStringAttributeValue"), "")
        span.setAttribute("longAttribute", 0L)
        span.setAttribute("boolAttribute", false)
        span.setAttribute("doubleAttribute", 0.12345)
        span.setAttribute(stringArrayKey("stringArrayAttribute"), listOf(""))
        span.setAttribute(booleanArrayKey("boolArrayAttribute"), listOf(true))
        span.setAttribute(longArrayKey("longArrayAttribute"), listOf(12345L))
        span.setAttribute(doubleArrayKey("doubleArrayAttribute"), listOf(1.2345))
        span.toSpanData().attributes.size shouldBe 9
    }

    @Test
    fun setAllAttributes() {
        val span = createTestRootSpan()
        val attributes: Attributes =
            Attributes.builder()
                .put("StringKey", "StringVal")
                .put("EmptyStringKey", "")
                .put(stringKey("EmptyStringAttributeValue"), "")
                .put("LongKey", 1000L)
                .put(longKey("LongKey2"), 5)
                .put(longKey("LongKey3"), 6L)
                .put("DoubleKey", 10.0)
                .put("BooleanKey", false)
                .put(stringArrayKey("ArrayStringKey"), listOf("StringVal", "", "StringVal2"))
                .put(longArrayKey("ArrayLongKey"), listOf(1L, 2L, 3L, 4L, 5L))
                .put(doubleArrayKey("ArrayDoubleKey"), listOf(0.1, 2.3, 4.5, 6.7, 8.9))
                .put(booleanArrayKey("ArrayBooleanKey"), listOf(true, false, false, true))
                .build()
        try {
            span.setAllAttributes(attributes)
        } finally {
            span.end()
        }
        val spanData: SpanData = span.toSpanData()
        spanData.attributes.size shouldBe 12
        spanData.attributes[stringKey("StringKey")].shouldNotBeNull()
        spanData.attributes[stringKey("EmptyStringKey")].shouldNotBeNull()
        spanData.attributes[stringKey("EmptyStringAttributeValue")].shouldNotBeNull()
        spanData.attributes[longKey("LongKey")].shouldNotBeNull()
        spanData.attributes[longKey("LongKey2")] shouldBe 5L
        spanData.attributes[longKey("LongKey3")] shouldBe 6L
        spanData.attributes[doubleKey("DoubleKey")].shouldNotBeNull()
        spanData.attributes[booleanKey("BooleanKey")].shouldNotBeNull()
        spanData.attributes[stringArrayKey("ArrayStringKey")].shouldNotBeNull()
        spanData.attributes[longArrayKey("ArrayLongKey")].shouldNotBeNull()
        spanData.attributes[doubleArrayKey("ArrayDoubleKey")].shouldNotBeNull()
        spanData.attributes[booleanArrayKey("ArrayBooleanKey")].shouldNotBeNull()
        spanData.attributes[stringArrayKey("ArrayStringKey")]!!.size shouldBe 3
        spanData.attributes[longArrayKey("ArrayLongKey")]!!.size shouldBe 5
        spanData.attributes[doubleArrayKey("ArrayDoubleKey")]!!.size shouldBe 5
        spanData.attributes[booleanArrayKey("ArrayBooleanKey")]!!.size shouldBe 4
    }

    @Test
    fun setAllAttributes_mergesAttributes() {
        val span = createTestRootSpan()
        val attributes =
            Attributes.builder()
                .put("StringKey", "StringVal")
                .put("LongKey", 1000L)
                .put("DoubleKey", 10.0)
                .put("BooleanKey", false)
                .build()
        try {
            span.setAttribute("StringKey", "OtherStringVal")
                .setAttribute("ExistingStringKey", "ExistingStringVal")
                .setAttribute("LongKey", 2000L)
                .setAllAttributes(attributes)
        } finally {
            span.end()
        }
        val spanData: SpanData = span.toSpanData()
        spanData.attributes.size shouldBe 5
        spanData.attributes[stringKey("StringKey")] shouldBe "StringVal"
        spanData.attributes[stringKey("ExistingStringKey")] shouldBe "ExistingStringVal"
        spanData.attributes[longKey("LongKey")] shouldBe 1000L
        spanData.attributes[doubleKey("DoubleKey")] shouldBe 10.0
        spanData.attributes[booleanKey("BooleanKey")] shouldBe false
    }

    @Test
    fun setAllAttributes_emptyAttributes() {
        val span = createTestRootSpan()
        span.setAllAttributes(Attributes.empty())
        span.toSpanData().attributes.size shouldBe 0
    }

    @Test
    fun addEvent() {
        val span = createTestRootSpan()
        try {
            span.addEvent("event1")
            span.addEvent("event2", Attributes.of(stringKey("e1key"), "e1Value"))
            span.addEvent("event3", 10, DateTimeUnit.SECOND)
            span.addEvent("event4", Instant.fromEpochSeconds(20, 0))
            span.addEvent(
                "event5",
                Attributes.builder().put("foo", "bar").build(),
                30,
                DateTimeUnit.MILLISECOND
            )
            span.addEvent(
                "event6",
                Attributes.builder().put("foo", "bar").build(),
                Instant.fromEpochMilliseconds(1000)
            )
        } finally {
            span.end()
        }
        val events: List<EventData> = span.toSpanData().events
        events shouldHaveSize 6
        events[0].also { event ->
            event.name shouldBe "event1"
            event.attributes shouldBe Attributes.empty()
            event.epochNanos shouldBe START_EPOCH_NANOS
        }
        events[1].also { event ->
            event.name shouldBe "event2"
            event.attributes shouldBe Attributes.of(stringKey("e1key"), "e1Value")
            event.epochNanos shouldBe START_EPOCH_NANOS
        }
        events[2].also { event ->
            event.name shouldBe "event3"
            event.attributes shouldBe Attributes.empty()
            event.epochNanos shouldBe DateTimeUnit.SECOND.normalizeToNanos(10)
        }
        events[3].also { event ->
            event.name shouldBe "event4"
            event.attributes shouldBe Attributes.empty()
            event.epochNanos shouldBe DateTimeUnit.SECOND.normalizeToNanos(20)
        }
        events[4].also { event ->
            event.name shouldBe "event5"
            event.attributes shouldBe Attributes.builder().put("foo", "bar").build()
            event.epochNanos shouldBe DateTimeUnit.MILLISECOND.normalizeToNanos(30)
        }
        events[5].also { event ->
            event.name shouldBe "event6"
            event.attributes shouldBe Attributes.builder().put("foo", "bar").build()
            event.epochNanos shouldBe DateTimeUnit.MILLISECOND.normalizeToNanos(1000)
        }
    }

    @Test
    fun attributeLength() {
        val maxLength = 25
        val span =
            createTestSpan(SpanLimits.builder().setMaxAttributeValueLength(maxLength).build())
        try {
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
            span.setAllAttributes(attributes)
            attributes = span.toSpanData().attributes
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
    fun eventAttributeLength() {
        val maxLength = 25
        val span =
            createTestSpan(SpanLimits.builder().setMaxAttributeValueLength(maxLength).build())
        try {
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
            span.setAllAttributes(attributes)
            attributes = span.toSpanData().attributes
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
    fun droppingAttributes() {
        val maxNumberOfAttributes = 8
        val spanLimits =
            SpanLimits.builder().setMaxNumberOfAttributes(maxNumberOfAttributes).build()
        val span = createTestSpan(spanLimits)
        try {
            for (i in 0 until 2 * maxNumberOfAttributes) {
                span.setAttribute(longKey("MyStringAttributeKey$i"), i.toLong())
            }
            val spanData: SpanData = span.toSpanData()
            spanData.attributes.size shouldBe maxNumberOfAttributes
            spanData.totalAttributeCount shouldBe 2 * maxNumberOfAttributes
        } finally {
            span.end()
        }
        val spanData: SpanData = span.toSpanData()
        spanData.attributes.size shouldBe maxNumberOfAttributes
        spanData.totalAttributeCount shouldBe 2 * maxNumberOfAttributes
    }

    @Test
    fun endWithTimestamp_numeric() {
        val span1 = createTestRootSpan()
        span1.end(10, DateTimeUnit.NANOSECOND)
        span1.toSpanData().endEpochNanos shouldBe 10
    }

    @Test
    fun endWithTimestamp_instant() {
        val span1 = createTestRootSpan()
        span1.end(Instant.fromEpochMilliseconds(10))
        span1.toSpanData().endEpochNanos shouldBe DateTimeUnit.MILLISECOND.normalizeToNanos(10)
    }

    @Test
    fun droppingAndAddingAttributes() {
        val maxNumberOfAttributes = 8
        val spanLimits =
            SpanLimits.builder().setMaxNumberOfAttributes(maxNumberOfAttributes).build()
        val span = createTestSpan(spanLimits)
        try {
            for (i in 0 until 2 * maxNumberOfAttributes) {
                span.setAttribute(longKey("MyStringAttributeKey$i"), i.toLong())
            }
            var spanData: SpanData = span.toSpanData()
            spanData.attributes.size shouldBe maxNumberOfAttributes
            spanData.totalAttributeCount shouldBe 2 * maxNumberOfAttributes
            for (i in 0 until maxNumberOfAttributes / 2) {
                val value = i + maxNumberOfAttributes * 3 / 2
                span.setAttribute(longKey("MyStringAttributeKey$i"), value.toLong())
            }
            spanData = span.toSpanData()
            spanData.attributes.size shouldBe maxNumberOfAttributes
            // Test that we still have in the attributes map the latest maxNumberOfAttributes / 2
            // entries.
            for (i in 0 until maxNumberOfAttributes / 2) {
                val value = i + maxNumberOfAttributes * 3 / 2
                spanData.attributes[longKey("MyStringAttributeKey$i")] shouldBe value
            }
            // Test that we have the newest re-added initial entries.
            for (i in maxNumberOfAttributes / 2 until maxNumberOfAttributes) {
                spanData.attributes[longKey("MyStringAttributeKey$i")] shouldBe i
            }
        } finally {
            span.end()
        }
    }

    @Test
    fun droppingEvents() {
        val maxNumberOfEvents = 8
        val spanLimits = SpanLimits.builder().setMaxNumberOfEvents(maxNumberOfEvents).build()
        val span = createTestSpan(spanLimits)
        try {
            for (i in 0 until 2 * maxNumberOfEvents) {
                span.addEvent("event2", Attributes.empty())
                testClock.advance(1.seconds)
            }
            val spanData: SpanData = span.toSpanData()
            spanData.events.size shouldBe maxNumberOfEvents
            for (i in 0 until maxNumberOfEvents) {
                val expectedEvent: EventData =
                    EventData.create(
                        START_EPOCH_NANOS + i * NANOS_PER_SECOND,
                        "event2",
                        Attributes.empty(),
                        0
                    )
                spanData.events[i] shouldBe expectedEvent
                spanData.totalRecordedEvents shouldBe 2 * maxNumberOfEvents
            }
            spanData.totalRecordedEvents shouldBe 2 * maxNumberOfEvents
        } finally {
            span.end()
        }
        val spanData: SpanData = span.toSpanData()
        spanData.events.size shouldBe maxNumberOfEvents
        for (i in 0 until maxNumberOfEvents) {
            val expectedEvent: EventData =
                EventData.create(
                    START_EPOCH_NANOS + i * NANOS_PER_SECOND,
                    "event2",
                    Attributes.empty(),
                    0
                )
            spanData.events[i] shouldBe expectedEvent
        }
    }

    @Test
    fun recordException() {
        val exception = IllegalStateException("there was an exception")
        val span = createTestRootSpan()
        val stacktrace: String = exception.stackTraceToString()
        testClock.advance(1000.nanoseconds)
        val timestamp = testClock.now()
        span.recordException(exception)
        val events: List<EventData> = span.toSpanData().events
        events.shouldHaveSize(1)
        val event: EventData = events[0]
        event.name shouldBe "exception"
        event.epochNanos shouldBe timestamp
        event.attributes shouldBe
            Attributes.builder()
                .put(SemanticAttributes.EXCEPTION_TYPE, "IllegalStateException")
                .put(SemanticAttributes.EXCEPTION_MESSAGE, "there was an exception")
                .put(SemanticAttributes.EXCEPTION_STACKTRACE, stacktrace)
                .build()
    }

    @Test
    fun recordException_noMessage() {
        val exception = IllegalStateException()
        val span = createTestRootSpan()
        span.recordException(exception)
        val events: List<EventData> = span.toSpanData().events
        events.shouldHaveSize(1)
        val event: EventData = events[0]
        event.attributes[SemanticAttributes.EXCEPTION_MESSAGE].shouldBeNull()
    }

    private class InnerClassException : Exception()

    @Test
    fun recordException_innerClassException() {
        val exception = InnerClassException()
        val span = createTestRootSpan()
        span.recordException(exception)
        val events: List<EventData> = span.toSpanData().events
        events.shouldHaveSize(1)
        val event: EventData = events[0]
        event.attributes[SemanticAttributes.EXCEPTION_TYPE] shouldBe
            InnerClassException::class.simpleName!!
    }

    @Test
    fun recordException_additionalAttributes() {
        val exception = IllegalStateException("there was an exception")
        val stacktrace: String = exception.stackTraceToString()
        testClock.advance(1000.nanoseconds)
        val timestamp = testClock.now()
        val span = createTestSpan(SpanKind.INTERNAL)
        span.recordException(
            exception,
            Attributes.of(
                stringKey("key1"),
                "this is an additional attribute",
                stringKey("exception.message"),
                "this is a precedence attribute"
            )
        )
        val events: List<EventData> = span.toSpanData().events
        events.shouldHaveSize(1)
        val event: EventData = events[0]
        event.name shouldBe "exception"
        event.epochNanos shouldBe timestamp
        event.attributes shouldBe
            Attributes.builder()
                .put("key1", "this is an additional attribute")
                .put("exception.type", "IllegalStateException")
                .put("exception.message", "this is a precedence attribute")
                .put("exception.stacktrace", stacktrace)
                .build()
    }

    @Test
    fun badArgsIgnored() {
        val span = createTestRootSpan()
        // Should be no exceptions
        span.end(0, DateTimeUnit.NANOSECOND)

        // Ignored the bad calls
        val data: SpanData = span.toSpanData()
        data.attributes.isEmpty().shouldBeTrue()
        data.status shouldBe StatusData.unset()
        data.name shouldBe SPAN_NAME
    }

    private fun createTestSpanWithAttributes(
        attributes: Map<AttributeKey<*>, Any>
    ): RecordEventsReadableSpan {
        val spanLimits: SpanLimits = SpanLimits.default
        val attributesMap =
            AttributesMap(spanLimits.maxNumberOfAttributes, spanLimits.maxAttributeValueLength)
        attributes.forEach { pair -> attributesMap.put(pair.key, pair.value) }
        return createTestSpan(
            SpanKind.INTERNAL,
            SpanLimits.default,
            null,
            attributesMap,
            listOf(link)
        )
    }

    private fun createTestRootSpan(): RecordEventsReadableSpan {
        return createTestSpan(
            SpanKind.INTERNAL,
            SpanLimits.default,
            SpanId.invalid,
            null,
            listOf(link)
        )
    }

    private fun createTestSpan(config: SpanLimits): RecordEventsReadableSpan {
        return createTestSpan(SpanKind.INTERNAL, config, parentSpanId, null, listOf(link))
    }

    private fun createTestSpan(
        kind: SpanKind,
        config: SpanLimits = SpanLimits.default,
        parentSpanId: String? = this.parentSpanId,
        attributes: AttributesMap? = null,
        links: List<LinkData> = listOf(link)
    ): RecordEventsReadableSpan {
        val span =
            RecordEventsReadableSpan.startSpan(
                spanContext,
                SPAN_NAME,
                instrumentationLibraryInfo,
                kind,
                if (parentSpanId != null)
                    Span.wrap(
                        SpanContext.create(
                            traceId,
                            parentSpanId,
                            TraceFlags.default,
                            TraceState.default
                        )
                    )
                else Span.invalid(),
                Context.root(),
                config,
                spanProcessor,
                testClock,
                resource,
                attributes,
                links,
                1,
                0
            )
        spanProcessor.startSpan shouldBe span
        spanProcessor.startContext shouldBe Context.root()
        return span
    }

    private fun spanDoWork(
        span: RecordEventsReadableSpan,
        canonicalCode: StatusCode?,
        descriptio: String?
    ) {
        span.setAttribute("MySingleStringAttributeKey", "MySingleStringAttributeValue")
        attributes.forEach { pair -> span.setAttribute(pair.key as AttributeKey<Any>, pair.value) }
        testClock.advance(1.seconds)
        span.addEvent("event2", Attributes.empty())
        testClock.advance(1.seconds)
        span.updateName(SPAN_NEW_NAME)
        if (canonicalCode != null) {
            span.setStatus(canonicalCode, descriptio!!)
        }
    }

    private fun verifySpanData(
        spanData: SpanData,
        attributes: Attributes?,
        eventData: List<EventData>,
        links: List<LinkData>,
        spanName: String,
        startEpochNanos: Long,
        endEpochNanos: Long,
        status: StatusData,
        hasEnded: Boolean
    ) {
        spanData.traceId shouldBe traceId
        spanData.spanId shouldBe spanId
        spanData.parentSpanId shouldBe parentSpanId
        spanData.spanContext.traceState shouldBe TraceState.default
        spanData.resource shouldBe resource
        spanData.instrumentationLibraryInfo shouldBe instrumentationLibraryInfo
        spanData.name shouldBe spanName
        spanData.events shouldBe eventData
        spanData.links shouldBe links
        spanData.startEpochNanos shouldBe startEpochNanos
        spanData.endEpochNanos shouldBe endEpochNanos
        spanData.status.statusCode shouldBe status.statusCode
        spanData.hasEnded() shouldBe hasEnded

        // verify equality manually, since the implementations don't all equals with each other.
        val spanDataAttributes: Attributes = spanData.attributes
        spanDataAttributes.size shouldBe attributes!!.size
        spanDataAttributes.forEach { key, value ->
            attributes[key as AttributeKey<Any>] shouldBe value
        }
    }

    @Test
    fun testAsSpanData() {
        val name = "GreatSpan"
        val kind: SpanKind = SpanKind.SERVER
        val traceId = traceId
        val spanId = spanId
        val parentSpanId = parentSpanId
        val spanLimits: SpanLimits = SpanLimits.default
        val spanProcessor: SpanProcessor = NoopSpanProcessor.instance
        val resource = resource
        val attributes: Attributes = TestUtils.generateRandomAttributes()
        val attributesWithCapacity = AttributesMap(32, Int.MAX_VALUE)
        attributes.forEach { key, value -> attributesWithCapacity.put(key, value) }
        val event1Attributes: Attributes = TestUtils.generateRandomAttributes()
        val event2Attributes: Attributes = TestUtils.generateRandomAttributes()
        val context: SpanContext =
            SpanContext.create(traceId, spanId, TraceFlags.default, TraceState.default)
        val link1: LinkData = LinkData.create(context, TestUtils.generateRandomAttributes())
        val clock = testClock
        val readableSpan =
            RecordEventsReadableSpan.startSpan(
                context,
                name,
                instrumentationLibraryInfo,
                kind,
                if (parentSpanId != null)
                    Span.wrap(
                        SpanContext.create(
                            traceId,
                            parentSpanId,
                            TraceFlags.default,
                            TraceState.default
                        )
                    )
                else Span.invalid(),
                Context.root(),
                spanLimits,
                spanProcessor,
                clock,
                resource,
                attributesWithCapacity,
                listOf(link1),
                1,
                0
            )
        val startEpochNanos = clock.now()
        clock.advance(4.milliseconds)
        val firstEventEpochNanos = clock.now()
        readableSpan.addEvent("event1", event1Attributes)
        clock.advance(6.milliseconds)
        val secondEventTimeNanos = clock.now()
        readableSpan.addEvent("event2", event2Attributes)
        clock.advance(100.milliseconds)
        readableSpan.end()
        val endEpochNanos = clock.now()
        val events: List<EventData> =
            listOf(
                EventData.create(
                    firstEventEpochNanos,
                    "event1",
                    event1Attributes,
                    event1Attributes.size
                ),
                EventData.create(
                    secondEventTimeNanos,
                    "event2",
                    event2Attributes,
                    event2Attributes.size
                )
            )
        val result: SpanData = readableSpan.toSpanData()
        verifySpanData(
            result,
            attributesWithCapacity,
            events,
            listOf(link1),
            name,
            startEpochNanos,
            endEpochNanos,
            StatusData.unset(),
            true
        )
        result.totalRecordedLinks shouldBe 1
        result.spanContext.isSampled() shouldBe false
    }

    /*   @Test
    @Throws(ExecutionException::class, InterruptedException::class)
    fun testConcurrentModification() {
        val span = createTestSpan(SpanKind.INTERNAL)
        val es: ExecutorService = Executors.newSingleThreadExecutor()
        val modifierFuture: Future<*> = es.submit(
            Runnable {
                for (i in 0 until 5096 * 5) {
                    span.setAttribute("hey$i", "")
                }
            })
        try {
            for (i in 0 until 5096 * 5) {
                span.toSpanData()
            }
        } catch (t: Throwable) {
            modifierFuture.cancel(true)
            throw t
        }
        modifierFuture.get()
    }*/

    companion object {
        private const val SPAN_NAME = "MySpanName"
        private const val SPAN_NEW_NAME = "NewName"
        private val NANOS_PER_SECOND: Long = (1.seconds).inWholeNanoseconds
        private const val START_EPOCH_NANOS = 1000123789654L
    }
}
