/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.opentelemetry.kotlin.Supplier
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.ExemplarData
import io.opentelemetry.kotlin.sdk.metrics.data.ExponentialHistogramData
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarReservoir
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.resources.Resource
import kotlin.math.min

internal class DoubleExponentialHistogramAggregator
constructor(private val reservoirSupplier: Supplier<ExemplarReservoir>) :
    Aggregator<ExponentialHistogramAccumulation> {

    override fun createHandle(): AggregatorHandle<ExponentialHistogramAccumulation> {
        return Handle(reservoirSupplier.get())
    }

    /**
     * This function is an immutable merge. It firstly combines the sum and zero count. Then it
     * performs a merge using the buckets from both accumulations, without modifying those
     * accumulations.
     *
     * @param previousAccumulation the previously captured accumulation
     * @param delta the newly captured (delta) accumulation
     * @return the result of the merge of the given accumulations.
     */
    override fun merge(
        previousAccumulation: ExponentialHistogramAccumulation,
        delta: ExponentialHistogramAccumulation
    ): ExponentialHistogramAccumulation {
        val sum: Double = previousAccumulation.sum + delta.sum
        val zeroCount: Long = previousAccumulation.zeroCount + delta.zeroCount

        // Create merged buckets
        val posBuckets: DoubleExponentialHistogramBuckets =
            DoubleExponentialHistogramBuckets.Companion.merge(
                previousAccumulation.positiveBuckets,
                delta.positiveBuckets
            )
        val negBuckets: DoubleExponentialHistogramBuckets =
            DoubleExponentialHistogramBuckets.Companion.merge(
                previousAccumulation.negativeBuckets,
                delta.negativeBuckets
            )

        // resolve possible scale difference due to merge
        val commonScale: Int = min(posBuckets.scale, negBuckets.scale)
        posBuckets.downscale(posBuckets.scale - commonScale)
        negBuckets.downscale(negBuckets.scale - commonScale)
        return ExponentialHistogramAccumulation.Companion.create(
            posBuckets.scale,
            sum,
            posBuckets,
            negBuckets,
            zeroCount,
            delta.exemplars
        )
    }

    /**
     * Returns a new DELTA aggregation by comparing two cumulative measurements.
     *
     * It is similar to merge(), however it decrements counts and sum instead of incrementing. It
     * does not modify the accumulations.
     *
     * @param previousCumulative the previously captured accumulation.
     * @param currentCumulative the newly captured (cumulative) accumulation.
     * @return The resulting delta accumulation.
     */
    override fun diff(
        previousCumulative: ExponentialHistogramAccumulation,
        currentCumulative: ExponentialHistogramAccumulation
    ): ExponentialHistogramAccumulation {

        // or maybe just do something similar to merge instead minus rather than increment
        val sum: Double = currentCumulative.sum - previousCumulative.sum
        val zeroCount: Long = currentCumulative.zeroCount - previousCumulative.zeroCount
        val posBuckets: DoubleExponentialHistogramBuckets =
            DoubleExponentialHistogramBuckets.Companion.diff(
                currentCumulative.positiveBuckets,
                previousCumulative.positiveBuckets
            )
        val negBuckets: DoubleExponentialHistogramBuckets =
            DoubleExponentialHistogramBuckets.Companion.diff(
                currentCumulative.negativeBuckets,
                previousCumulative.negativeBuckets
            )

        // resolve possible scale difference due to merge
        val commonScale: Int = min(posBuckets.scale, negBuckets.scale)
        posBuckets.downscale(posBuckets.scale - commonScale)
        negBuckets.downscale(negBuckets.scale - commonScale)
        return ExponentialHistogramAccumulation.Companion.create(
            posBuckets.scale,
            sum,
            posBuckets,
            negBuckets,
            zeroCount,
            currentCumulative.exemplars
        )
    }

    override fun toMetricData(
        resource: Resource,
        instrumentationLibrary: InstrumentationLibraryInfo,
        metricDescriptor: MetricDescriptor,
        accumulationByLabels: Map<Attributes, ExponentialHistogramAccumulation>,
        temporality: AggregationTemporality,
        startEpochNanos: Long,
        lastCollectionEpoch: Long,
        epochNanos: Long
    ): MetricData {
        return MetricData.createExponentialHistogram(
            resource,
            instrumentationLibrary,
            metricDescriptor.name,
            metricDescriptor.description,
            metricDescriptor.unit,
            ExponentialHistogramData.create(
                temporality,
                MetricDataUtils.toExponentialHistogramPointList(
                    accumulationByLabels,
                    if ((temporality === AggregationTemporality.CUMULATIVE)) startEpochNanos
                    else lastCollectionEpoch,
                    epochNanos
                )
            )
        )
    }

    internal class Handle constructor(reservoir: ExemplarReservoir) :
        AggregatorHandle<ExponentialHistogramAccumulation>(reservoir) {
        private var scale: Int
        private var positiveBuckets: DoubleExponentialHistogramBuckets
        private var negativeBuckets: DoubleExponentialHistogramBuckets
        private var zeroCount: Long = 0
        private var sum: Double = 0.0

        init {
            scale = DoubleExponentialHistogramBuckets.Companion.MAX_SCALE
            positiveBuckets = DoubleExponentialHistogramBuckets()
            negativeBuckets = DoubleExponentialHistogramBuckets()
        }

        override fun doAccumulateThenReset(
            exemplars: List<ExemplarData>
        ): ExponentialHistogramAccumulation {
            val acc: ExponentialHistogramAccumulation =
                ExponentialHistogramAccumulation.Companion.create(
                    scale,
                    sum,
                    positiveBuckets,
                    negativeBuckets,
                    zeroCount,
                    exemplars
                )
            sum = 0.0
            zeroCount = 0
            positiveBuckets = DoubleExponentialHistogramBuckets()
            negativeBuckets = DoubleExponentialHistogramBuckets()
            return acc
        }

        override fun doRecordDouble(value: Double) {

            // ignore NaN and infinity
            if (value.isFinite()) {
                return
            }
            sum += value
            val c: Int = value.compareTo(0.0)
            if (c == 0) {
                zeroCount++
                return
            }

            // Record; If recording fails, calculate scale reduction and scale down to fit new
            // value.
            // 2nd attempt at recording should work with new scale
            val buckets: DoubleExponentialHistogramBuckets =
                if ((c > 0)) positiveBuckets else negativeBuckets
            if (!buckets.record(value)) {
                // getScaleReduction() used with downScale() will scale down as required to record
                // value,
                // fit inside max allowed buckets, and make sure index can be represented by int.
                downScale(buckets.getScaleReduction(value))
                buckets.record(value)
            }
        }

        override fun doRecordLong(value: Long) {
            doRecordDouble(value.toDouble())
        }

        fun downScale(by: Int) {
            positiveBuckets.downscale(by)
            negativeBuckets.downscale(by)
            scale -= by
        }
    }
}
