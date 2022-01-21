/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.sdk.metrics

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.opentelemetry.api.common.AttributeKey.Companion.stringKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.metrics.BoundDoubleUpDownCounter
import io.opentelemetry.api.metrics.DoubleUpDownCounter
import io.opentelemetry.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.sdk.metrics.data.MetricDataType
import io.opentelemetry.sdk.metrics.testing.InMemoryMetricReader
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.TestClock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

/** Unit tests for [SdkDoubleUpDownCounter]. */
internal class SdkDoubleUpDownCounterTest {
    private val testClock: TestClock = TestClock.create()
    private val sdkMeterReader = InMemoryMetricReader.create()
    private val sdkMeterProvider =
        SdkMeterProvider.builder()
            .setClock(testClock)
            .setResource(RESOURCE)
            .registerMetricReader(sdkMeterReader)
            .build()
    private val sdkMeter = sdkMeterProvider[SdkDoubleUpDownCounterTest::class.simpleName!!]

    @Test
    fun collectMetrics_NoRecords() {
        val doubleUpDownCounter =
            sdkMeter.upDownCounterBuilder("testUpDownCounter").ofDoubles().build()
        val bound = doubleUpDownCounter.bind(Attributes.builder().put("foo", "bar").build())
        try {
            sdkMeterReader.collectAllMetrics().shouldBeEmpty()
        } finally {
            bound.unbind()
        }
    }

