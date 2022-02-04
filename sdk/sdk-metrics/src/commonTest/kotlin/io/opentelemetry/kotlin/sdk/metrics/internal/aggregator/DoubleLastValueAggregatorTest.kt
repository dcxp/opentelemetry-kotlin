/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.DoubleExemplarData
import io.opentelemetry.kotlin.sdk.metrics.data.ExemplarData
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarReservoir
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.resources.Resource
import kotlin.test.Test

/** Unit tests for [AggregatorHandle]. */
internal class DoubleLastValueAggregatorTest {
    @Test
    fun createHandle() {
        aggregator.createHandle().shouldBeInstanceOf<DoubleLastValueAggregator.Handle>()
    }

    @Test
    fun multipleRecords() {
        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.recordDouble(12.1)
        aggregatorHandle.accumulateThenReset(Attributes.empty())!!.value shouldBe 12.1
        aggregatorHandle.recordDouble(13.1)
        aggregatorHandle.recordDouble(14.1)
        aggregatorHandle.accumulateThenReset(Attributes.empty())!!.value shouldBe 14.1
    }

    @Test
    fun toAccumulationAndReset() {
        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.accumulateThenReset(Attributes.empty()).shouldBeNull()
        aggregatorHandle.recordDouble(13.1)
        aggregatorHandle.accumulateThenReset(Attributes.empty())!!.value shouldBe 13.1
        aggregatorHandle.accumulateThenReset(Attributes.empty()).shouldBeNull()
        aggregatorHandle.recordDouble(12.1)
        aggregatorHandle.accumulateThenReset(Attributes.empty())!!.value shouldBe 12.1
        aggregatorHandle.accumulateThenReset(Attributes.empty()).shouldBeNull()
    }

    @Test
    fun mergeAccumulation() {
        val attributes = Attributes.builder().put("test", "value").build()
        val exemplar: ExemplarData =
            DoubleExemplarData.create(attributes, 2L, "spanid", "traceid", 1.0)
        val exemplars = listOf(exemplar)
        val previousExemplars: List<ExemplarData> =
            listOf(DoubleExemplarData.create(attributes, 1L, "spanId", "traceId", 2.0))
        val result =
            aggregator.merge(
                DoubleAccumulation.create(1.0, previousExemplars),
                DoubleAccumulation.create(2.0, exemplars)
            )
        // Assert that latest measurement is kept.
        result shouldBe DoubleAccumulation.create(2.0, exemplars)
    }

    @Test
    fun diffAccumulation() {
        val attributes = Attributes.builder().put("test", "value").build()
        val exemplar: ExemplarData =
            DoubleExemplarData.create(attributes, 2L, "spanid", "traceid", 1.0)
        val exemplars = listOf(exemplar)
        val previousExemplars: List<ExemplarData> =
            listOf(DoubleExemplarData.create(attributes, 1L, "spanId", "traceId", 2.0))
        val result =
            aggregator.diff(
                DoubleAccumulation.create(1.0, previousExemplars),
                DoubleAccumulation.create(2.0, exemplars)
            )
        // Assert that latest measurement is kept.
        result shouldBe DoubleAccumulation.create(2.0, exemplars)
    }

    @Test
    fun toMetricData() {
        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.recordDouble(10.0)
        val metricData =
            aggregator.toMetricData(
                RESOURCE,
                INSTRUMENTATION_LIBRARY_INFO,
                METRIC_DESCRIPTOR,
                mapOf(
                    Attributes.empty() to aggregatorHandle.accumulateThenReset(Attributes.empty())!!
                ),
                AggregationTemporality.DELTA,
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
            doubleGaugeData.shouldNotBeNull()
            assertSoftly(doubleGaugeData) {
                points.forEach { point ->
                    assertSoftly(point) {
                        startEpochNanos shouldBe 10
                        epochNanos shouldBe 100
                        attributes shouldBe Attributes.empty()
                        value shouldBe 10
                    }
                }
            }
        }
    }

    companion object {
        private val RESOURCE: Resource = Resource.default
        private val INSTRUMENTATION_LIBRARY_INFO = InstrumentationLibraryInfo.empty()
        private val METRIC_DESCRIPTOR = MetricDescriptor.create("name", "description", "unit")
        private val aggregator = DoubleLastValueAggregator(ExemplarReservoir::noSamples)
    }
}
