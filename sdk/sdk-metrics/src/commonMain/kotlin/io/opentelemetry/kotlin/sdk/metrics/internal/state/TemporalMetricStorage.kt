/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.Aggregator
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.export.CollectionHandle
import io.opentelemetry.kotlin.sdk.resources.Resource

/** Stores last reported time and (optional) accumulation for metrics. */
internal class TemporalMetricStorage<T>(
    private val aggregator: Aggregator<T>,
    private val isSynchronous: Boolean
) {
    private val reportHistory: MutableMap<CollectionHandle, LastReportedAccumulation<T>> =
        mutableMapOf()

    /**
     * Builds the [MetricData] streams to report against a specific metric reader.
     *
     * @param collector The handle of the metric reader.
     * @param resource The resource to attach these metrics against.
     * @param instrumentationLibraryInfo The instrumentation library that generated these metrics.
     * @param temporality The aggregation temporality requested by the reader.
     * @param currentAccumulation THe current accumulation of metric data from instruments. This
     * might be delta (for synchronous) or cumulative (for asynchronous).
     * @param startEpochNanos The timestamp when the metrics SDK started.
     * @param epochNanos The current collection timestamp.
     * @return The [MetricData] points or `null`.
     */
    fun buildMetricFor(
        collector: CollectionHandle,
        resource: Resource,
        instrumentationLibraryInfo: InstrumentationLibraryInfo,
        descriptor: MetricDescriptor, // Temporality is requested by the collector.
        temporality: AggregationTemporality,
        currentAccumulation: Map<Attributes, T>,
        startEpochNanos: Long,
        epochNanos: Long
    ): MetricData? {
        // In case it's our first collection, default to start timestamp.
        var lastCollectionEpoch = startEpochNanos
        var result = currentAccumulation.toMutableMap()
        // Check our last report time.
        if (reportHistory.containsKey(collector)) {
            val last = reportHistory[collector]!!
            lastCollectionEpoch = last.epochNanos
            // Use aggregation temporality + instrument to determine if we do a merge or a diff of
            // previous.  We have the following four scenarios:
            // 1. Delta Aggregation (temporality) + Cumulative recording (async instrument).
            //    Here we diff with last cumulative to get a delta.
            // 2. Cumulative Aggregation + Delta recording (sync instrument).
            //    Here we merge with our last record to get a cumulative aggregation.
            // 3. Cumulative Aggregation + Cumulative recording - do nothing
            // 4. Delta Aggregation + Delta recording - do nothing.
            if (temporality === AggregationTemporality.DELTA && !isSynchronous) {
                MetricStorageUtils.diffInPlace<T>(
                    last.accumulation,
                    currentAccumulation,
                    aggregator
                )
                result = last.accumulation
            } else if (temporality === AggregationTemporality.CUMULATIVE && isSynchronous) {
                // We need to make sure the current delta recording gets merged into the previous
                // cumulative
                // for the next cumulative measurement.
                io.opentelemetry.kotlin.sdk.metrics.internal.state.MetricStorageUtils.mergeInPlace<
                    T>(last.accumulation, currentAccumulation, aggregator)
                result = last.accumulation
            }
        }
        // Update last reported (cumulative) accumulation.
        // For synchronous instruments, we need the merge result.
        // For asynchronous instruments, we need the recorded value.
        // This assumes aggregation remains consistent for the lifetime of a collector, and
        // could be optimised to not record results for cases 3+4 listed above.
        if (isSynchronous) {
            // Sync instruments remember the full recording.
            reportHistory[collector] = LastReportedAccumulation(result, epochNanos)
        } else {
            // Async instruments record the raw measurement.
            reportHistory[collector] =
                LastReportedAccumulation(currentAccumulation.toMutableMap(), epochNanos)
        }
        return if (result.isEmpty()) {
            null
        } else
            aggregator.toMetricData(
                resource,
                instrumentationLibraryInfo,
                descriptor,
                result,
                temporality,
                startEpochNanos,
                lastCollectionEpoch,
                epochNanos
            )
    }

    /** Remembers what was presented to a specific exporter. */
    private class LastReportedAccumulation<T>
    /**
     * Constructs a new reporting record.
     *
     * @param accumulation The last accumulation of metric data.
     * @param epochNanos The timestamp the data was reported.
     */
    (val accumulation: MutableMap<Attributes, T>, val epochNanos: Long)
}
