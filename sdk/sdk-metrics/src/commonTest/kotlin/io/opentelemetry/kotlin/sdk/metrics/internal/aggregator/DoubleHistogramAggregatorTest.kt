/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.DoubleExemplarData
import io.opentelemetry.kotlin.sdk.metrics.data.ExemplarData
import io.opentelemetry.kotlin.sdk.metrics.data.MetricDataType
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarReservoir
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.metrics.mock.ExemplarReservoirMock
import io.opentelemetry.kotlin.sdk.resources.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class DoubleHistogramAggregatorTest {
    var reservoir = ExemplarReservoirMock()
    @Test
    fun createHandle() {
        aggregator.createHandle().shouldBeInstanceOf<DoubleHistogramAggregator.Handle>()
    }

    @Test
    fun testRecordings() {
        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.recordLong(20)
        aggregatorHandle.recordLong(5)
        aggregatorHandle.recordLong(150)
        aggregatorHandle.recordLong(2000)
        aggregatorHandle.accumulateThenReset(Attributes.empty()) shouldBe
            HistogramAccumulation.create(2175.0, longArrayOf(1, 1, 1, 1))
    }

    @Test
    fun testExemplarsInAccumulation() {
        val attributes = Attributes.builder().put("test", "value").build()
        val exemplar: ExemplarData =
            DoubleExemplarData.create(attributes, 2L, "spanid", "traceid", 1.0)
        val exemplars = listOf(exemplar)
        reservoir = ExemplarReservoirMock(exemplars)
        val aggregator = DoubleHistogramAggregator(boundaries) { reservoir }
        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.recordDouble(0.0, attributes, Context.root())
        aggregatorHandle.accumulateThenReset(Attributes.empty()) shouldBe
            HistogramAccumulation.create(0.0, longArrayOf(1, 0, 0, 0), exemplars)
    }

    @Test
    fun toAccumulationAndReset() {
        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.accumulateThenReset(Attributes.empty()).shouldBeNull()
        aggregatorHandle.recordLong(100)
        aggregatorHandle.accumulateThenReset(Attributes.empty()) shouldBe
            HistogramAccumulation.create(100.0, longArrayOf(0, 1, 0, 0))
        aggregatorHandle.accumulateThenReset(Attributes.empty()).shouldBeNull()
        aggregatorHandle.recordLong(0)
        aggregatorHandle.accumulateThenReset(Attributes.empty()) shouldBe
            HistogramAccumulation.create(0.0, longArrayOf(1, 0, 0, 0))
        aggregatorHandle.accumulateThenReset(Attributes.empty()).shouldBeNull()
    }

    @Test
    fun accumulateData() {
        aggregator.accumulateDoubleMeasurement(11.1, Attributes.empty(), Context.current()) shouldBe
            HistogramAccumulation.create(11.1, longArrayOf(0, 1, 0, 0))
        aggregator.accumulateLongMeasurement(10, Attributes.empty(), Context.current()) shouldBe
            HistogramAccumulation.create(10.0, longArrayOf(1, 0, 0, 0))
    }

    @Test
    fun mergeAccumulation() {
        val attributes = Attributes.builder().put("test", "value").build()
        val exemplar: ExemplarData =
            DoubleExemplarData.create(attributes, 2L, "spanid", "traceid", 1.0)
        val exemplars = listOf(exemplar)
        val previousExemplars: List<ExemplarData> =
            listOf(DoubleExemplarData.create(attributes, 1L, "spanId", "traceId", 2.0))
        val previousAccumulation =
            HistogramAccumulation.create(2.0, longArrayOf(1, 1, 0), previousExemplars)
        val nextAccumulation = HistogramAccumulation.create(2.0, longArrayOf(0, 0, 2), exemplars)
        // Assure most recent exemplars are kept.
        aggregator.merge(previousAccumulation, nextAccumulation) shouldBe
            HistogramAccumulation.create(4.0, longArrayOf(1, 1, 2), exemplars)
    }

    @Test
    fun diffAccumulation() {
        val attributes = Attributes.builder().put("test", "value").build()
        val exemplar: ExemplarData =
            DoubleExemplarData.create(attributes, 2L, "spanid", "traceid", 1.0)
        val exemplars = listOf(exemplar)
        val previousExemplars: List<ExemplarData> =
            listOf(DoubleExemplarData.create(attributes, 1L, "spanId", "traceId", 2.0))
        val previousAccumulation =
            HistogramAccumulation.create(2.0, longArrayOf(1, 1, 2), previousExemplars)
        val nextAccumulation = HistogramAccumulation.create(5.0, longArrayOf(2, 2, 2), exemplars)
        // Assure most recent exemplars are kept.
        aggregator.diff(previousAccumulation, nextAccumulation) shouldBe
            HistogramAccumulation.create(3.0, longArrayOf(1, 1, 0), exemplars)
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
                AggregationTemporality.DELTA,
                0,
                10,
                100
            )
        metricData.shouldNotBeNull()
        metricData.type shouldBe MetricDataType.HISTOGRAM
        metricData.doubleHistogramData.aggregationTemporality shouldBe AggregationTemporality.DELTA
    }

    @Test
    fun toMetricDataWithExemplars() {
        val attributes = Attributes.builder().put("test", "value").build()
        val exemplar: ExemplarData =
            DoubleExemplarData.create(attributes, 2L, "spanid", "traceid", 1.0)
        val accumulation =
            HistogramAccumulation.create(2.0, longArrayOf(1, 0, 0, 0), listOf(exemplar))

        assertSoftly(
            aggregator.toMetricData(
                RESOURCE,
                INSTRUMENTATION_LIBRARY_INFO,
                METRIC_DESCRIPTOR,
                mapOf(Attributes.empty() to accumulation),
                AggregationTemporality.CUMULATIVE,
                0,
                10,
                100
            )
        ) {
            doubleHistogramData.shouldNotBeNull()
            assertSoftly(doubleHistogramData) {
                points.forEach { point ->
                    assertSoftly(point) {
                        sum shouldBe 2
                        counts.shouldContainInOrder(1, 0, 0, 0)
                        count shouldBe 1
                        exemplars shouldContain exemplar
                    }
                }
            }
        }
    }

    @Test
    fun testHistogramCounts() {
        aggregator.accumulateDoubleMeasurement(1.1, Attributes.empty(), Context.root())!!
            .counts
            .size shouldBe boundaries.size + 1

        aggregator.accumulateLongMeasurement(1, Attributes.empty(), Context.root())!!
            .counts
            .size shouldBe boundaries.size + 1

        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.recordDouble(1.1)
        val histogramAccumulation = aggregatorHandle.accumulateThenReset(Attributes.empty())
        histogramAccumulation.shouldNotBeNull()
        histogramAccumulation.counts.size shouldBe boundaries.size + 1
    }

    @Test
    fun testMultithreadedUpdates() = runTest( timeout = 200.seconds ) {
        val aggregatorHandle = aggregator.createHandle()
        val summarizer = Histogram()
        val updates: List<Long> = listOf(1L, 2L, 3L, 5L, 7L, 11L, 13L, 17L, 19L, 23L)
        val numberOfThreads: Int = updates.size
        val numberOfUpdates = 10000
        val threads =
            updates.map { v ->
                CoroutineScope(Dispatchers.Unconfined).launch {
                    for (j in 0 until numberOfUpdates) {
                        aggregatorHandle.recordLong(v)
                        if (Random.nextInt(10) == 0) {
                            summarizer.process(
                                aggregatorHandle.accumulateThenReset(Attributes.empty())!!
                            )
                        }
                    }
                }
            }
        threads.forEach { it.start() }
        threads.forEach { it.join() }

        // make sure everything gets merged when all the aggregation is done.
        val result = aggregatorHandle.accumulateThenReset(Attributes.empty())
        result?.let { summarizer.process(it) }
        summarizer.accumulation shouldBe
            HistogramAccumulation.create(1010000.0, longArrayOf(50000, 50000, 0, 0))
    }

    private class Histogram {
        var accumulation: HistogramAccumulation? = null
        fun process(other: HistogramAccumulation) {
            if (accumulation == null) {
                accumulation = other
                return
            }
            accumulation = aggregator.merge(accumulation!!, other)
        }
    }

    companion object {
        private val boundaries = doubleArrayOf(10.0, 100.0, 1000.0)
        private val RESOURCE: Resource = Resource.default
        private val INSTRUMENTATION_LIBRARY_INFO = InstrumentationLibraryInfo.empty()
        private val METRIC_DESCRIPTOR = MetricDescriptor.create("name", "description", "unit")
        private val aggregator = DoubleHistogramAggregator(boundaries, ExemplarReservoir::noSamples)
    }
}
