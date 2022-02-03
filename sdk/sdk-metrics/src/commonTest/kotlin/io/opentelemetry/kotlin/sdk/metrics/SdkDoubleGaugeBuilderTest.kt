/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.stringKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.*
import io.opentelemetry.kotlin.sdk.metrics.testing.InMemoryMetricReader
import io.opentelemetry.kotlin.sdk.resources.Resource
import io.opentelemetry.kotlin.sdk.testing.time.TestClock
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

/** Unit tests for [DoubleValueObserverSdk]. */
internal class SdkDoubleGaugeBuilderTest {
    private val testClock: TestClock = TestClock.create()
    private val sdkMeterReader = InMemoryMetricReader.create()
    private val sdkMeterProvider =
        SdkMeterProvider.builder()
            .setClock(testClock)
            .setResource(RESOURCE)
            .registerMetricReader(sdkMeterReader)
            .build()
    private val sdkMeter = sdkMeterProvider[SdkDoubleGaugeBuilderTest::class.simpleName!!]

    @Test
    fun collectMetrics_NoRecords() {
        sdkMeter
            .gaugeBuilder("testObserver")
            .setDescription("My own DoubleValueObserver")
            .setUnit("ms")
            .buildWithCallback {}
        sdkMeterReader.collectAllMetrics().shouldBeEmpty()
    }

    @Test
    fun collectMetrics_WithOneRecord() {
        sdkMeter
            .gaugeBuilder("testObserver")
            .setDescription("My own DoubleValueObserver")
            .setUnit("ms")
            .buildWithCallback { result ->
                result.observe(12.1, Attributes.builder().put("k", "v").build())
            }
        testClock.advance(1.seconds)
        assertSoftly(sdkMeterReader.collectAllMetrics()) {
            assertSoftly(single()) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                name shouldBe "testObserver"
                description shouldBe "My own DoubleValueObserver"
                unit shouldBe "ms"
                type shouldBe MetricDataType.DOUBLE_GAUGE
                data shouldBe
                    DoubleGaugeData.create(
                        listOf(
                            DoublePointData.create(
                                testClock.now() - 1000000000L,
                                testClock.now(),
                                Attributes.builder().put("k", "v").build(),
                                12.1
                            )
                        )
                    )
            }
        }
        testClock.advance(1.seconds)
        assertSoftly(sdkMeterReader.collectAllMetrics()) {
            assertSoftly(single()) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                name shouldBe "testObserver"
                description shouldBe "My own DoubleValueObserver"
                unit shouldBe "ms"
                type shouldBe MetricDataType.DOUBLE_GAUGE
                data shouldBe
                    DoubleGaugeData.create(
                        listOf(
                            DoublePointData.create(
                                testClock.now() - 2000000000L,
                                testClock.now(),
                                Attributes.builder().put("k", "v").build(),
                                12.1
                            )
                        )
                    )
            }
        }
    }

    companion object {
        private val RESOURCE =
            Resource.create(Attributes.of(stringKey("resource_key"), "resource_value"))
        private val INSTRUMENTATION_LIBRARY_INFO =
            InstrumentationLibraryInfo.create(SdkDoubleGaugeBuilderTest::class.simpleName!!, null)
    }
}
