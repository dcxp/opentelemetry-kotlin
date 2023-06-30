/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.stringKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.metrics.BoundDoubleHistogram
import io.opentelemetry.kotlin.api.metrics.DoubleHistogram
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.MetricDataType
import io.opentelemetry.kotlin.sdk.metrics.testing.InMemoryMetricReader
import io.opentelemetry.kotlin.sdk.resources.Resource
import io.opentelemetry.kotlin.sdk.testing.time.TestClock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

/** Unit tests for [SdkDoubleHistogram]. */
internal class SdkDoubleHistogramTest {
    private val testClock: TestClock = TestClock.create()
    private val sdkMeterReader = InMemoryMetricReader.create()
    private val sdkMeterProvider =
        SdkMeterProvider.builder()
            .setClock(testClock)
            .setResource(RESOURCE)
            .registerMetricReader(sdkMeterReader)
            .build()
    private val sdkMeter = sdkMeterProvider[SdkDoubleHistogramTest::class.simpleName!!]

    @Test
    fun collectMetrics_NoRecords() {
        val doubleRecorder = sdkMeter.histogramBuilder("testRecorder").build()
        val bound = doubleRecorder.bind(Attributes.builder().put("key", "value").build())
        try {
            sdkMeterReader.collectAllMetrics().shouldBeEmpty()
        } finally {
            bound.unbind()
        }
    }

    @Test
    fun collectMetrics_WithEmptyAttributes() {
        val doubleRecorder =
            sdkMeter
                .histogramBuilder("testRecorder")
                .setDescription("description")
                .setUnit("ms")
                .build()
        testClock.advance(1.seconds)
        doubleRecorder.record(12.0, Attributes.empty())
        doubleRecorder.record(12.0)
        assertSoftly(sdkMeterReader.collectAllMetrics()) {
            assertSoftly(single()) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                name shouldBe "testRecorder"
                description shouldBe "description"
                unit shouldBe "ms"
                type shouldBe MetricDataType.HISTOGRAM
                doubleHistogramData.shouldNotBeNull()
                assertSoftly(doubleHistogramData) {
                    aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                    assertSoftly(points.single()) {
                        startEpochNanos shouldBe testClock.now() - SECOND_NANOS
                        epochNanos shouldBe testClock.now()
                        attributes shouldBe Attributes.empty()
                        count shouldBe 2
                        sum shouldBe 24
                        boundaries.shouldContainInOrder(
                            5.0,
                            10.0,
                            25.0,
                            50.0,
                            75.0,
                            100.0,
                            250.0,
                            500.0,
                            750.0,
                            1000.0,
                            2500.0,
                            5000.0,
                            7500.0,
                            10000.0
                        )
                        counts.shouldContainInOrder(0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
                    }
                }
            }
        }
    }

