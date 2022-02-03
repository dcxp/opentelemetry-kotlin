/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.DoubleSummaryData
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.resources.Resource
import kotlin.math.max
import kotlin.math.min

internal abstract class AbstractMinMaxSumCountAggregator : Aggregator<MinMaxSumCountAccumulation> {
    override fun merge(
        previous: MinMaxSumCountAccumulation,
        current: MinMaxSumCountAccumulation
    ): MinMaxSumCountAccumulation {
        return MinMaxSumCountAccumulation.Companion.create(
            previous.count + current.count,
            previous.sum + current.sum,
            min(previous.min, current.min),
            max(previous.max, current.max)
        )
    }

    override fun diff(
        previousCumulative: MinMaxSumCountAccumulation,
        currentCumulative: MinMaxSumCountAccumulation
    ): MinMaxSumCountAccumulation {
        // Summary does not support CUMULATIVE vs. DELTA.
        return currentCumulative
    }

    override fun toMetricData(
        resource: Resource,
        instrumentationLibrary: InstrumentationLibraryInfo,
        metricDescriptor: MetricDescriptor,
        accumulationByLabels: Map<Attributes, MinMaxSumCountAccumulation>,
        temporality: AggregationTemporality,
        startEpochNanos: Long,
        lastCollectionEpoch: Long,
        epochNanos: Long
    ): MetricData {
        // We always report as "summary" temporality.
        return MetricData.createDoubleSummary(
            resource,
            instrumentationLibrary,
            metricDescriptor.name,
            metricDescriptor.description,
            metricDescriptor.unit,
            DoubleSummaryData.create(
                MetricDataUtils.toDoubleSummaryPointList(
                    accumulationByLabels,
                    lastCollectionEpoch,
                    epochNanos
                )
            )
        )
    }
}
