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
import io.opentelemetry.sdk.metrics.data.LongGaugeData
import io.opentelemetry.sdk.metrics.data.MetricData
import io.opentelemetry.sdk.metrics.exemplar.ExemplarReservoir
import io.opentelemetry.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.sdk.resources.Resource
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
class LongLastValueAggregator(private val reservoirSupplier: Supplier<ExemplarReservoir>) :
    io.opentelemetry.sdk.metrics.internal.aggregator.Aggregator<
        io.opentelemetry.sdk.metrics.internal.aggregator.LongAccumulation> {

    override fun createHandle():
        io.opentelemetry.sdk.metrics.internal.aggregator.AggregatorHandle<
            io.opentelemetry.sdk.metrics.internal.aggregator.LongAccumulation> {
        return Handle(reservoirSupplier.get())
    }

    override fun merge(
        previous: io.opentelemetry.sdk.metrics.internal.aggregator.LongAccumulation,
        current: io.opentelemetry.sdk.metrics.internal.aggregator.LongAccumulation
    ): io.opentelemetry.sdk.metrics.internal.aggregator.LongAccumulation {
        return current
    }

    override fun diff(
        previous: io.opentelemetry.sdk.metrics.internal.aggregator.LongAccumulation,
        current: io.opentelemetry.sdk.metrics.internal.aggregator.LongAccumulation
    ): io.opentelemetry.sdk.metrics.internal.aggregator.LongAccumulation {
        return current
    }

    override fun toMetricData(
        resource: Resource,
        instrumentationLibraryInfo: InstrumentationLibraryInfo,
        descriptor: MetricDescriptor,
        accumulationByLabels:
            Map<Attributes, io.opentelemetry.sdk.metrics.internal.aggregator.LongAccumulation>,
        temporality: AggregationTemporality,
        startEpochNanos: Long,
        lastCollectionEpoch: Long,
        epochNanos: Long
    ): MetricData {
        // Last-Value ignores temporality generally, but we can set a start time on the gauge.
        return MetricData.createLongGauge(
            resource,
            instrumentationLibraryInfo,
            descriptor.name,
            descriptor.description,
            descriptor.unit,
            LongGaugeData.create(
                io.opentelemetry.sdk.metrics.internal.aggregator.MetricDataUtils.toLongPointList(
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
            io.opentelemetry.sdk.metrics.internal.aggregator.LongAccumulation>(exemplarReservoir) {
        private val current = atomic(DEFAULT_VALUE)

        override fun doAccumulateThenReset(
            exemplars: List<ExemplarData>
        ): io.opentelemetry.sdk.metrics.internal.aggregator.LongAccumulation {
            return io.opentelemetry.sdk.metrics.internal.aggregator.LongAccumulation.Companion
                .create(current.getAndSet(DEFAULT_VALUE), exemplars)
        }

        override fun doRecordLong(value: Long) {
            current.getAndSet(value)
        }

        companion object {
            private val DEFAULT_VALUE: Long = 0
        }
    }
}
