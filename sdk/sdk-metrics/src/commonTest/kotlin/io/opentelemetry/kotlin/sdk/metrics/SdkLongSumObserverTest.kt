/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.stringKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentType
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.MetricDataType
import io.opentelemetry.kotlin.sdk.metrics.testing.InMemoryMetricReader
import io.opentelemetry.kotlin.sdk.metrics.view.Aggregation
import io.opentelemetry.kotlin.sdk.metrics.view.InstrumentSelector
import io.opentelemetry.kotlin.sdk.metrics.view.View
import io.opentelemetry.kotlin.sdk.resources.Resource
import io.opentelemetry.kotlin.sdk.testing.time.TestClock
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

/** Unit tests for [LongSumObserverSdk]. */
internal class SdkLongSumObserverTest {
    private val testClock: TestClock = TestClock.create()
    private val sdkMeterProviderBuilder =
        SdkMeterProvider.builder().setClock(testClock).setResource(RESOURCE)
    @Test
    fun collectMetrics_NoRecords() {
        val sdkMeterReader = InMemoryMetricReader.create()
        val sdkMeterProvider = sdkMeterProviderBuilder.registerMetricReader(sdkMeterReader).build()
        sdkMeterProvider[SdkLongSumObserverTest::class.simpleName!!]
            .counterBuilder("testObserver")
            .setDescription("My own LongSumObserver")
            .setUnit("ms")
            .buildWithCallback {}
        sdkMeterReader.collectAllMetrics().shouldBeEmpty()
    }

    @Test
    fun collectMetrics_WithOneRecord() {
        val sdkMeterReader = InMemoryMetricReader.create()
        val sdkMeterProvider = sdkMeterProviderBuilder.registerMetricReader(sdkMeterReader).build()
        sdkMeterProvider[SdkLongSumObserverTest::class.simpleName!!].counterBuilder("testObserver")
            .buildWithCallback { result ->
                result.observe(12, Attributes.builder().put("k", "v").build())
            }
        testClock.advance(1.seconds)
        assertSoftly(sdkMeterReader.collectAllMetrics()) {
            assertSoftly(single()) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                name shouldBe "testObserver"
                type shouldBe MetricDataType.LONG_SUM
                longSumData.shouldNotBeNull()
                assertSoftly(longSumData) {
                    isMonotonic.shouldBeTrue()
                    aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                    assertSoftly(points.single()) {
                        startEpochNanos shouldBe testClock.now() - SECOND_NANOS
                        epochNanos shouldBe testClock.now()
                        attributes shouldBe Attributes.of(stringKey("k"), "v")
                        value shouldBe 12
                    }
                }
            }
        }
        testClock.advance(1.seconds)
        assertSoftly(sdkMeterReader.collectAllMetrics()) {
            assertSoftly(single()) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                name shouldBe "testObserver"
                type shouldBe MetricDataType.LONG_SUM
                longSumData.shouldNotBeNull()
                assertSoftly(longSumData) {
                    isMonotonic.shouldBeTrue()
                    aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                    assertSoftly(points.single()) {
                        startEpochNanos shouldBe testClock.now() - 2 * SECOND_NANOS
                        epochNanos shouldBe testClock.now()
                        attributes shouldBe Attributes.of(stringKey("k"), "v")
                        value shouldBe 12
                    }
                }
            }
        }
    }

    @Test
    fun collectMetrics_DeltaSumAggregator() {
        val sdkMeterReader = InMemoryMetricReader.createDelta()
        val sdkMeterProvider =
            sdkMeterProviderBuilder
                .registerMetricReader(sdkMeterReader)
                .registerView(
                    InstrumentSelector.builder()
                        .setInstrumentType(InstrumentType.OBSERVABLE_SUM)
                        .build(),
                    View.builder().setAggregation(Aggregation.sum()).build()
                )
                .build()
        sdkMeterProvider[SdkLongSumObserverTest::class.simpleName!!].counterBuilder("testObserver")
            .buildWithCallback { result ->
                result.observe(12, Attributes.builder().put("k", "v").build())
            }
        testClock.advance(1.seconds)
        assertSoftly(sdkMeterReader.collectAllMetrics()) {
            assertSoftly(single()) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                name shouldBe "testObserver"
                type shouldBe MetricDataType.LONG_SUM
                longSumData.shouldNotBeNull()
                assertSoftly(longSumData) {
                    isMonotonic.shouldBeTrue()
                    aggregationTemporality shouldBe AggregationTemporality.DELTA
                    assertSoftly(points.single()) {
                        startEpochNanos shouldBe testClock.now() - SECOND_NANOS
                        epochNanos shouldBe testClock.now()
                        attributes shouldBe Attributes.of(stringKey("k"), "v")
                        value shouldBe 12
                    }
                }
            }
        }
        testClock.advance(1.seconds)
        assertSoftly(sdkMeterReader.collectAllMetrics()) {
            assertSoftly(single()) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                name shouldBe "testObserver"
                type shouldBe MetricDataType.LONG_SUM
                longSumData.shouldNotBeNull()
                assertSoftly(longSumData) {
                    isMonotonic.shouldBeTrue()
                    aggregationTemporality shouldBe AggregationTemporality.DELTA
                    assertSoftly(points.single()) {
                        startEpochNanos shouldBe testClock.now() - SECOND_NANOS
                        epochNanos shouldBe testClock.now()
                        attributes shouldBe Attributes.of(stringKey("k"), "v")
                        value shouldBe 0
                    }
                }
            }
        }
    }

    companion object {
        private const val SECOND_NANOS: Long = 1000000000
        private val RESOURCE =
            Resource.create(Attributes.of(stringKey("resource_key"), "resource_value"))
        private val INSTRUMENTATION_LIBRARY_INFO =
            InstrumentationLibraryInfo.create(SdkLongSumObserverTest::class.simpleName!!, null)
    }
}
