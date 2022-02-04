/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.ExemplarData
import io.opentelemetry.kotlin.sdk.metrics.data.LongExemplarData
import io.opentelemetry.kotlin.sdk.metrics.data.LongGaugeData
import io.opentelemetry.kotlin.sdk.metrics.data.LongPointData
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarReservoir
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.resources.Resource
import kotlin.test.Test

/** Unit tests for [LongLastValueAggregator]. */
internal class LongLastValueAggregatorTest {
    @Test
    fun createHandle() {
        aggregator.createHandle().shouldBeInstanceOf<LongLastValueAggregator.Handle>()
    }
    @Test
    fun multipleRecords() {
        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.recordLong(12)
        aggregatorHandle.accumulateThenReset(Attributes.empty())!!.value shouldBe 12L
        aggregatorHandle.recordLong(13)
        aggregatorHandle.recordLong(14)
        aggregatorHandle.accumulateThenReset(Attributes.empty())!!.value shouldBe 14L
    }

    @Test
    fun toAccumulationAndReset() {
        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.accumulateThenReset(Attributes.empty()).shouldBeNull()
        aggregatorHandle.recordLong(13)
        aggregatorHandle.accumulateThenReset(Attributes.empty())!!.value shouldBe 13L
        aggregatorHandle.accumulateThenReset(Attributes.empty()).shouldBeNull()
        aggregatorHandle.recordLong(12)
        aggregatorHandle.accumulateThenReset(Attributes.empty())!!.value shouldBe 12L
        aggregatorHandle.accumulateThenReset(Attributes.empty()).shouldBeNull()
    }

    @Test
    fun mergeAccumulation() {
        val attributes = Attributes.builder().put("test", "value").build()
        val exemplar: ExemplarData = LongExemplarData.create(attributes, 2L, "spanid", "traceid", 1)
        val exemplars = listOf(exemplar)
        val previousExemplars: List<ExemplarData> =
            listOf(LongExemplarData.create(attributes, 1L, "spanId", "traceId", 2))
        val result =
            aggregator.merge(
                LongAccumulation.create(1, previousExemplars),
                LongAccumulation.create(2, exemplars)
            )
        // Assert that latest measurement is kept.
        result shouldBe LongAccumulation.create(2, exemplars)
    }

    @Test
    fun toMetricData() {
        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.recordLong(10)
        val metricData =
            aggregator.toMetricData(
                RESOURCE,
                INSTRUMENTATION_LIBRARY_INFO,
                METRIC_DESCRIPTOR,
                mapOf(
                    Attributes.empty() to aggregatorHandle.accumulateThenReset(Attributes.empty())!!
                ),
                AggregationTemporality.CUMULATIVE,
                2,
                10,
                100
            )
        metricData shouldBe
            MetricData.createLongGauge(
                Resource.default,
                InstrumentationLibraryInfo.empty(),
                "name",
                "description",
                "unit",
                LongGaugeData.create(listOf(LongPointData.create(2, 100, Attributes.empty(), 10)))
            )
    }

    companion object {
        private val RESOURCE: Resource = Resource.default
        private val INSTRUMENTATION_LIBRARY_INFO = InstrumentationLibraryInfo.empty()
        private val METRIC_DESCRIPTOR = MetricDescriptor.create("name", "description", "unit")
        private val aggregator = LongLastValueAggregator(ExemplarReservoir::noSamples)
    }
}
