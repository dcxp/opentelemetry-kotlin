/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarReservoir
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.resources.Resource
import kotlin.test.Test

/** Unit tests for [CountAggregator]. */
internal class CountAggregatorTest {
    @Test
    fun createHandle() {
        aggregator.createHandle().shouldBeInstanceOf<CountAggregator.Handle>()
    }

    @Test
    fun toPoint() {
        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.accumulateThenReset(Attributes.empty()).shouldBeNull()
    }

    @Test
    fun recordLongOperations() {
        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.recordLong(12)
        aggregatorHandle.recordLong(12)
        aggregatorHandle.accumulateThenReset(Attributes.empty())!!.value shouldBe 2
    }

    @Test
    fun recordDoubleOperations() {
        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.recordDouble(12.3)
        aggregatorHandle.recordDouble(12.3)
        aggregatorHandle.accumulateThenReset(Attributes.empty())!!.value shouldBe 2
    }

    @Test
    fun toMetricData_CumulativeTemporality() {
        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.recordLong(10)
        val metricData =
            aggregator.toMetricData(
                RESOURCE,
                LIBRARY,
                SIMPLE_METRIC_DESCRIPTOR,
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
            unit shouldBe "1"
            assertSoftly(longSumData) {
                aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                isMonotonic.shouldBeTrue()
                points.forEach { point ->
                    assertSoftly(point) {
                        startEpochNanos shouldBe 0
                        epochNanos shouldBe 100
                        attributes shouldBe Attributes.empty()
                        value shouldBe 1
                    }
                }
            }
        }
    }

    @Test
    fun toMetricData_DeltaTemporality() {
        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.recordLong(10)
        val metricData =
            aggregator.toMetricData(
                RESOURCE,
                LIBRARY,
                SIMPLE_METRIC_DESCRIPTOR,
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
            unit shouldBe "1"
            assertSoftly(longSumData) {
                aggregationTemporality shouldBe AggregationTemporality.DELTA
                isMonotonic.shouldBeTrue()
                points.forEach { point ->
                    assertSoftly(point) {
                        startEpochNanos shouldBe 10
                        epochNanos shouldBe 100
                        attributes shouldBe Attributes.empty()
                        value shouldBe 1
                    }
                }
            }
        }
    }

    companion object {
        private val RESOURCE: Resource = Resource.default
        private val LIBRARY = InstrumentationLibraryInfo.empty()
        private val SIMPLE_METRIC_DESCRIPTOR =
            MetricDescriptor.create("name", "description", "unit")
        private val aggregator = CountAggregator(ExemplarReservoir::noSamples)
    }
}
