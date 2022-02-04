/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.testing.InMemoryMetricReader
import io.opentelemetry.kotlin.sdk.resources.Resource
import io.opentelemetry.kotlin.sdk.testing.time.TestClock
import kotlin.test.Test

/** Unit tests for [SdkMeterProvider]. */
internal class SdkMeterRegistryTest {
    private val testClock: TestClock = TestClock.create()
    private val sdkMeterReader = InMemoryMetricReader.create()
    private val meterProvider =
        SdkMeterProvider.builder()
            .setClock(testClock)
            .setResource(Resource.empty())
            .registerMetricReader(sdkMeterReader)
            .build()

    @Test
    fun builder_HappyPath() {
        SdkMeterProvider.builder()
            .setClock(testClock)
            .setResource(Resource.empty())
            .build()
            .shouldNotBeNull()
    }

    @Test
    fun defaultGet() {
        meterProvider["test"].shouldBeInstanceOf<SdkMeter>()
    }

    @Test
    fun sameInstanceForSameName_WithoutVersion() {
        meterProvider["test"] shouldBe meterProvider["test"]
        meterProvider["test"] shouldBe meterProvider.meterBuilder("test").build()
    }

    @Test
    fun sameInstanceForSameName_WithVersion() {
        meterProvider.meterBuilder("test").setInstrumentationVersion("version").build() shouldBe
            meterProvider.meterBuilder("test").setInstrumentationVersion("version").build()
    }

    @Test
    fun sameInstanceForSameName_WithVersionAndSchema() {
        meterProvider
            .meterBuilder("test")
            .setInstrumentationVersion("version")
            .setSchemaUrl("http://url")
            .build() shouldBe
            meterProvider
                .meterBuilder("test")
                .setInstrumentationVersion("version")
                .setSchemaUrl("http://url")
                .build()
    }

    @Test
    fun propagatesInstrumentationLibraryInfoToMeter() {
        val expected =
            InstrumentationLibraryInfo.create("theName", "theVersion", "http://theschema")
        val meter =
            meterProvider
                .meterBuilder(expected.name)
                .setInstrumentationVersion(expected.version!!)
                .setSchemaUrl(expected.schemaUrl!!)
                .build() as
                SdkMeter
        meter.instrumentationLibraryInfo shouldBe expected
    }

    @Test
    fun metricProducer_GetAllMetrics() {
        val sdkMeter1 = meterProvider["io.opentelemetry.kotlin.sdk.metrics.MeterSdkRegistryTest_1"]
        val longCounter1 = sdkMeter1.counterBuilder("testLongCounter").build()
        longCounter1.add(10, Attributes.empty())
        val sdkMeter2 = meterProvider["io.opentelemetry.kotlin.sdk.metrics.MeterSdkRegistryTest_2"]
        val longCounter2 = sdkMeter2.counterBuilder("testLongCounter").build()
        longCounter2.add(10, Attributes.empty())
        var result = sdkMeterReader.collectAllMetrics()
        result.forEach { metric ->
            assertSoftly(metric) {
                name shouldBe "testLongCounter"
                longSumData.shouldNotBeNull()
                longSumData.aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                longSumData.isMonotonic.shouldBeTrue()
                assertSoftly(longSumData.points.single()) {
                    startEpochNanos shouldBe testClock.now()
                    epochNanos shouldBe testClock.now()
                    attributes shouldBe Attributes.empty()
                    value shouldBe 10
                }
            }
        }
        result
            .map { it.instrumentationLibraryInfo }
            .shouldContainExactlyInAnyOrder(
                (sdkMeter1 as SdkMeter).instrumentationLibraryInfo,
                (sdkMeter2 as SdkMeter).instrumentationLibraryInfo
            )
    }

    @Test
    fun suppliesDefaultMeterForEmptyName() {
        var meter = meterProvider[""] as SdkMeter
        meter.instrumentationLibraryInfo.name shouldBe SdkMeterProvider.DEFAULT_METER_NAME
        meter = meterProvider.meterBuilder("").build() as SdkMeter
        meter.instrumentationLibraryInfo.name shouldBe SdkMeterProvider.DEFAULT_METER_NAME
    }
}
