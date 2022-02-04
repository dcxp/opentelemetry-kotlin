/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.opentelemetry.kotlin.Supplier
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.DoubleSumData
import io.opentelemetry.kotlin.sdk.metrics.data.ExemplarData
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarReservoir
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.resources.Resource
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
) : AbstractSumAggregator<DoubleAccumulation>(instrumentDescriptor) {

    override fun createHandle(): AggregatorHandle<DoubleAccumulation> {
        return Handle(reservoirSupplier.get())
    }

    override fun accumulateDoubleMeasurement(
        value: Double,
        attributes: Attributes,
        context: Context
    ): DoubleAccumulation {
        return DoubleAccumulation.create(value)
    }

    override fun merge(
        previousAccumulation: DoubleAccumulation,
        accumulation: DoubleAccumulation
    ): DoubleAccumulation {
        return DoubleAccumulation.create(
            previousAccumulation.value + accumulation.value,
            accumulation.exemplars
        )
    }

    override fun diff(
        previousAccumulation: DoubleAccumulation,
        accumulation: DoubleAccumulation
    ): DoubleAccumulation {
        return DoubleAccumulation.create(
            accumulation.value - previousAccumulation.value,
            accumulation.exemplars
        )
    }

    override fun toMetricData(
        resource: Resource,
        instrumentationLibraryInfo: InstrumentationLibraryInfo,
        descriptor: MetricDescriptor,
        accumulationByLabels: Map<Attributes, DoubleAccumulation>,
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
                MetricDataUtils.toDoublePointList(
                    accumulationByLabels,
                    if (temporality === AggregationTemporality.CUMULATIVE) startEpochNanos
                    else lastCollectionEpoch,
                    epochNanos
                )
            )
        )
    }

    internal class Handle(exemplarReservoir: ExemplarReservoir) :
        AggregatorHandle<DoubleAccumulation>(exemplarReservoir) {
        // Wrapper is needed because of a deadlock in native if a double atomic is directly updated
        private val current = atomic(SumWrapper())

        override fun doAccumulateThenReset(exemplars: List<ExemplarData>): DoubleAccumulation {
            val currentSum = current.getAndSet(SumWrapper()).sum
            return DoubleAccumulation.create(currentSum, exemplars)
        }

        override fun doRecordDouble(value: Double) {
            current.update { it.copy(sum = it.sum + value) }
        }

        private data class SumWrapper(val sum: Double = 0.0)
    }
}