    @Test
    fun collectMetrics_WithMultipleCollects() {
        val startTime: Long = testClock.now()
        val doubleRecorder = sdkMeter.histogramBuilder("testRecorder").build()
        val bound = doubleRecorder.bind(Attributes.builder().put("K", "V").build())
        try {
            // Do some records using bounds and direct calls and bindings.
            doubleRecorder.record(12.1, Attributes.empty())
            bound.record(123.3)
            doubleRecorder.record(-13.1, Attributes.empty())
            // Advancing time here should not matter.
            testClock.advance(1.seconds)
            bound.record(321.5)
            doubleRecorder.record(-121.5, Attributes.builder().put("K", "V").build())
            assertSoftly(sdkMeterReader.collectAllMetrics()) {
                assertSoftly(single()) {
                    resource shouldBe RESOURCE
                    instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                    name shouldBe "testRecorder"
                    type shouldBe MetricDataType.HISTOGRAM
                    doubleHistogramData.shouldNotBeNull()
                    assertSoftly(doubleHistogramData) {
                        aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                        assertSoftly(points.single { it.sum == 323.3 }) {
                            startEpochNanos shouldBe startTime
                            epochNanos shouldBe testClock.now()
                            attributes shouldBe Attributes.builder().put("K", "V").build()
                            count shouldBe 3
                            sum shouldBe 323.3
                            counts.shouldContainInOrder(1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0)
                        }
                        assertSoftly(points.single { it.sum == -1.0 }) {
                            startEpochNanos shouldBe startTime
                            epochNanos shouldBe testClock.now()
                            attributes shouldBe Attributes.empty()
                            count shouldBe 2
                            sum shouldBe -1.0
                            counts.shouldContainInOrder(1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
                        }
                    }
                }
            }

            // Histograms are cumulative by default.
            testClock.advance(1.seconds)
            bound.record(222.0)
            doubleRecorder.record(17.0, Attributes.empty())
            assertSoftly(sdkMeterReader.collectAllMetrics()) {
                assertSoftly(single()) {
                    resource shouldBe RESOURCE
                    instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                    name shouldBe "testRecorder"
                    type shouldBe MetricDataType.HISTOGRAM
                    doubleHistogramData.shouldNotBeNull()
                    assertSoftly(doubleHistogramData) {
                        aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                        assertSoftly(points.single { it.sum == 545.3 }) {
                            startEpochNanos shouldBe startTime
                            epochNanos shouldBe testClock.now()
                            attributes shouldBe Attributes.builder().put("K", "V").build()
                            count shouldBe 4
                            sum shouldBe 545.3
                            counts.shouldContainInOrder(1, 0, 0, 0, 0, 0, 2, 1, 0, 0, 0, 0, 0, 0, 0)
                        }
                        assertSoftly(points.single { it.sum == 16.0 }) {
                            startEpochNanos shouldBe startTime
                            epochNanos shouldBe testClock.now()
                            attributes shouldBe Attributes.empty()
                            count shouldBe 3
                            sum shouldBe 16.0
                            counts.shouldContainInOrder(1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
                        }
                    }
                }
            }
        } finally {
            bound.unbind()
        }
    }

    @Test
    fun stressTest() = runTest( timeout = 200.seconds ) {
        val doubleRecorder = sdkMeter.histogramBuilder("testRecorder").build()
        var stressTestBuilder =
            StressTestRunner.builder()
                .setInstrument((doubleRecorder as SdkDoubleHistogram))
                .setCollectionIntervalMs(10)
        for (i in 0..3) {
            stressTestBuilder =
                stressTestBuilder.addOperation(
                    StressTestRunner.Operation.create(
                        1000,
                        2,
                        OperationUpdaterDirectCall(doubleRecorder, "K", "V")
                    )
                )
            stressTestBuilder =
                stressTestBuilder.addOperation(
                    StressTestRunner.Operation.create(
                        1000,
                        2,
                        OperationUpdaterWithBinding(
                            doubleRecorder.bind(Attributes.builder().put("K", "V").build())
                        )
                    )
                )
        }
        stressTestBuilder.build().run()
        assertSoftly(sdkMeterReader.collectAllMetrics()) {
            assertSoftly(single()) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                name shouldBe "testRecorder"
                type shouldBe MetricDataType.HISTOGRAM
                doubleHistogramData.shouldNotBeNull()
                assertSoftly(doubleHistogramData) {
                    aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                    assertSoftly(points.single()) {
                        startEpochNanos shouldBe testClock.now()
                        epochNanos shouldBe testClock.now()
                        attributes shouldBe Attributes.of(stringKey("K"), "V")
                        count shouldBe 8000
                        sum shouldBe 80000
                    }
                }
            }
        }
    }

    @Test
    fun stressTest_WithDifferentLabelSet() = runTest( timeout = 200.seconds ) {
        val keys = arrayOf("Key_1", "Key_2", "Key_3", "Key_4")
        val values = arrayOf("Value_1", "Value_2", "Value_3", "Value_4")
        val doubleRecorder = sdkMeter.histogramBuilder("testRecorder").build()
        var stressTestBuilder =
            StressTestRunner.builder()
                .setInstrument((doubleRecorder as SdkDoubleHistogram))
                .setCollectionIntervalMs(10)
        for (i in 0..3) {
            stressTestBuilder =
                stressTestBuilder.addOperation(
                    StressTestRunner.Operation.create(
                        2000,
                        1,
                        OperationUpdaterDirectCall(doubleRecorder, keys[i], values[i])
                    )
                )
            stressTestBuilder =
                stressTestBuilder.addOperation(
                    StressTestRunner.Operation.create(
                        2000,
                        1,
                        OperationUpdaterWithBinding(
                            doubleRecorder.bind(
                                Attributes.builder().put(keys[i], values[i]).build()
                            )
                        )
                    )
                )
        }
        stressTestBuilder.build().run()
        val result = sdkMeterReader.collectAllMetrics()
        assertSoftly(result.single()) {
            resource shouldBe RESOURCE
            instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
            name shouldBe "testRecorder"
            type shouldBe MetricDataType.HISTOGRAM
            doubleHistogramData.shouldNotBeNull()
            assertSoftly(doubleHistogramData) {
                aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                points.forEach { point ->
                    assertSoftly(point) {
                        startEpochNanos shouldBe testClock.now()
                        epochNanos shouldBe testClock.now()
                        count shouldBe 4000
                        sum shouldBe 40000
                        counts.shouldContainInOrder(
                            0,
                            2000,
                            2000,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0
                        )
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

    private class OperationUpdaterWithBinding(
        private val boundDoubleValueRecorder: BoundDoubleHistogram
    ) : StressTestRunner.OperationUpdater() {
        override fun update() {
            boundDoubleValueRecorder.record(11.0)
        }

        override fun cleanup() {
            boundDoubleValueRecorder.unbind()
        }
    }

    private class OperationUpdaterDirectCall(
        private val doubleValueRecorder: DoubleHistogram,
        private val key: String,
        private val value: String
    ) : StressTestRunner.OperationUpdater() {
        override fun update() {
            doubleValueRecorder.record(9.0, Attributes.builder().put(key, value).build())
        }

        override fun cleanup() {}
    }

    companion object {
        private const val SECOND_NANOS: Long = 1000000000
        private val RESOURCE =
            Resource.create(Attributes.of(stringKey("resource_key"), "resource_value"))
        private val INSTRUMENTATION_LIBRARY_INFO =
            InstrumentationLibraryInfo.create(SdkDoubleHistogramTest::class.simpleName!!, null)
    }
}
