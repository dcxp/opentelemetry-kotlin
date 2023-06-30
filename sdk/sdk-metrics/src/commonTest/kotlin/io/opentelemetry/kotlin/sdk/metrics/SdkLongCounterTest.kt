/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.stringKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.metrics.BoundLongCounter
import io.opentelemetry.kotlin.api.metrics.LongCounter
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.LongPointData
import io.opentelemetry.kotlin.sdk.metrics.data.LongSumData
import io.opentelemetry.kotlin.sdk.metrics.data.MetricDataType
import io.opentelemetry.kotlin.sdk.metrics.testing.InMemoryMetricReader
import io.opentelemetry.kotlin.sdk.resources.Resource
import io.opentelemetry.kotlin.sdk.testing.time.TestClock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

/** Unit tests for [SdkLongCounter]. */
internal class SdkLongCounterTest {
    private val testClock: TestClock = TestClock.create()
    private val sdkMeterReader = InMemoryMetricReader.create()
    private val sdkMeterProvider =
        SdkMeterProvider.builder()
            .setClock(testClock)
            .setResource(RESOURCE)
            .registerMetricReader(sdkMeterReader)
            .build()
    private val sdkMeter = sdkMeterProvider[SdkLongCounterTest::class.simpleName!!]

    @Test
    fun collectMetrics_NoRecords() {
        val longCounter = sdkMeter.counterBuilder("Counter").build()
        val bound = longCounter.bind(Attributes.builder().put("foo", "bar").build())
        try {
            sdkMeterReader.collectAllMetrics().shouldBeEmpty()
        } finally {
            bound.unbind()
        }
    }

