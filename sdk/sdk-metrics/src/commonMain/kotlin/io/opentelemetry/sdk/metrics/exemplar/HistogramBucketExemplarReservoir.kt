/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.sdk.metrics.exemplar

import io.opentelemetry.api.common.Attributes
import io.opentelemetry.context.Context
import io.opentelemetry.sdk.common.Clock
import io.opentelemetry.sdk.metrics.internal.aggregator.ExplicitBucketHistogramUtils.createBoundaryArray
import io.opentelemetry.sdk.metrics.internal.aggregator.ExplicitBucketHistogramUtils.findBucketIndex

/** A Reservoir sampler that preserves the latest seen measurement per-histogram bucket. */
/**
 * Constructs a new reservoir sampler that aligns exemplars with histogram buckets.
 *
 * @param clock The clock to use when annotating measurements with time.
 * @param boundaries Histogram bucket boundaries.
 */
internal class HistogramBucketExemplarReservoir(clock: Clock, private val boundaries: DoubleArray) :
    AbstractFixedSizeExemplarReservoir(clock, boundaries.size + 1) {

    override fun reservoirIndexFor(value: Double, attributes: Attributes, context: Context): Int {
        return findBucketIndex(boundaries, value)
    }

    companion object {
        /** Constructs a new histogram bucket exemplar reservoir using standard configuration. */
        fun create(clock: Clock, boundaries: List<Double>): HistogramBucketExemplarReservoir {
            return HistogramBucketExemplarReservoir(clock, createBoundaryArray(boundaries))
        }
    }
}