    @Test
    fun collectMetrics_WithEmptyAttributes() {
        val doubleUpDownCounter =
            sdkMeter
                .upDownCounterBuilder("testUpDownCounter")
                .ofDoubles()
                .setDescription("description")
                .setUnit("ms")
                .build()
        testClock.advance(1.seconds)
        doubleUpDownCounter.add(12.0, Attributes.empty())
        doubleUpDownCounter.add(12.0)
        assertSoftly(sdkMeterReader.collectAllMetrics()) {
            assertSoftly(single()) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                name shouldBe "testUpDownCounter"
                description shouldBe "description"
                unit shouldBe "ms"
                type shouldBe MetricDataType.DOUBLE_SUM
                doubleSumData.shouldNotBeNull()
                assertSoftly(doubleSumData) {
                    isMonotonic.shouldBeFalse()
                    aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                    assertSoftly(points.single()) {
                        startEpochNanos shouldBe testClock.now() - SECOND_NANOS
                        epochNanos shouldBe testClock.now()
                        attributes shouldBe Attributes.empty()
                        value shouldBe 24.0
                    }
                }
            }
        }
    }

    @Test
    fun collectMetrics_WithMultipleCollects() {
        val startTime: Long = testClock.now()
        val doubleUpDownCounter =
            sdkMeter.upDownCounterBuilder("testUpDownCounter").ofDoubles().build()
        val bound = doubleUpDownCounter.bind(Attributes.builder().put("K", "V").build())
        try {
            // Do some records using bounds and direct calls and bindings.
            doubleUpDownCounter.add(12.1, Attributes.empty())
            bound.add(123.3)
            doubleUpDownCounter.add(21.4, Attributes.empty())
            // Advancing time here should not matter.
            testClock.advance(1.seconds)
            bound.add(321.5)
            doubleUpDownCounter.add(111.1, Attributes.builder().put("K", "V").build())
            assertSoftly(sdkMeterReader.collectAllMetrics()) {
                assertSoftly(single()) {
                    resource shouldBe RESOURCE
                    instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                    name shouldBe "testUpDownCounter"
                    type shouldBe MetricDataType.DOUBLE_SUM
                    doubleSumData.shouldNotBeNull()
                    assertSoftly(doubleSumData) {
                        isMonotonic.shouldBeFalse()
                        aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                        assertSoftly(points.single { point -> point.value == 33.5 }) {
                            startEpochNanos shouldBe startTime
                            epochNanos shouldBe testClock.now()
                            attributes shouldBe Attributes.empty()
                        }
                        assertSoftly(points.single { point -> point.value == 555.9 }) {
                            startEpochNanos shouldBe startTime
                            epochNanos shouldBe testClock.now()
                            attributes shouldBe Attributes.of(stringKey("K"), "V")
                        }
                    }
                }
            }
            // Repeat to prove we keep previous values.
            testClock.advance(1.seconds)
            bound.add(222.0)
            doubleUpDownCounter.add(11.0, Attributes.empty())
            assertSoftly(sdkMeterReader.collectAllMetrics()) {
                assertSoftly(single()) {
                    resource shouldBe RESOURCE
                    instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                    name shouldBe "testUpDownCounter"
                    type shouldBe MetricDataType.DOUBLE_SUM
                    doubleSumData.shouldNotBeNull()
                    assertSoftly(doubleSumData) {
                        isMonotonic.shouldBeFalse()
                        aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                        assertSoftly(points.single { point -> point.value == 44.5 }) {
                            startEpochNanos shouldBe startTime
                            epochNanos shouldBe testClock.now()
                            attributes shouldBe Attributes.empty()
                        }
                        assertSoftly(points.single { point -> point.value == 777.9 }) {
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
    fun stressTest() = runTest {
        val doubleUpDownCounter =
            sdkMeter.upDownCounterBuilder("testUpDownCounter").ofDoubles().build()
        var stressTestBuilder =
            StressTestRunner.builder()
                .setInstrument((doubleUpDownCounter as SdkDoubleUpDownCounter))
                .setCollectionIntervalMs(10)
        for (i in 0..3) {
            stressTestBuilder =
                stressTestBuilder.addOperation(
                    StressTestRunner.Operation.create(
                        1000,
                        2,
                        OperationUpdaterDirectCall(doubleUpDownCounter, "K", "V")
                    )
                )
            stressTestBuilder =
                stressTestBuilder.addOperation(
                    StressTestRunner.Operation.create(
                        1000,
                        2,
                        OperationUpdaterWithBinding(
                            doubleUpDownCounter.bind(Attributes.builder().put("K", "V").build())
                        )
                    )
                )
        }
        stressTestBuilder.build().run()
        assertSoftly(sdkMeterReader.collectAllMetrics()) {
            assertSoftly(single()) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                name shouldBe "testUpDownCounter"
                type shouldBe MetricDataType.DOUBLE_SUM
                doubleSumData.shouldNotBeNull()
                assertSoftly(doubleSumData) {
                    aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                    assertSoftly(points.single()) {
                        startEpochNanos shouldBe testClock.now()
                        epochNanos shouldBe testClock.now()
                        attributes shouldBe Attributes.of(stringKey("K"), "V")
                        value shouldBe 80000
                    }
                }
            }
        }
    }

    @Test
    fun stressTest_WithDifferentLabelSet() = runTest {
        val keys = arrayOf("Key_1", "Key_2", "Key_3", "Key_4")
        val values = arrayOf("Value_1", "Value_2", "Value_3", "Value_4")
        val doubleUpDownCounter =
            sdkMeter.upDownCounterBuilder("testUpDownCounter").ofDoubles().build()
        var stressTestBuilder =
            StressTestRunner.builder()
                .setInstrument((doubleUpDownCounter as SdkDoubleUpDownCounter))
                .setCollectionIntervalMs(10)
        for (i in 0..3) {
            stressTestBuilder =
                stressTestBuilder.addOperation(
                    StressTestRunner.Operation.create(
                        2000,
                        1,
                        OperationUpdaterDirectCall(doubleUpDownCounter, keys[i], values[i])
                    )
                )
            stressTestBuilder =
                stressTestBuilder.addOperation(
                    StressTestRunner.Operation.create(
                        2000,
                        1,
                        OperationUpdaterWithBinding(
                            doubleUpDownCounter.bind(
                                Attributes.builder().put(keys[i], values[i]).build()
                            )
                        )
                    )
                )
        }
        stressTestBuilder.build().run()
        assertSoftly(sdkMeterReader.collectAllMetrics()) {
            assertSoftly(single()) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                name shouldBe "testUpDownCounter"
                type shouldBe MetricDataType.DOUBLE_SUM
                doubleSumData.shouldNotBeNull()
                assertSoftly(doubleSumData) {
                    aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                    points.forEach { point ->
                        assertSoftly(point) {
                            startEpochNanos shouldBe testClock.now()
                            epochNanos shouldBe testClock.now()
                            value shouldBe 40000
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

    private class OperationUpdaterWithBinding(
        private val boundDoubleUpDownCounter: BoundDoubleUpDownCounter
    ) : StressTestRunner.OperationUpdater() {
        override fun update() {
            boundDoubleUpDownCounter.add(9.0)
        }

        override fun cleanup() {
            boundDoubleUpDownCounter.unbind()
        }
    }

    private class OperationUpdaterDirectCall(
        private val doubleUpDownCounter: DoubleUpDownCounter,
        private val key: String,
        private val value: String
    ) : StressTestRunner.OperationUpdater() {
        override fun update() {
            doubleUpDownCounter.add(11.0, Attributes.builder().put(key, value).build())
        }

        override fun cleanup() {}
    }

    companion object {
        private const val SECOND_NANOS: Long = 1000000000
        private val RESOURCE =
            Resource.create(Attributes.of(stringKey("resource_key"), "resource_value"))
        private val INSTRUMENTATION_LIBRARY_INFO =
            InstrumentationLibraryInfo.create(SdkDoubleUpDownCounterTest::class.simpleName!!, null)
    }
}
