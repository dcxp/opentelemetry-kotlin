/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.view

import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarFilter
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.Aggregator
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor

/**
 * Configures how measurements are combined into metrics for [View]s.
 *
 * Aggregation provides a set of built-in aggregations via static methods.
 */
abstract class Aggregation internal constructor() {
    /**
     * Returns a new [Aggregator].
     *
     * @param instrumentDescriptor the descriptor of the `Instrument` that will record measurements.
     * @param exemplarFilter the filter on which measurements should turn into exemplars
     * @return a new [Aggregator], or `null` if no measurements should be recorded.
     */
    abstract fun <T> createAggregator(
        instrumentDescriptor: InstrumentDescriptor,
        exemplarFilter: ExemplarFilter
    ): Aggregator<T>

    companion object {
        /** The None Aggregation will ignore/drop all Instrument Measurements. */
        fun none(): Aggregation {
            return NoAggregation.INSTANCE
        }

        /** The default aggregation for an instrument will be chosen. */
        fun defaultAggregation(): Aggregation {
            return DefaultAggregation.INSTANCE
        }

        /** Instrument measurements will be combined into a metric Sum. */
        fun sum(): Aggregation {
            return SumAggregation.DEFAULT
        }

        /** Remembers the last seen measurement and reports as a Gauge. */
        fun lastValue(): Aggregation {
            return LastValueAggregation.INSTANCE
        }

        /**
         * Aggregates measurements into an explicit bucket histogram using the default bucket
         * boundaries.
         */
        fun explicitBucketHistogram(): Aggregation {
            return ExplicitBucketHistogramAggregation.Companion.DEFAULT
        }

        /**
         * Aggregates measurements into an explicit bucket histogram.
         *
         * @param bucketBoundaries A list of (inclusive) upper bounds for the histogram. Should be
         * in order from lowest to highest.
         */
        fun explicitBucketHistogram(bucketBoundaries: List<Double>): Aggregation {
            return ExplicitBucketHistogramAggregation(bucketBoundaries)
        }

        /** Aggregates measurements using the best available Histogram. */
        fun histogram(): Aggregation {
            return explicitBucketHistogram()
        }
    }
}
