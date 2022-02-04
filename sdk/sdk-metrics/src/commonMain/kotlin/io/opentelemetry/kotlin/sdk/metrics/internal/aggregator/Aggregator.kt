/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.resources.Resource

/**
 * Aggregator represents the abstract class for all the available aggregations that can be computed
 * during the accumulation phase for all the instrument.
 *
 * The synchronous instruments will create an [AggregatorHandle] to record individual measurements
 * synchronously, and for asynchronous the [.accumulateDoubleMeasurement] or
 * [.accumulateLongMeasurement] will be used when reading values from the instrument callbacks.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
interface Aggregator<T> {
    /**
     * Returns a new [AggregatorHandle]. This MUST by used by the synchronous to aggregate recorded
     * measurements during the collection cycle.
     *
     * @return a new [AggregatorHandle].
     */
    fun createHandle(): AggregatorHandle<T>

    /**
     * Returns a new `Accumulation` for the given value. This MUST be used by the asynchronous
     * instruments to create `Accumulation` that are passed to the processor.
     *
     * @param value the given value to be used to create the `Accumulation`.
     * @return a new `Accumulation` for the given value.
     */
    fun accumulateLongMeasurement(value: Long, attributes: Attributes, context: Context): T? {
        val handle: AggregatorHandle<T> = createHandle()
        handle.recordLong(value, attributes, context)
        return handle.accumulateThenReset(attributes)
    }

    /**
     * Returns a new `Accumulation` for the given value. This MUST be used by the asynchronous
     * instruments to create `Accumulation` that are passed to the processor.
     *
     * @param value the given value to be used to create the `Accumulation`.
     * @return a new `Accumulation` for the given value.
     */
    fun accumulateDoubleMeasurement(value: Double, attributes: Attributes, context: Context): T? {
        val handle: AggregatorHandle<T> = createHandle()
        handle.recordDouble(value, attributes, context)
        return handle.accumulateThenReset(attributes)
    }

    /**
     * Returns the result of the merge of the given accumulations.
     *
     * This should always assume that the accumulations do not overlap and merge together for a new
     * cumulative report.
     *
     * @param previousCumulative the previously captured accumulation
     * @param delta the newly captured (delta) accumulation
     * @return the result of the merge of the given accumulations.
     */
    fun merge(previousCumulative: T, delta: T): T

    /**
     * Returns a new DELTA aggregation by comparing two cumulative measurements.
     *
     * @param previousCumulative the previously captured accumulation.
     * @param currentCumulative the newly captured (cumulative) accumulation.
     * @return The resulting delta accumulation.
     */
    fun diff(previousCumulative: T, currentCumulative: T): T

    /**
     * Returns the [MetricData] that this `Aggregation` will produce.
     *
     * @param resource the resource producing the metric.
     * @param instrumentationLibrary the library that instrumented the metric.
     * @param metricDescriptor the name, description and unit of the metric.
     * @param accumulationByLabels the map of Labels to Accumulation.
     * @param temporality the temporality of the accumulation.
     * @param startEpochNanos the startEpochNanos for the `Point`.
     * @param epochNanos the epochNanos for the `Point`.
     * @return the [MetricDataType] that this `Aggregation` will produce.
     */
    fun toMetricData(
        resource: Resource,
        instrumentationLibrary: InstrumentationLibraryInfo,
        metricDescriptor: MetricDescriptor,
        accumulationByLabels: Map<Attributes, T>,
        temporality: AggregationTemporality,
        startEpochNanos: Long,
        lastCollectionEpoch: Long,
        epochNanos: Long
    ): MetricData

    companion object {
        /**
         * Returns the empty aggregator, an aggregator that never records measurements or reports
         * values.
         */
        fun empty(): Aggregator<Unit> {
            return EmptyAggregator.INSTANCE
        }
    }
}
