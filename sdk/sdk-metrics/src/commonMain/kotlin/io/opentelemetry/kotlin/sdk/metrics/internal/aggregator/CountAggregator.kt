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
import io.opentelemetry.kotlin.sdk.metrics.data.LongSumData
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarReservoir
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.resources.Resource
import kotlinx.atomicfu.atomic

internal class CountAggregator(private val reservoirSupplier: Supplier<ExemplarReservoir>) :
    Aggregator<LongAccumulation> {

    override fun createHandle(): AggregatorHandle<LongAccumulation> {
        return Handle(reservoirSupplier.get())
    }

    override fun merge(previous: LongAccumulation, current: LongAccumulation): LongAccumulation {
        return LongAccumulation.Companion.create(previous.value + current.value, current.exemplars)
    }

    override fun diff(previous: LongAccumulation, current: LongAccumulation): LongAccumulation {
        // For count of measurements, `diff` returns the "DELTA" of measurements that occurred.
        // Given how we aggregate, this effectively is just the current value for async
        // instruments.
        return current
    }

    override fun toMetricData(
        resource: Resource,
        instrumentationLibrary: InstrumentationLibraryInfo,
        metricDescriptor: MetricDescriptor,
        accumulationByLabels: Map<Attributes, LongAccumulation>,
        temporality: AggregationTemporality,
        startEpochNanos: Long,
        lastCollectionEpoch: Long,
        epochNanos: Long
    ): MetricData {
        return MetricData.createLongSum(
            resource,
            instrumentationLibrary,
            metricDescriptor.name,
            metricDescriptor.description,
            "1",
            LongSumData.create(
                /* isMonotonic= */ true,
                temporality,
                MetricDataUtils.toLongPointList(
                    accumulationByLabels,
                    if (temporality === AggregationTemporality.CUMULATIVE) startEpochNanos
                    else lastCollectionEpoch,
                    epochNanos
                )
            )
        )
    }

    internal class Handle(exemplarReservoir: ExemplarReservoir) :
        AggregatorHandle<LongAccumulation>(exemplarReservoir) {
        private val current = atomic(0L)
        override fun doRecordLong(value: Long) {
            current.incrementAndGet()
        }

        override fun doRecordDouble(value: Double) {
            current.incrementAndGet()
        }

        override fun doAccumulateThenReset(exemplars: List<ExemplarData>): LongAccumulation {
            val result = current.getAndSet(0)
            return LongAccumulation.create(result, exemplars)
        }
    }
}
