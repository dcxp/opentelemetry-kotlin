/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentType
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentValueType
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.DoubleExemplarData
import io.opentelemetry.kotlin.sdk.metrics.data.ExemplarData
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarReservoir
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.metrics.mock.ExemplarReservoirMock
import io.opentelemetry.kotlin.sdk.resources.Resource
import kotlin.test.Test

/** Unit tests for [LongSumAggregator]. */
internal class LongSumAggregatorTest {
    var reservoir = ExemplarReservoirMock()

    @Test
    fun createHandle() {
        aggregator.createHandle().shouldBeInstanceOf<LongSumAggregator.Handle>()
    }

    @Test
    fun multipleRecords() {
        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.recordLong(12)
        aggregatorHandle.recordLong(12)
        aggregatorHandle.recordLong(12)
        aggregatorHandle.recordLong(12)
        aggregatorHandle.recordLong(12)
        aggregatorHandle.accumulateThenReset(Attributes.empty())!!.value shouldBe 12 * 5
        aggregatorHandle.accumulateThenReset(Attributes.empty()).shouldBeNull()
    }

    @Test
    fun multipleRecords_WithNegatives() {
        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.recordLong(12)
        aggregatorHandle.recordLong(12)
        aggregatorHandle.recordLong(-23)
        aggregatorHandle.recordLong(12)
        aggregatorHandle.recordLong(12)
        aggregatorHandle.recordLong(-11)
        aggregatorHandle.accumulateThenReset(Attributes.empty())!!.value shouldBe 14
        aggregatorHandle.accumulateThenReset(Attributes.empty()).shouldBeNull()
    }

    @Test
    fun toAccumulationAndReset() {
        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.accumulateThenReset(Attributes.empty()).shouldBeNull()
        aggregatorHandle.recordLong(13)
        aggregatorHandle.recordLong(12)
        aggregatorHandle.accumulateThenReset(Attributes.empty())!!.value shouldBe 25
        aggregatorHandle.accumulateThenReset(Attributes.empty()).shouldBeNull()
        aggregatorHandle.recordLong(12)
        aggregatorHandle.recordLong(-25)
        aggregatorHandle.accumulateThenReset(Attributes.empty())!!.value shouldBe -13
        aggregatorHandle.accumulateThenReset(Attributes.empty()).shouldBeNull()
    }

    @Test
    fun testExemplarsInAccumulation() {
        val attributes = Attributes.builder().put("test", "value").build()
        val exemplar: ExemplarData =
            DoubleExemplarData.create(attributes, 2L, "spanid", "traceid", 1.0)
        val exemplars = listOf(exemplar)
        reservoir = ExemplarReservoirMock(exemplars)
        val aggregator =
            LongSumAggregator(
                InstrumentDescriptor.create(
                    "instrument_name",
                    "instrument_description",
                    "instrument_unit",
                    InstrumentType.COUNTER,
                    InstrumentValueType.LONG
                )
            ) { reservoir }
        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.recordLong(0, attributes, Context.root())
        aggregatorHandle.accumulateThenReset(Attributes.empty()) shouldBe
            LongAccumulation.create(0, exemplars)
    }

    @Test
    fun mergeAndDiff() {
        val exemplar: ExemplarData =
            DoubleExemplarData.create(Attributes.empty(), 2L, "spanid", "traceid", 1.0)
        val exemplars = listOf(exemplar)
        for (instrumentType in InstrumentType.values()) {
            for (temporality in AggregationTemporality.values()) {
                val aggregator =
                    LongSumAggregator(
                        InstrumentDescriptor.create(
                            "name",
                            "description",
                            "unit",
                            instrumentType,
                            InstrumentValueType.LONG
                        ),
                        ExemplarReservoir::noSamples
                    )
                val merged =
                    aggregator.merge(
                        LongAccumulation.create(1L),
                        LongAccumulation.create(2L, exemplars)
                    )
                merged.value shouldBe 3
                merged.exemplars.shouldContainInOrder(exemplar)
                val diffed =
                    aggregator.diff(
                        LongAccumulation.create(1L),
                        LongAccumulation.create(2L, exemplars)
                    )
                diffed.value shouldBe 1
                diffed.exemplars.shouldContainInOrder(exemplar)
            }
        }
    }

    @Test
    fun toMetricData() {
        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.recordLong(10)
        val metricData =
            aggregator.toMetricData(
                resource,
                library,
                metricDescriptor,
                mapOf(
                    Attributes.empty() to aggregatorHandle.accumulateThenReset(Attributes.empty())!!
                ),
                AggregationTemporality.CUMULATIVE,
                0,
                10,
                100
            )
        assertSoftly(metricData) {
            resource shouldBe Resource.default
            instrumentationLibraryInfo shouldBe InstrumentationLibraryInfo.empty()
            name shouldBe "name"
            description shouldBe "description"
            unit shouldBe "unit"
            longSumData.shouldNotBeNull()
            assertSoftly(longSumData) {
                aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                isMonotonic.shouldBeTrue()
                points.forEach { point ->
                    assertSoftly(point) {
                        startEpochNanos shouldBe 0
                        epochNanos shouldBe 100
                        attributes shouldBe Attributes.empty()
                        value shouldBe 10
                    }
                }
            }
        }
    }

    @Test
    fun toMetricDataWithExemplars() {
        val attributes = Attributes.builder().put("test", "value").build()
        val exemplar: ExemplarData =
            DoubleExemplarData.create(attributes, 2L, "spanid", "traceid", 1.0)
        val accumulation = LongAccumulation.create(1, listOf(exemplar))

        assertSoftly(
            aggregator.toMetricData(
                resource,
                library,
                metricDescriptor,
                mapOf(Attributes.empty() to accumulation),
                AggregationTemporality.CUMULATIVE,
                0,
                10,
                100
            )
        ) {
            longSumData.shouldNotBeNull()
            assertSoftly(longSumData) {
                points.forEach { point ->
                    assertSoftly(point) {
                        value shouldBe 1
                        exemplars.shouldContainInOrder(exemplar)
                    }
                }
            }
        }
    }

    companion object {
        private val resource: Resource = Resource.default
        private val library = InstrumentationLibraryInfo.empty()
        private val metricDescriptor = MetricDescriptor.create("name", "description", "unit")
        private val aggregator =
            LongSumAggregator(
                InstrumentDescriptor.create(
                    "instrument_name",
                    "instrument_description",
                    "instrument_unit",
                    InstrumentType.COUNTER,
                    InstrumentValueType.LONG
                ),
                ExemplarReservoir::noSamples
            )
    }
}
