/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.ExemplarData
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarReservoir
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.resources.Resource

/**
 * A "null object" Aggregator which denotes no aggregation should occur.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
class EmptyAggregator private constructor() :
    io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.Aggregator<Unit> {
    override fun createHandle():
        io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.AggregatorHandle<Unit> {
        return HANDLE
    }

    override fun merge(previousAccumulation: Unit, accumulation: Unit): Unit {}

    override fun diff(previousAccumulation: Unit, accumulation: Unit): Unit {}

    override fun toMetricData(
        resource: Resource,
        instrumentationLibraryInfo: InstrumentationLibraryInfo,
        descriptor: MetricDescriptor,
        accumulationByLabels: Map<Attributes, Unit>,
        temporality: AggregationTemporality,
        startEpochNanos: Long,
        lastCollectionEpoch: Long,
        epochNanos: Long
    ): MetricData {
        throw Exception("Can not create empty metric")
    }

    companion object {
        val INSTANCE: io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.Aggregator<Unit> =
            EmptyAggregator()
        private val HANDLE:
            io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.AggregatorHandle<Unit> =
            object :
                io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.AggregatorHandle<Unit>(
                    ExemplarReservoir.noSamples()
                ) {
                override fun doRecordLong(value: Long) {}
                override fun doRecordDouble(value: Double) {}
                override fun doAccumulateThenReset(exemplars: List<ExemplarData>): Unit {
                    return
                }
            }
    }
}
