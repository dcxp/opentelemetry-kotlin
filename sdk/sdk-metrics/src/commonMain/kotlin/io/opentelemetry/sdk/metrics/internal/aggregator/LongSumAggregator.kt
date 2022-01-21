/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.sdk.metrics.internal.aggregator

import io.opentelemetry.Supplier
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.sdk.metrics.data.ExemplarData
import io.opentelemetry.sdk.metrics.data.LongSumData
import io.opentelemetry.sdk.metrics.data.MetricData
import io.opentelemetry.sdk.metrics.exemplar.ExemplarReservoir
import io.opentelemetry.sdk.metrics.internal.descriptor.InstrumentDescriptor
import io.opentelemetry.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.sdk.resources.Resource
import kotlinx.atomicfu.atomic

/**
 * Sum aggregator that keeps values as `long`s.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
class LongSumAggregator(
    instrumentDescriptor: InstrumentDescriptor,
    reservoirSupplier: Supplier<ExemplarReservoir>
) : AbstractSumAggregator<LongAccumulation>(instrumentDescriptor) {
    private val reservoirSupplier: Supplier<ExemplarReservoir>

    init {
        this.reservoirSupplier = reservoirSupplier
    }

    override fun createHandle(): AggregatorHandle<LongAccumulation> {
        return Handle(reservoirSupplier.get())
    }

    override fun merge(
        previousAccumulation: LongAccumulation,
        accumulation: LongAccumulation
    ): LongAccumulation {
        return LongAccumulation.Companion.create(
            previousAccumulation.value + accumulation.value,
            accumulation.exemplars
        )
    }

    override fun diff(
        previousAccumulation: LongAccumulation,
        accumulation: LongAccumulation
    ): LongAccumulation {
        return LongAccumulation.Companion.create(
            accumulation.value - previousAccumulation.value,
            accumulation.exemplars
        )
    }

    override fun toMetricData(
        resource: Resource,
        instrumentationLibraryInfo: InstrumentationLibraryInfo,
        descriptor: MetricDescriptor,
        accumulationByLabels: Map<Attributes, LongAccumulation>,
        temporality: AggregationTemporality,
        startEpochNanos: Long,
        lastCollectionEpoch: Long,
        epochNanos: Long
    ): MetricData {
        return MetricData.createLongSum(
            resource,
            instrumentationLibraryInfo,
            descriptor.name,
            descriptor.description,
            descriptor.unit,
            LongSumData.create(
                isMonotonic,
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
        override fun doAccumulateThenReset(exemplars: List<ExemplarData>): LongAccumulation {
            val sum = current.getAndSet(0)
            return LongAccumulation.create(sum, exemplars)
        }

        override fun doRecordLong(value: Long) {
            current.addAndGet(value)
        }
    }
}
