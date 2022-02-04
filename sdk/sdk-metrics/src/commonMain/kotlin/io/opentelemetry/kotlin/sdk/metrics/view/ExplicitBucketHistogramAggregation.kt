/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.view

import io.opentelemetry.kotlin.sdk.common.Clock
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarFilter
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarReservoir
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.Aggregator
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.DoubleHistogramAggregator
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.ExplicitBucketHistogramUtils
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor

/** Explicit bucket histogram aggregation configuration. */
internal class ExplicitBucketHistogramAggregation(private val bucketBoundaries: List<Double>) :
    Aggregation() {
    private val bucketBoundaryArray: DoubleArray =
        ExplicitBucketHistogramUtils.createBoundaryArray(bucketBoundaries)

    @Suppress("UNCHECKED_CAST")
    override fun <T> createAggregator(
        instrumentDescriptor: InstrumentDescriptor,
        exemplarFilter: ExemplarFilter
    ): Aggregator<T> {
        return DoubleHistogramAggregator(bucketBoundaryArray) {
            ExemplarReservoir.filtered(
                exemplarFilter,
                ExemplarReservoir.histogramBucketReservoir(Clock.default, bucketBoundaries)
            )
        } as
            Aggregator<T>
    }

    override fun toString(): String {
        return "ExplicitBucketHistogramAggregation($bucketBoundaries)"
    }

    companion object {
        val DEFAULT: Aggregation =
            ExplicitBucketHistogramAggregation(
                ExplicitBucketHistogramUtils.DEFAULT_HISTOGRAM_BUCKET_BOUNDARIES
            )
    }
}
