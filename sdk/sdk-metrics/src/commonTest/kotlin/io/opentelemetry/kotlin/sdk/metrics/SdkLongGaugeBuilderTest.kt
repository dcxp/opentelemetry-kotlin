/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.stringKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.MetricDataType
import io.opentelemetry.kotlin.sdk.metrics.testing.InMemoryMetricReader
import io.opentelemetry.kotlin.sdk.resources.Resource
import io.opentelemetry.kotlin.sdk.testing.time.TestClock
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

/** Unit tests for [LongValueObserverSdk]. */
internal class SdkLongGaugeBuilderTest {
    private val testClock: TestClock = TestClock.create()
    private val sdkMeterReader = InMemoryMetricReader.create()
    private val sdkMeterProvider =
        SdkMeterProvider.builder()
            .setClock(testClock)
            .setResource(RESOURCE)
            .registerMetricReader(sdkMeterReader)
            .build()
    private val sdkMeter = sdkMeterProvider[SdkLongGaugeBuilderTest::class.simpleName!!]
    @Test
    fun collectMetrics_NoRecords() {
        sdkMeter
            .gaugeBuilder("testObserver")
            .ofLongs()
            .setDescription("My own LongValueObserver")
            .setUnit("ms")
            .buildWithCallback {}
        sdkMeterReader.collectAllMetrics().shouldBeEmpty()
    }

    @Test
    fun collectMetrics_WithOneRecord() {
        sdkMeter.gaugeBuilder("testObserver").ofLongs().buildWithCallback { result ->
            result.observe(12, Attributes.builder().put("k", "v").build())
        }
        testClock.advance(1.seconds)
        assertSoftly(sdkMeterReader.collectAllMetrics()) {
            shouldHaveSize(1)
            assertSoftly(single()) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                name shouldBe "testObserver"
                type shouldBe MetricDataType.LONG_GAUGE
                longGaugeData.shouldNotBeNull()
                assertSoftly(longGaugeData) {
                    assertSoftly(points.single()) {
                        startEpochNanos shouldBe testClock.now() - 1000000000L
                        epochNanos shouldBe testClock.now()
                        attributes shouldBe Attributes.builder().put("k", "v").build()
                        value shouldBe 12
                    }
                }
            }
        }
        testClock.advance(1.seconds)
        assertSoftly(sdkMeterReader.collectAllMetrics()) {
            shouldHaveSize(1)
            assertSoftly(single()) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                name shouldBe "testObserver"
                type shouldBe MetricDataType.LONG_GAUGE
                longGaugeData.shouldNotBeNull()
                assertSoftly(longGaugeData) {
                    assertSoftly(points.single()) {
                        startEpochNanos shouldBe testClock.now() - 2000000000L
                        epochNanos shouldBe testClock.now()
                        attributes shouldBe Attributes.builder().put("k", "v").build()
                        value shouldBe 12
                    }
                }
            }
        }
    }

    companion object {
        private val RESOURCE =
            Resource.create(Attributes.of(stringKey("resource_key"), "resource_value"))
        private val INSTRUMENTATION_LIBRARY_INFO =
            InstrumentationLibraryInfo.create(SdkLongGaugeBuilderTest::class.simpleName!!, null)
    }
}
