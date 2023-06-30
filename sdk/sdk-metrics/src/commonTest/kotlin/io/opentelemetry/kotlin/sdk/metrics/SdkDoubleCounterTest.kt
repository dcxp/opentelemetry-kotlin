/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.stringKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.metrics.BoundDoubleCounter
import io.opentelemetry.kotlin.api.metrics.DoubleCounter
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.DoublePointData
import io.opentelemetry.kotlin.sdk.metrics.data.DoubleSumData
import io.opentelemetry.kotlin.sdk.metrics.data.MetricDataType
import io.opentelemetry.kotlin.sdk.metrics.testing.InMemoryMetricReader
import io.opentelemetry.kotlin.sdk.resources.Resource
import io.opentelemetry.kotlin.sdk.testing.time.TestClock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

/** Unit tests for [SdkDoubleCounter]. */
class SdkDoubleCounterTest {
    private val testClock: TestClock = TestClock.create()
    private val sdkMeterReader = InMemoryMetricReader.create()
    private val sdkMeterProvider =
        SdkMeterProvider.builder()
            .setClock(testClock)
            .registerMetricReader(sdkMeterReader)
            .setResource(RESOURCE)
            .build()
    private val sdkMeter = sdkMeterProvider[SdkDoubleCounterTest::class.simpleName!!]

    @Test
    fun collectMetrics_NoRecords() {
        val doubleCounter = sdkMeter.counterBuilder("testCounter").ofDoubles().build()
        val bound = doubleCounter.bind(Attributes.builder().put("foo", "bar").build())
        try {
            sdkMeterReader.collectAllMetrics().shouldBeEmpty()
        } finally {
            bound.unbind()
        }
    }

