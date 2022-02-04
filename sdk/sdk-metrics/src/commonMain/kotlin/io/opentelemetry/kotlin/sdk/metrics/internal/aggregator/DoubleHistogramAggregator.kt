/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.opentelemetry.kotlin.Supplier
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.DoubleHistogramData
import io.opentelemetry.kotlin.sdk.metrics.data.ExemplarData
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarReservoir
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.resources.Resource
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.reentrantLock

/**
 * Aggregator that generates histograms.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
class DoubleHistogramAggregator(
    private val boundaries: DoubleArray,
    private val reservoirSupplier: Supplier<ExemplarReservoir>
) : Aggregator<HistogramAccumulation> {
    // a cache for converting to MetricData
    private val boundaryList: List<Double> = boundaries.toList()

    override fun createHandle(): AggregatorHandle<HistogramAccumulation> {
        return Handle(boundaries, reservoirSupplier.get())
    }

    /**
     * Return the result of the merge of two histogram accumulations. As long as one Aggregator
     * instance produces all Accumulations with constant boundaries we don't need to worry about
     * merging accumulations with different boundaries.
     */
    override fun merge(
        previous: HistogramAccumulation,
        current: HistogramAccumulation
    ): HistogramAccumulation {
        val previousCounts: LongArray = previous.counts
        val mergedCounts = LongArray(previousCounts.size)
        for (i in previousCounts.indices) {
            mergedCounts[i] = previousCounts[i] + current.counts.get(i)
        }
        return HistogramAccumulation.Companion.create(
            previous.sum + current.sum,
            mergedCounts,
            current.exemplars
        )
    }

    override fun diff(
        previous: HistogramAccumulation,
        current: HistogramAccumulation
    ): HistogramAccumulation {
        val previousCounts: LongArray = previous.counts
        val diffedCounts = LongArray(previousCounts.size)
        for (i in previousCounts.indices) {
            diffedCounts[i] = current.counts.get(i) - previousCounts[i]
        }
        return HistogramAccumulation.Companion.create(
            current.sum - previous.sum,
            diffedCounts,
            current.exemplars
        )
    }

    override fun toMetricData(
        resource: Resource,
        instrumentationLibraryInfo: InstrumentationLibraryInfo,
        metricDescriptor: MetricDescriptor,
        accumulationByLabels: Map<Attributes, HistogramAccumulation>,
        temporality: AggregationTemporality,
        startEpochNanos: Long,
        lastCollectionEpoch: Long,
        epochNanos: Long
    ): MetricData {
        return MetricData.createDoubleHistogram(
            resource,
            instrumentationLibraryInfo,
            metricDescriptor.name,
            metricDescriptor.description,
            metricDescriptor.unit,
            DoubleHistogramData.create(
                temporality,
                MetricDataUtils.toDoubleHistogramPointList(
                    accumulationByLabels,
                    if (temporality === AggregationTemporality.CUMULATIVE) startEpochNanos
                    else lastCollectionEpoch,
                    epochNanos,
                    boundaryList
                )
            )
        )
    }

    internal class Handle( // read-only
    private val boundaries: DoubleArray, reservoir: ExemplarReservoir) :
        AggregatorHandle<HistogramAccumulation>(reservoir) {
        private val sum = atomic(0.0)
        private val counts: AtomicRef<LongArray> = atomic(LongArray(boundaries.size + 1))
        private val lock = reentrantLock()

        override fun doAccumulateThenReset(exemplars: List<ExemplarData>): HistogramAccumulation {
            lock.lock()
            return try {
                val acc: HistogramAccumulation =
                    HistogramAccumulation.create(sum.value, counts.value.copyOf(), exemplars)
                sum.value = 0.0
                counts.value.fill(0)
                acc
            } finally {
                lock.unlock()
            }
        }

        override fun doRecordDouble(value: Double) {
            val bucketIndex: Int = ExplicitBucketHistogramUtils.findBucketIndex(boundaries, value)
            lock.lock()
            try {
                val oldValue = sum.value
                sum.lazySet(oldValue + value)
                counts.value[bucketIndex]++
            } finally {
                lock.unlock()
            }
        }

        override fun doRecordLong(value: Long) {
            doRecordDouble(value.toDouble())
        }

        private data class SumWrapper(val sum: Double = 0.0)
    }
}