    @Test
    fun collectMetrics_WithEmptyAttributes() {
        val longCounter =
            sdkMeter
                .counterBuilder("testCounter")
                .setDescription("description")
                .setUnit("By")
                .build()
        testClock.advance(1.seconds)
        longCounter.add(12, Attributes.empty())
        longCounter.add(12)
        assertSoftly(sdkMeterReader.collectAllMetrics()) {
            shouldHaveSize(1)
            assertSoftly(single()) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                name shouldBe "testCounter"
                description shouldBe "description"
                unit shouldBe "By"
                type shouldBe MetricDataType.LONG_SUM
                data shouldBe
                    LongSumData.create(
                        true,
                        AggregationTemporality.CUMULATIVE,
                        listOf(
                            LongPointData.create(
                                testClock.now() - SECOND_NANOS,
                                testClock.now(),
                                Attributes.empty(),
                                24
                            )
                        )
                    )
            }
        }
    }

    @Test
    fun collectMetrics_WithMultipleCollects() {
        val startTime: Long = testClock.now()
        val longCounter = sdkMeter.counterBuilder("testCounter").build()
        val bound = longCounter.bind(Attributes.builder().put("K", "V").build())
        try {
            // Do some records using bounds and direct calls and bindings.
            longCounter.add(12, Attributes.empty())
            bound.add(123)
            longCounter.add(21, Attributes.empty())
            // Advancing time here should not matter.
            testClock.advance(1.seconds)
            bound.add(321)
            longCounter.add(111, Attributes.builder().put("K", "V").build())
            assertSoftly(sdkMeterReader.collectAllMetrics()) {
                shouldHaveSize(1)
                assertSoftly(single()) {
                    resource shouldBe RESOURCE
                    instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                    name shouldBe "testCounter"
                    type shouldBe MetricDataType.LONG_SUM
                    assertSoftly(longSumData) {
                        isMonotonic.shouldBeTrue()
                        aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                        assertSoftly(points.single { point -> point.value == 33L }) {
                            startEpochNanos shouldBe startTime
                            epochNanos shouldBe testClock.now()
                            attributes shouldBe Attributes.empty()
                        }
                        assertSoftly(points.single { point -> point.value == 555L }) {
                            startEpochNanos shouldBe startTime
                            epochNanos shouldBe testClock.now()
                            attributes shouldBe Attributes.of(stringKey("K"), "V")
                        }
                    }
                }
            }
            // Repeat to prove we keep previous values.
            testClock.advance(1.seconds)
            bound.add(222)
            longCounter.add(11, Attributes.empty())
            assertSoftly(sdkMeterReader.collectAllMetrics()) {
                shouldHaveSize(1)
                assertSoftly(single()) {
                    resource shouldBe RESOURCE
                    instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                    name shouldBe "testCounter"
                    type shouldBe MetricDataType.LONG_SUM
                    assertSoftly(longSumData) {
                        isMonotonic.shouldBeTrue()
                        aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                        assertSoftly(points.single { point -> point.value == 44L }) {
                            startEpochNanos shouldBe startTime
                            epochNanos shouldBe testClock.now()
                            attributes shouldBe Attributes.empty()
                        }
                        assertSoftly(points.single { point -> point.value == 777L }) {
                            startEpochNanos shouldBe startTime
                            epochNanos shouldBe testClock.now()
                            attributes shouldBe Attributes.of(stringKey("K"), "V")
                        }
                    }
                }
            }
        } finally {
            bound.unbind()
        }
    }

    @Test
    fun longCounterAdd_MonotonicityCheck() {
        val longCounter = sdkMeter.counterBuilder("testCounter").build()
        shouldThrow<IllegalArgumentException> { longCounter.add(-45, Attributes.empty()) }
    }

    @Test
    fun boundLongCounterAdd_MonotonicityCheck() {
        val longCounter = sdkMeter.counterBuilder("testCounter").build()
        shouldThrow<IllegalArgumentException> { longCounter.bind(Attributes.empty()).add(-9) }
    }

    @Test
    fun stressTest() = runTest( timeout = 200.seconds ) {
        val longCounter = sdkMeter.counterBuilder("testCounter").build()
        var stressTestBuilder =
            StressTestRunner.builder()
                .setInstrument((longCounter as SdkLongCounter))
                .setCollectionIntervalMs(10)
        for (i in 0..3) {
            stressTestBuilder =
                stressTestBuilder.addOperation(
                    StressTestRunner.Operation.create(
                        2000,
                        1,
                        OperationUpdaterDirectCall(longCounter, "K", "V")
                    )
                )
            stressTestBuilder =
                stressTestBuilder.addOperation(
                    StressTestRunner.Operation.create(
                        2000,
                        1,
                        OperationUpdaterWithBinding(
                            longCounter.bind(Attributes.builder().put("K", "V").build())
                        )
                    )
                )
        }
        stressTestBuilder.build().run()
        assertSoftly(sdkMeterReader.collectAllMetrics()) {
            assertSoftly(single()) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                name shouldBe "testCounter"
                type shouldBe MetricDataType.LONG_SUM
                longSumData.shouldNotBeNull()
                assertSoftly(longSumData) {
                    aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                    assertSoftly(points.single()) {
                        startEpochNanos shouldBe testClock.now()
                        epochNanos shouldBe testClock.now()
                        attributes shouldBe Attributes.of(stringKey("K"), "V")
                        value shouldBe 160000
                    }
                }
            }
        }
    }

    @Test
    fun stressTest_WithDifferentLabelSet() = runTest( timeout = 200.seconds ) {
        val keys = arrayOf("Key_1", "Key_2", "Key_3", "Key_4")
        val values = arrayOf("Value_1", "Value_2", "Value_3", "Value_4")
        val longCounter = sdkMeter.counterBuilder("testCounter").build()
        var stressTestBuilder =
            StressTestRunner.builder()
                .setInstrument((longCounter as SdkLongCounter))
                .setCollectionIntervalMs(10)
        for (i in 0..3) {
            stressTestBuilder =
                stressTestBuilder.addOperation(
                    StressTestRunner.Operation.create(
                        1000,
                        2,
                        OperationUpdaterDirectCall(longCounter, keys[i], values[i])
                    )
                )
            stressTestBuilder =
                stressTestBuilder.addOperation(
                    StressTestRunner.Operation.create(
                        1000,
                        2,
                        OperationUpdaterWithBinding(
                            longCounter.bind(Attributes.builder().put(keys[i], values[i]).build())
                        )
                    )
                )
        }
        stressTestBuilder.build().run()
        assertSoftly(sdkMeterReader.collectAllMetrics()) {
            assertSoftly(single()) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                name shouldBe "testCounter"
                type shouldBe MetricDataType.LONG_SUM
                longSumData.shouldNotBeNull()
                assertSoftly(longSumData) {
                    aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                    points.forEach { point ->
                        assertSoftly(point) {
                            startEpochNanos shouldBe testClock.now()
                            epochNanos shouldBe testClock.now()
                            value shouldBe 20000
                        }
                    }
                    points
                        .map { it.attributes }
                        .shouldContainExactlyInAnyOrder(
                            Attributes.of(stringKey(keys[0]), values[0]),
                            Attributes.of(stringKey(keys[1]), values[1]),
                            Attributes.of(stringKey(keys[2]), values[2]),
                            Attributes.of(stringKey(keys[3]), values[3]),
                        )
                }
            }
        }
    }

    private class OperationUpdaterWithBinding(private val boundLongCounter: BoundLongCounter) :
        StressTestRunner.OperationUpdater() {
        override fun update() {
            boundLongCounter.add(9)
        }

        override fun cleanup() {
            boundLongCounter.unbind()
        }
    }

    private class OperationUpdaterDirectCall(
        private val longCounter: LongCounter,
        private val key: String,
        private val value: String
    ) : StressTestRunner.OperationUpdater() {
        override fun update() {
            longCounter.add(11, Attributes.builder().put(key, value).build())
        }

        override fun cleanup() {}
    }

    companion object {
        private const val SECOND_NANOS: Long = 1000000000
        private val RESOURCE =
            Resource.create(Attributes.of(stringKey("resource_key"), "resource_value"))
        private val INSTRUMENTATION_LIBRARY_INFO =
            InstrumentationLibraryInfo.create(SdkLongCounterTest::class.simpleName!!, null)
    }
}
