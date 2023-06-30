/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.kotest.matchers.types.shouldBeInstanceOf
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.Math.scalb
import io.opentelemetry.kotlin.sdk.metrics.data.ExemplarData
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarReservoir
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.metrics.mock.ExemplarReservoirMock
import io.opentelemetry.kotlin.sdk.resources.Resource
import kotlin.math.floor
import kotlin.math.ln
import kotlin.test.Test

class DoubleExponentialHistogramAggregatorTest {
    var reservoir = ExemplarReservoirMock()

    @Test
    fun createHandle() {
        aggregator.createHandle().shouldBeInstanceOf<DoubleExponentialHistogramAggregator.Handle>()
    }
    // TODO fix scaleB
    /*
        @Test
        fun testRecordings() {
            val aggregatorHandle = aggregator.createHandle()
            aggregatorHandle.recordDouble(0.5)
            aggregatorHandle.recordDouble(1.0)
            aggregatorHandle.recordDouble(12.0)
            aggregatorHandle.recordDouble(15.213)
            aggregatorHandle.recordDouble(12.0)
            aggregatorHandle.recordDouble(-13.2)
            aggregatorHandle.recordDouble(-2.01)
            aggregatorHandle.recordDouble(-1.0)
            aggregatorHandle.recordDouble(0.0)
            aggregatorHandle.recordLong(0)
            val acc = aggregatorHandle.accumulateThenReset(Attributes.empty())
            acc.shouldNotBeNull()
            val positiveCounts: List<Long> = acc.positiveBuckets.bucketCounts
            val negativeCounts = acc.negativeBuckets.bucketCounts
            val expectedScale = 6 // should be downscaled from 20 to 6 after recordings
            acc.scale shouldBe expectedScale
            acc.zeroCount shouldBe 2

            // Assert positive recordings are at correct index
            val posOffset = acc.positiveBuckets.offset
            acc.positiveBuckets.totalCount shouldBe 5
            positiveCounts[valueToIndex(expectedScale, 0.5) - posOffset] shouldBe 1
            (positiveCounts[valueToIndex(expectedScale, 1.0) - posOffset] shouldBe 1)
            (positiveCounts[valueToIndex(expectedScale, 12.0) - posOffset] shouldBe 2)
            (positiveCounts[valueToIndex(expectedScale, 15.213) - posOffset] shouldBe 1)

            // Assert negative recordings are at correct index
            val negOffset = acc.negativeBuckets.offset
            (acc.negativeBuckets.totalCount shouldBe 3)
            (negativeCounts[valueToIndex(expectedScale, 13.2) - negOffset] shouldBe 1)
            (negativeCounts[valueToIndex(expectedScale, 2.01) - negOffset] shouldBe 1)
            (negativeCounts[valueToIndex(expectedScale, 1.0) - negOffset] shouldBe 1)
        }

        @Test
        fun testInvalidRecording() {
            val aggregatorHandle = aggregator.createHandle()
            // Non finite recordings should be ignored
            aggregatorHandle.recordDouble(Double.POSITIVE_INFINITY)
            aggregatorHandle.recordDouble(Double.NEGATIVE_INFINITY)
            aggregatorHandle.recordDouble(Double.NaN)
            val acc = aggregatorHandle.accumulateThenReset(Attributes.empty())
            (acc!!.sum shouldBe 0)
            (acc.positiveBuckets.totalCount shouldBe 0)
            (acc.negativeBuckets.totalCount shouldBe 0)
            (acc.zeroCount shouldBe 0)
        }

        @Test
        fun testExemplarsInAccumulation() {

            val attributes = Attributes.builder().put("test", "value").build()
            val exemplar: ExemplarData =
                DoubleExemplarData.create(attributes, 2L, "spanid", "traceid", 1.0)
            val exemplars = listOf(exemplar)
            reservoir = ExemplarReservoirMock(exemplars)
            val agg = DoubleExponentialHistogramAggregator(Supplier<ExemplarReservoir> { reservoir })
            val aggregatorHandle = agg.createHandle()
            aggregatorHandle.recordDouble(0.0, attributes, Context.root())
            (aggregatorHandle.accumulateThenReset(Attributes.empty())!!.exemplars shouldBe exemplars)
        }

        @Test
        fun testAccumulationAndReset() {
            val aggregatorHandle = aggregator.createHandle()
            aggregatorHandle.accumulateThenReset(Attributes.empty()).shouldBeNull()
            aggregatorHandle.recordDouble(5.0)
            (aggregatorHandle.accumulateThenReset(Attributes.empty())!!
                .positiveBuckets
                .bucketCounts shouldBe listOf(1L))
            aggregatorHandle.accumulateThenReset(Attributes.empty()).shouldBeNull()
        }

        @Test
        fun testAccumulateData() {
            val acc = aggregator.accumulateDoubleMeasurement(1.2, Attributes.empty(), Context.current())
            val expected = getTestAccumulation(emptyList(), 1.2)
            (acc shouldBe expected)
        }

        @Test
        fun diffAccumulation() {
            val attributes = Attributes.builder().put("test", "value").build()
            val exemplar: ExemplarData =
                DoubleExemplarData.create(attributes, 2L, "spanid", "traceid", 1.0)
            val exemplars = listOf(exemplar)
            val previousExemplars: List<ExemplarData> =
                listOf(DoubleExemplarData.create(attributes, 1L, "spanId", "traceId", 2.0))
            val nextAccumulation = getTestAccumulation(exemplars, 0.0, 0.0, 1.0, 1.0, -1.0)
            val previousAccumulation = getTestAccumulation(previousExemplars, 0.0, 1.0, -1.0)

            // Assure most recent exemplars are kept
            (aggregator.diff(previousAccumulation, nextAccumulation) shouldBe
                getTestAccumulation(exemplars, 0.0, 1.0))
        }

        @Test
        fun diffDownScaledAccumulation() {
            val attributes = Attributes.builder().put("test", "value").build()
            val exemplar: ExemplarData =
                DoubleExemplarData.create(attributes, 2L, "spanid", "traceid", 1.0)
            val exemplars = listOf(exemplar)
            val previousExemplars: List<ExemplarData> =
                listOf(DoubleExemplarData.create(attributes, 1L, "spanId", "traceId", 2.0))
            val nextAccumulation = getTestAccumulation(exemplars, 1.0, 1.0, 100.0, -1.0, -100.0)
            val previousAccumulation = getTestAccumulation(previousExemplars, 1.0, -1.0)

            // Assure most recent exemplars are kept
            val diff = aggregator.diff(previousAccumulation, nextAccumulation)
            (diff shouldBe getTestAccumulation(exemplars, 1.0, 100.0, -100.0))
        }

        @Test
        fun testMergeAccumulation() {
            val attributes = Attributes.builder().put("test", "value").build()
            val exemplar: ExemplarData =
                DoubleExemplarData.create(attributes, 2L, "spanid", "traceid", 1.0)
            val exemplars = listOf(exemplar)
            val previousExemplars: List<ExemplarData> =
                listOf(DoubleExemplarData.create(attributes, 1L, "spanId", "traceId", 2.0))
            val previousAccumulation =
                getTestAccumulation(previousExemplars, 0.0, 4.1, 100.0, 100.0, 10000.0, 1000000.0)
            val nextAccumulation = getTestAccumulation(exemplars, -1000.0, -2000000.0, -8.2, 2.3)

            // Merged accumulations should equal accumulation with equivalent recordings and latest
            // exemplars.
            (aggregator.merge(previousAccumulation, nextAccumulation) shouldBe
                getTestAccumulation(
                    exemplars,
                    0.0,
                    4.1,
                    100.0,
                    100.0,
                    10000.0,
                    1000000.0,
                    -1000.0,
                    -2000000.0,
                    -8.2,
                    2.3
                ))
        }

        @Test
        fun testMergeNonOverlap() {
            val previousAccumulation =
                getTestAccumulation(emptyList(), 10.0, 100.0, 100.0, 10000.0, 100000.0)
            val nextAccumulation = getTestAccumulation(emptyList(), 0.001, 0.01, 0.1, 1.0)
            (aggregator.merge(previousAccumulation, nextAccumulation) shouldBe
                getTestAccumulation(
                    emptyList(),
                    0.001,
                    0.01,
                    0.1,
                    1.0,
                    10.0,
                    100.0,
                    100.0,
                    10000.0,
                    100000.0
                ))
        }

        @Test
        fun testMergeWithEmptyBuckets() {
            (aggregator.merge(
                getTestAccumulation(emptyList()),
                getTestAccumulation(emptyList(), 1.0)
            ) shouldBe getTestAccumulation(emptyList(), 1.0))
            (aggregator.merge(
                getTestAccumulation(emptyList(), 1.0),
                getTestAccumulation(emptyList())
            ) shouldBe getTestAccumulation(emptyList(), 1.0))
            (aggregator.merge(
                getTestAccumulation(emptyList()),
                getTestAccumulation(emptyList())
            ) shouldBe getTestAccumulation(emptyList()))
        }

        @Test
        fun testMergeOverlap() {
            val previousAccumulation =
                getTestAccumulation(emptyList(), 0.0, 10.0, 100.0, 10000.0, 100000.0)
            val nextAccumulation = getTestAccumulation(emptyList(), 100000.0, 10000.0, 100.0, 10.0, 0.0)
            (aggregator.merge(previousAccumulation, nextAccumulation) shouldBe
                getTestAccumulation(
                    emptyList(),
                    0.0,
                    0.0,
                    10.0,
                    10.0,
                    100.0,
                    100.0,
                    10000.0,
                    10000.0,
                    100000.0,
                    100000.0
                ))
        }

        @Test
        fun testInsert1M() {
            val handle = aggregator.createHandle()
            val min = 1.0 / (1 shl 16)
            val n = 1024 * 1024 - 1
            var d = min
            for (i in 0 until n) {
                handle.recordDouble(d)
                d += min
            }
            val acc = handle.accumulateThenReset(Attributes.empty())
            (acc!!.scale shouldBe 4)
            (acc.positiveBuckets.bucketCounts.size shouldBe 320)
        }

        @Test
        fun testDownScale() {
            val handle = aggregator.createHandle() as DoubleExponentialHistogramAggregator.Handle
            handle.downScale(20) // down to zero scale

            // test histogram operates properly after being manually scaled down to 0
            handle.recordDouble(0.5)
            handle.recordDouble(1.0)
            handle.recordDouble(2.0)
            handle.recordDouble(4.0)
            handle.recordDouble(16.0)
            val acc = handle.accumulateThenReset(Attributes.empty())
            (acc!!.scale shouldBe 0)
            val buckets: ExponentialHistogramBuckets = acc.positiveBuckets
            (acc.sum shouldBe 23.5)
            (buckets.offset shouldBe -1)
            (buckets.bucketCounts shouldBe listOf(1L, 1L, 1L, 1L, 0L, 1L))
        }

        @Test
        fun testToMetricData() {
            val attributes = Attributes.builder().put("test", "value").build()
            val exemplar: ExemplarData =
                DoubleExemplarData.create(attributes, 2L, "spanid", "traceid", 1.0)
            val reservoirSupplier: Supplier<ExemplarReservoir> =
                Supplier<ExemplarReservoir> { ExemplarReservoirMock(exemplar) }
            val cumulativeAggregator = DoubleExponentialHistogramAggregator(reservoirSupplier)
            val aggregatorHandle = cumulativeAggregator.createHandle()
            aggregatorHandle.recordDouble(0.0)
            aggregatorHandle.recordDouble(0.0)
            aggregatorHandle.recordDouble(123.456)
            val acc = aggregatorHandle.accumulateThenReset(Attributes.empty())
            val metricDataCumulative =
                cumulativeAggregator.toMetricData(
                    RESOURCE,
                    INSTRUMENTATION_LIBRARY_INFO,
                    METRIC_DESCRIPTOR,
                    mapOf(Attributes.empty() to acc!!),
                    AggregationTemporality.CUMULATIVE,
                    0,
                    10,
                    100
                )

            // Assertions run twice to verify immutability; recordings shouldn't modify the metric data
            for (i in 0..1) {
                assertSoftly(metricDataCumulative) {
                    exponentialHistogramData.shouldNotBeNull()
                    assertSoftly(exponentialHistogramData) {
                        aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                        points.forEach { point ->
                            assertSoftly(point) {
                                sum shouldBe 123.456
                                scale shouldBe 20
                                zeroCount shouldBe 2
                                // totalCount shouldBe 3
                                this.exemplars shouldContain exemplar
                                assertSoftly(positiveBuckets!!) {
                                    bucketCounts shouldContainInOrder listOf(1L)
                                    offset shouldBe valueToIndex(20, 123.456)
                                    totalCount shouldBe 1
                                }
                                assertSoftly(negativeBuckets!!) {
                                    bucketCounts shouldContainInOrder listOf()
                                    totalCount shouldBe 0
                                }
                            }
                        }
                    }
                }
                aggregatorHandle.recordDouble(1.0)
                aggregatorHandle.recordDouble(-1.0)
                aggregatorHandle.recordDouble(0.0)
            }
            val metricDataDelta =
                cumulativeAggregator.toMetricData(
                    RESOURCE,
                    INSTRUMENTATION_LIBRARY_INFO,
                    METRIC_DESCRIPTOR,
                    mapOf(Attributes.empty() to acc),
                    AggregationTemporality.DELTA,
                    0,
                    10,
                    100
                )
            (metricDataDelta.exponentialHistogramData.aggregationTemporality shouldBe
                AggregationTemporality.DELTA)
        }

        @Test
        fun testMultithreadedUpdates() = runTest( timeout = 200.seconds ) {
            val aggregatorHandle = aggregator.createHandle()
            val summarizer = ExponentialHistogram()
            val updates: List<Double> = listOf(0.0, 0.1, -0.1, 1.0, -1.0, 100.0)
            val numberOfUpdates = 10000
            val threads =
                updates.map { v ->
                    CoroutineScope(Dispatchers.Unconfined).launch {
                        for (j in 0 until numberOfUpdates) {
                            aggregatorHandle.recordDouble(v)
                            if (Random.nextInt(10) == 0) {
                                summarizer.process(
                                    aggregatorHandle.accumulateThenReset(Attributes.empty())
                                )
                            }
                        }
                    }
                }
            threads.forEach { it.start() }
            threads.forEach { it.join() }
            // make sure everything gets merged when all the aggregation is done.
            summarizer.process(aggregatorHandle.accumulateThenReset(Attributes.empty()))
            val acc: ExponentialHistogramAccumulation = summarizer.accumulation!!
            (acc.zeroCount shouldBe numberOfUpdates)
            (acc.sum shouldBe 100.0 * 10000) // float error
            (acc.scale shouldBe 5)
            (acc.positiveBuckets).totalCount shouldBe numberOfUpdates * 3
            (acc.positiveBuckets).offset shouldBe -107
            (acc.negativeBuckets).totalCount shouldBe numberOfUpdates * 2
            (acc.negativeBuckets).offset shouldBe -107

            // Verify positive buckets have correct counts
            val posCounts = acc.positiveBuckets.bucketCounts
            (posCounts[valueToIndex(acc.scale, 0.1) - acc.positiveBuckets.offset] shouldBe
                numberOfUpdates)
            (posCounts[valueToIndex(acc.scale, 1.0) - acc.positiveBuckets.offset] shouldBe
                numberOfUpdates)
            (posCounts[valueToIndex(acc.scale, 100.0) - acc.positiveBuckets.offset] shouldBe
                numberOfUpdates)

            // Verify negative buckets have correct counts
            val negCounts = acc.negativeBuckets.bucketCounts
            (negCounts[valueToIndex(acc.scale, 0.1) - acc.positiveBuckets.offset] shouldBe
                numberOfUpdates)
            (negCounts[valueToIndex(acc.scale, 1.0) - acc.positiveBuckets.offset] shouldBe
                numberOfUpdates)
        }
    */
    private class ExponentialHistogram {
        var accumulation: ExponentialHistogramAccumulation? = null

        fun process(other: ExponentialHistogramAccumulation?) {
            if (other == null) {
                return
            }
            if (accumulation == null) {
                accumulation = other
                return
            }
            accumulation = aggregator.merge(accumulation!!, other)
        }
    }

    companion object {
        private val aggregator = DoubleExponentialHistogramAggregator(ExemplarReservoir::noSamples)
        private val RESOURCE: Resource = Resource.default
        private val INSTRUMENTATION_LIBRARY_INFO = InstrumentationLibraryInfo.empty()
        private val METRIC_DESCRIPTOR = MetricDescriptor.create("name", "description", "unit")
        private fun valueToIndex(scale: Int, value: Double): Int {
            val scaleFactor: Double = scalb(1.0 / ln(2.0), scale)
            return floor(ln(value) * scaleFactor).toInt()
        }

        private fun getTestAccumulation(
            exemplars: List<ExemplarData>,
            vararg recordings: Double
        ): ExponentialHistogramAccumulation {
            val aggregatorHandle = aggregator.createHandle()
            for (r in recordings) {
                aggregatorHandle.recordDouble(r)
            }
            return aggregatorHandle.doAccumulateThenReset(exemplars)
        }
    }
}
