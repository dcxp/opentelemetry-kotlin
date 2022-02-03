/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.opentelemetry.kotlin.Supplier
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.DoubleGaugeData
import io.opentelemetry.kotlin.sdk.metrics.data.ExemplarData
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarReservoir
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.resources.Resource
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic

/**
 * Aggregator that aggregates recorded values by storing the last recorded value.
 *
 * Limitation: The current implementation does not store a time when the value was recorded, so
 * merging multiple LastValueAggregators will not preserve the ordering of records. This is not a
 * problem because LastValueAggregator is currently only available for Observers which record all
 * values once.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
class DoubleLastValueAggregator(private val reservoirSupplier: Supplier<ExemplarReservoir>) :
    Aggregator<DoubleAccumulation> {

    override fun createHandle(): AggregatorHandle<DoubleAccumulation> {
        return Handle(reservoirSupplier.get())
    }

    override fun merge(
        previous: DoubleAccumulation,
        current: DoubleAccumulation
    ): DoubleAccumulation {
        return current
    }

    override fun diff(
        previous: DoubleAccumulation,
        current: DoubleAccumulation
    ): DoubleAccumulation {
        return current
    }

    override fun toMetricData(
        resource: Resource,
        instrumentationLibraryInfo: InstrumentationLibraryInfo,
        descriptor: MetricDescriptor,
        accumulationByLabels: Map<Attributes, DoubleAccumulation>,
        temporality: AggregationTemporality,
        startEpochNanos: Long,
        lastCollectionEpochNanos: Long,
        epochNanos: Long
    ): MetricData {
        // Gauge does not need a start time, but we send one as advised by the data model
        // for identifying resets.
        return MetricData.createDoubleGauge(
            resource,
            instrumentationLibraryInfo,
            descriptor.name,
            descriptor.description,
            descriptor.unit,
            DoubleGaugeData.create(
                MetricDataUtils.toDoublePointList(
                    accumulationByLabels,
                    if (temporality === AggregationTemporality.CUMULATIVE) startEpochNanos
                    else lastCollectionEpochNanos,
                    epochNanos
                )
            )
        )
    }

    internal class Handle(reservoir: ExemplarReservoir) :
        AggregatorHandle<DoubleAccumulation>(reservoir) {
        private val current: AtomicRef<Double> = atomic(DEFAULT_VALUE.value)

        override fun doAccumulateThenReset(exemplars: List<ExemplarData>): DoubleAccumulation {
            return DoubleAccumulation.create(current.getAndSet(DEFAULT_VALUE.value), exemplars)
        }

        override fun doRecordDouble(value: Double) {
            current.lazySet(value)
        }

        companion object {
            private val DEFAULT_VALUE: AtomicRef<Double> = atomic(Double.NaN)
        }
    }
}