    @Test
    fun collectMetrics_WithEmptyAttributes() {
        val doubleCounter =
            sdkMeter
                .counterBuilder("testCounter")
                .ofDoubles()
                .setDescription("description")
                .setUnit("ms")
                .build()
        testClock.advance(SECOND_NANOS.nanoseconds)
        doubleCounter.add(12.0, Attributes.empty())
        doubleCounter.add(12.0)
        assertSoftly(sdkMeterReader.collectAllMetrics()) {
            shouldHaveSize(1)
            assertSoftly(single()) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                name shouldBe "testCounter"
                description shouldBe "description"
                unit shouldBe "ms"
                type shouldBe MetricDataType.DOUBLE_SUM
                data shouldBe
                    DoubleSumData.create(
                        true,
                        AggregationTemporality.CUMULATIVE,
                        listOf(
                            DoublePointData.create(
                                testClock.now() - SECOND_NANOS,
                                testClock.now(),
                                Attributes.empty(),
                                24.0
                            )
                        )
                    )
            }
        }
    }

    @Test
    fun collectMetrics_WithMultipleCollects() {
        val startTime: Long = testClock.now()
        val doubleCounter = sdkMeter.counterBuilder("testCounter").ofDoubles().build()
        val bound = doubleCounter.bind(Attributes.builder().put("K", "V").build())
        try {
            // Do some records using bounds and direct calls and bindings.
            doubleCounter.add(12.1, Attributes.empty())
            bound.add(123.3)
            doubleCounter.add(21.4, Attributes.empty())
            // Advancing time here should not matter.
            testClock.advance(SECOND_NANOS.nanoseconds)
            bound.add(321.5)
            doubleCounter.add(111.1, Attributes.builder().put("K", "V").build())
            assertSoftly(sdkMeterReader.collectAllMetrics()) {
                shouldHaveSize(1)
                assertSoftly(single()) {
                    resource shouldBe RESOURCE
                    instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                    name shouldBe "testCounter"
                    description shouldBe ""
                    unit shouldBe "1"
                    type shouldBe MetricDataType.DOUBLE_SUM
                    assertSoftly(data as DoubleSumData) {
                        isMonotonic shouldBe true
                        aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                        points.shouldContainExactlyInAnyOrder(
                            DoublePointData.create(
                                testClock.now() - SECOND_NANOS,
                                testClock.now(),
                                Attributes.empty(),
                                33.5
                            ),
                            DoublePointData.create(
                                testClock.now() - SECOND_NANOS,
                                testClock.now(),
                                Attributes.of(stringKey("K"), "V"),
                                555.9
                            )
                        )
                    }
                }
            }
            // Repeat to prove we keep previous values.
            testClock.advance(SECOND_NANOS.nanoseconds)
            bound.add(222.0)
            doubleCounter.add(11.0, Attributes.empty())
            assertSoftly(sdkMeterReader.collectAllMetrics()) {
                shouldHaveSize(1)
                assertSoftly(single()) {
                    resource shouldBe RESOURCE
                    instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                    name shouldBe "testCounter"
                    description shouldBe ""
                    unit shouldBe "1"
                    type shouldBe MetricDataType.DOUBLE_SUM
                    assertSoftly(data as DoubleSumData) {
                        isMonotonic shouldBe true
                        aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                        points.shouldContainExactlyInAnyOrder(
                            DoublePointData.create(
                                startTime,
                                testClock.now(),
                                Attributes.empty(),
                                44.5
                            ),
                            DoublePointData.create(
                                startTime,
                                testClock.now(),
                                Attributes.of(stringKey("K"), "V"),
                                777.9
                            )
                        )
                    }
                }
            }
        } finally {
            bound.unbind()
        }
    }

    @Test
    fun doubleCounterAdd_Monotonicity() {
        val doubleCounter = sdkMeter.counterBuilder("testCounter").ofDoubles().build()
        shouldThrow<IllegalArgumentException> { doubleCounter.add(-45.77, Attributes.empty()) }
    }

    @Test
    fun boundDoubleCounterAdd_Monotonicity() {
        val doubleCounter = sdkMeter.counterBuilder("testCounter").ofDoubles().build()
        shouldThrow<IllegalArgumentException> { doubleCounter.bind(Attributes.empty()).add(-9.3) }
    }

    @Test
    fun stressTest() = runTest( timeout = 200.seconds ) {
        val doubleCounter = sdkMeter.counterBuilder("testCounter").ofDoubles().build()
        var stressTestBuilder: StressTestRunner.Builder =
            StressTestRunner.builder()
                .setInstrument(doubleCounter as SdkDoubleCounter)
                .setCollectionIntervalMs(10)
        for (i in 0..3) {
            stressTestBuilder =
                stressTestBuilder
                    .addOperation(
                        StressTestRunner.Operation.create(
                            1000,
                            2,
                            OperationUpdaterDirectCall(doubleCounter, "K", "V")
                        )
                    )
                    .addOperation(
                        StressTestRunner.Operation.create(
                            1000,
                            2,
                            OperationUpdaterWithBinding(
                                doubleCounter.bind(Attributes.builder().put("K", "V").build())
                            )
                        )
                    )
        }
        stressTestBuilder.build().run()
        assertSoftly(sdkMeterReader.collectAllMetrics()) {
            shouldHaveSize(1)
            assertSoftly(single()) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                name shouldBe "testCounter"
                description shouldBe ""
                unit shouldBe "1"
                type shouldBe MetricDataType.DOUBLE_SUM
                data shouldBe
                    DoubleSumData.create(
                        true,
                        AggregationTemporality.CUMULATIVE,
                        listOf(
                            DoublePointData.create(
                                testClock.now(),
                                testClock.now(),
                                Attributes.of(stringKey("K"), "V"),
                                80000.0
                            )
                        )
                    )
            }
        }
    }

    @Test
    fun stressTest_WithDifferentLabelSet() = runTest( timeout = 200.seconds ) {
        val keys = arrayOf("Key_1", "Key_2", "Key_3", "Key_4")
        val values = arrayOf("Value_1", "Value_2", "Value_3", "Value_4")
        val doubleCounter = sdkMeter.counterBuilder("testCounter").ofDoubles().build()
        var stressTestBuilder: StressTestRunner.Builder =
            StressTestRunner.builder()
                .setInstrument(doubleCounter as SdkDoubleCounter)
                .setCollectionIntervalMs(10)
        for (i in 0..3) {
            stressTestBuilder =
                stressTestBuilder
                    .addOperation(
                        StressTestRunner.Operation.create(
                            2000,
                            1,
                            OperationUpdaterDirectCall(doubleCounter, keys[i], values[i])
                        )
                    )
                    .addOperation(
                        StressTestRunner.Operation.create(
                            2000,
                            1,
                            OperationUpdaterWithBinding(
                                doubleCounter.bind(
                                    Attributes.builder().put(keys[i], values[i]).build()
                                )
                            )
                        )
                    )
        }
        stressTestBuilder.build().run()
        assertSoftly(sdkMeterReader.collectAllMetrics()) {
            shouldHaveSize(1)
            assertSoftly(single()) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                name shouldBe "testCounter"
                description shouldBe ""
                unit shouldBe "1"
                type shouldBe MetricDataType.DOUBLE_SUM
                assertSoftly(data as DoubleSumData) {
                    isMonotonic shouldBe true
                    aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                    points.shouldContainExactlyInAnyOrder(
                        DoublePointData.create(
                            testClock.now(),
                            testClock.now(),
                            Attributes.of(
                                stringKey(keys[0]),
                                values[0],
                            ),
                            40000.0
                        ),
                        DoublePointData.create(
                            testClock.now(),
                            testClock.now(),
                            Attributes.of(
                                stringKey(keys[1]),
                                values[1],
                            ),
                            40000.0
                        ),
                        DoublePointData.create(
                            testClock.now(),
                            testClock.now(),
                            Attributes.of(
                                stringKey(keys[2]),
                                values[2],
                            ),
                            40000.0
                        ),
                        DoublePointData.create(
                            testClock.now(),
                            testClock.now(),
                            Attributes.of(
                                stringKey(keys[3]),
                                values[3],
                            ),
                            40000.0
                        )
                    )
                }
            }
        }
    }

    internal class OperationUpdaterWithBinding
    constructor(private val boundDoubleCounter: BoundDoubleCounter) :
        StressTestRunner.OperationUpdater() {
        override fun update() {
            boundDoubleCounter.add(9.0)
        }

        override fun cleanup() {
            boundDoubleCounter.unbind()
        }
    }

    internal class OperationUpdaterDirectCall
    constructor(
        private val doubleCounter: DoubleCounter,
        private val key: String,
        private val value: String
    ) : StressTestRunner.OperationUpdater() {
        override fun update() {
            doubleCounter.add(11.0, Attributes.builder().put(key, value).build())
        }

        override fun cleanup() {}
    }

    companion object {
        private const val SECOND_NANOS: Long = 1000000000
        private val RESOURCE =
            Resource.create(Attributes.of(stringKey("resource_key"), "resource_value"))
        private val INSTRUMENTATION_LIBRARY_INFO =
            InstrumentationLibraryInfo.create(SdkDoubleCounterTest::class.simpleName!!, null)
    }
}
