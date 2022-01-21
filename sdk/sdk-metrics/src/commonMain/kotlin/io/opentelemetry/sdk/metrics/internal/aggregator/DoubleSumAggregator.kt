/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.sdk.metrics.internal.aggregator

import io.opentelemetry.Supplier
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.context.Context
import io.opentelemetry.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.sdk.metrics.data.DoubleSumData
import io.opentelemetry.sdk.metrics.data.ExemplarData
import io.opentelemetry.sdk.metrics.data.MetricData
import io.opentelemetry.sdk.metrics.exemplar.ExemplarReservoir
import io.opentelemetry.sdk.metrics.internal.descriptor.InstrumentDescriptor
import io.opentelemetry.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.sdk.resources.Resource
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update

/**
 * Sum aggregator that keeps values as `double`s.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
class DoubleSumAggregator(
    instrumentDescriptor: InstrumentDescriptor,
    private val reservoirSupplier: Supplier<ExemplarReservoir>
) :
    io.opentelemetry.sdk.metrics.internal.aggregator.AbstractSumAggregator<
        io.opentelemetry.sdk.metrics.internal.aggregator.DoubleAccumulation>(instrumentDescriptor) {

    override fun createHandle():
        io.opentelemetry.sdk.metrics.internal.aggregator.AggregatorHandle<
            io.opentelemetry.sdk.metrics.internal.aggregator.DoubleAccumulation> {
        return Handle(reservoirSupplier.get())
    }

    override fun accumulateDoubleMeasurement(
        value: Double,
        attributes: Attributes,
        context: Context
    ): io.opentelemetry.sdk.metrics.internal.aggregator.DoubleAccumulation {
        return io.opentelemetry.sdk.metrics.internal.aggregator.DoubleAccumulation.Companion.create(
            value
        )
    }

    override fun merge(
        previousAccumulation: io.opentelemetry.sdk.metrics.internal.aggregator.DoubleAccumulation,
        accumulation: io.opentelemetry.sdk.metrics.internal.aggregator.DoubleAccumulation
    ): io.opentelemetry.sdk.metrics.internal.aggregator.DoubleAccumulation {
        return io.opentelemetry.sdk.metrics.internal.aggregator.DoubleAccumulation.Companion.create(
            previousAccumulation.value + accumulation.value,
            accumulation.exemplars
        )
    }

    override fun diff(
        previousAccumulation: io.opentelemetry.sdk.metrics.internal.aggregator.DoubleAccumulation,
        accumulation: io.opentelemetry.sdk.metrics.internal.aggregator.DoubleAccumulation
    ): io.opentelemetry.sdk.metrics.internal.aggregator.DoubleAccumulation {
        return io.opentelemetry.sdk.metrics.internal.aggregator.DoubleAccumulation.Companion.create(
            accumulation.value - previousAccumulation.value,
            accumulation.exemplars
        )
    }

    override fun toMetricData(
        resource: Resource,
        instrumentationLibraryInfo: InstrumentationLibraryInfo,
        descriptor: MetricDescriptor,
        accumulationByLabels:
            Map<Attributes, io.opentelemetry.sdk.metrics.internal.aggregator.DoubleAccumulation>,
        temporality: AggregationTemporality,
        startEpochNanos: Long,
        lastCollectionEpoch: Long,
        epochNanos: Long
    ): MetricData {
        return MetricData.createDoubleSum(
            resource,
            instrumentationLibraryInfo,
            descriptor.name,
            descriptor.description,
            descriptor.unit,
            DoubleSumData.create(
                isMonotonic,
                temporality,
                io.opentelemetry.sdk.metrics.internal.aggregator.MetricDataUtils.toDoublePointList(
                    accumulationByLabels,
                    if (temporality === AggregationTemporality.CUMULATIVE) startEpochNanos
                    else lastCollectionEpoch,
                    epochNanos
                )
            )
        )
    }

    internal class Handle(exemplarReservoir: ExemplarReservoir) :
        io.opentelemetry.sdk.metrics.internal.aggregator.AggregatorHandle<
            io.opentelemetry.sdk.metrics.internal.aggregator.DoubleAccumulation>(
            exemplarReservoir
        ) {
        private val current = atomic(0.0)
        override fun doAccumulateThenReset(
            exemplars: List<ExemplarData>
        ): io.opentelemetry.sdk.metrics.internal.aggregator.DoubleAccumulation {
            val currentSum = current.getAndSet(0.0)
            return io.opentelemetry.sdk.metrics.internal.aggregator.DoubleAccumulation.Companion
                .create(currentSum, exemplars)
        }

        override fun doRecordDouble(value: Double) {
            current.update { it + value }
        }
    }
}
