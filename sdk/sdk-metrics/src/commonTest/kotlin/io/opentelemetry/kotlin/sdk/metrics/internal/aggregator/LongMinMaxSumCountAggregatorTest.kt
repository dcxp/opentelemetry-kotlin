/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.MetricDataType
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarReservoir
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.resources.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

internal class LongMinMaxSumCountAggregatorTest {
    @Test
    fun createHandle() {
        aggregator.createHandle().shouldBeInstanceOf<LongMinMaxSumCountAggregator.Handle>()
    }
    @Test
    fun testRecordings() {
        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.recordLong(100)
        aggregatorHandle.accumulateThenReset(Attributes.empty()) shouldBe
            MinMaxSumCountAccumulation.create(1, 100.0, 100.0, 100.0)
        aggregatorHandle.recordLong(200)
        aggregatorHandle.accumulateThenReset(Attributes.empty()) shouldBe
            MinMaxSumCountAccumulation.create(1, 200.0, 200.0, 200.0)
        aggregatorHandle.recordLong(-75)
        aggregatorHandle.accumulateThenReset(Attributes.empty()) shouldBe
            MinMaxSumCountAccumulation.create(1, -75.0, -75.0, -75.0)
    }

    @Test
    fun toAccumulationAndReset() {
        val aggregatorHandle = aggregator.createHandle()
        aggregatorHandle.accumulateThenReset(Attributes.empty()).shouldBeNull()
        aggregatorHandle.recordLong(100)
        aggregatorHandle.accumulateThenReset(Attributes.empty()) shouldBe
            MinMaxSumCountAccumulation.create(1, 100.0, 100.0, 100.0)
        aggregatorHandle.accumulateThenReset(Attributes.empty()).shouldBeNull()
        aggregatorHandle.recordLong(100)
        aggregatorHandle.accumulateThenReset(Attributes.empty()) shouldBe
            MinMaxSumCountAccumulation.create(1, 100.0, 100.0, 100.0)
        aggregatorHandle.accumulateThenReset(Attributes.empty()).shouldBeNull()
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
                0,
                10,
                100
            )
        metricData.shouldNotBeNull()
        metricData.type shouldBe MetricDataType.SUMMARY
    }

    @Test
    fun testMultithreadedUpdates() = runTest( timeout = 200.seconds ) {
        val aggregatorHandle = aggregator.createHandle()
        val summarizer = Summary()
        val numberOfThreads = 10
        val updates = longArrayOf(1, 2, 3, 5, 7, 11, 13, 17, 19, 23)
        val numberOfUpdates = 1000
        val workers =
            (0 until numberOfThreads).map { v ->
                CoroutineScope(Dispatchers.Unconfined).launch {
                    val update = updates[v]
                    for (j in 0 until numberOfUpdates) {
                        aggregatorHandle.recordLong(update)
                        if (Random.nextInt(10) == 0) {
                            summarizer.process(
                                aggregatorHandle.accumulateThenReset(Attributes.empty())!!
                            )
                        }
                    }
                }
            }
        workers.forEach { it.start() }
        workers.forEach { it.join() }
        // make sure everything gets merged when all the aggregation is done.
        val lastResult = aggregatorHandle.accumulateThenReset(Attributes.empty())
        lastResult?.let { summarizer.process(it) }
        summarizer.accumulation shouldBe
            MinMaxSumCountAccumulation.create(
                (numberOfThreads * numberOfUpdates).toLong(),
                101000.0,
                1.0,
                23.0
            )
    }

    private class Summary {
        var accumulation: MinMaxSumCountAccumulation? = null
        fun process(other: MinMaxSumCountAccumulation) {
            if (accumulation == null) {
                accumulation = other
                return
            }
            accumulation = aggregator.merge(accumulation!!, other)
        }
    }

    companion object {
        private val RESOURCE: Resource = Resource.default
        private val INSTRUMENTATION_LIBRARY_INFO = InstrumentationLibraryInfo.empty()
        private val METRIC_DESCRIPTOR = MetricDescriptor.create("name", "description", "unit")
        private val aggregator = LongMinMaxSumCountAggregator(ExemplarReservoir::noSamples)
    }
}
