/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentType
import io.opentelemetry.kotlin.sdk.metrics.data.DoubleHistogramPointData
import io.opentelemetry.kotlin.sdk.metrics.data.DoublePointData
import io.opentelemetry.kotlin.sdk.metrics.data.DoubleSummaryPointData
import io.opentelemetry.kotlin.sdk.metrics.data.ExponentialHistogramPointData
import io.opentelemetry.kotlin.sdk.metrics.data.LongPointData
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor

internal object MetricDataUtils {
    /** Returns true if the instrument does not allow negative measurements. */
    fun isMonotonicInstrument(descriptor: InstrumentDescriptor): Boolean {
        val type: InstrumentType = descriptor.type
        return type === InstrumentType.HISTOGRAM ||
            type === InstrumentType.COUNTER ||
            type === InstrumentType.OBSERVABLE_SUM
    }

    fun toLongPointList(
        accumulationMap: Map<Attributes, LongAccumulation>,
        startEpochNanos: Long,
        epochNanos: Long
    ): List<LongPointData> {
        val points =
            accumulationMap.map { entry ->
                LongPointData.create(
                    startEpochNanos,
                    epochNanos,
                    entry.key,
                    entry.value.value,
                    entry.value.exemplars
                )
            }
        return points
    }

    fun toDoublePointList(
        accumulationMap: Map<Attributes, DoubleAccumulation>,
        startEpochNanos: Long,
        epochNanos: Long
    ): List<DoublePointData> {
        val points =
            accumulationMap.map { entry ->
                DoublePointData.create(
                    startEpochNanos,
                    epochNanos,
                    entry.key,
                    entry.value.value,
                    entry.value.exemplars
                )
            }
        return points
    }

    fun toDoubleSummaryPointList(
        accumulationMap: Map<Attributes, MinMaxSumCountAccumulation>,
        startEpochNanos: Long,
        epochNanos: Long
    ): List<DoubleSummaryPointData> {
        val points =
            accumulationMap.map { entry ->
                entry.value.toPoint(startEpochNanos, epochNanos, entry.key)
            }
        return points
    }

    fun toDoubleHistogramPointList(
        accumulationMap: Map<Attributes, HistogramAccumulation>,
        startEpochNanos: Long,
        epochNanos: Long,
        boundaries: List<Double>
    ): List<DoubleHistogramPointData> {
        val points =
            accumulationMap.map { entry ->
                val labels = entry.key
                val aggregator = entry.value
                val counts = aggregator.counts.toList()

                DoubleHistogramPointData.create(
                    startEpochNanos,
                    epochNanos,
                    labels,
                    aggregator.sum,
                    boundaries,
                    counts,
                    aggregator.exemplars
                )
            }
        return points
    }

    fun toExponentialHistogramPointList(
        accumulationMap: Map<Attributes, ExponentialHistogramAccumulation>,
        startEpochNanos: Long,
        epochNanos: Long
    ): List<ExponentialHistogramPointData> {
        val points =
            accumulationMap.map { entry ->
                val labels = entry.key
                val aggregator = entry.value

                ExponentialHistogramPointData.create(
                    aggregator.scale,
                    aggregator.sum,
                    aggregator.zeroCount,
                    aggregator.positiveBuckets,
                    aggregator.negativeBuckets,
                    startEpochNanos,
                    epochNanos,
                    labels,
                    aggregator.exemplars
                )
            }
        return points
    }
}
