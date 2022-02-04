/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.exemplar

import io.opentelemetry.kotlin.Supplier
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.Clock
import io.opentelemetry.kotlin.sdk.metrics.data.ExemplarData
import kotlin.random.Random

/**
 * An interface for an exemplar reservoir of samples.
 *
 * This represents a reservoir for a specific "point" of metric data.
 */
interface ExemplarReservoir {
    /** Offers a `long` measurement to be sampled. */
    fun offerMeasurement(value: Long, attributes: Attributes, context: Context)

    /** Offers a `double` measurement to be sampled. */
    fun offerMeasurement(value: Double, attributes: Attributes, context: Context)

    /**
     * Builds (an immutable) list of Exemplars for exporting from the current reservoir.
     *
     * Additionally, clears the reservoir for the next sampling period.
     *
     * @param pointAttributes the [Attributes] associated with the metric point. [ ]s should filter
     * these out of their final data state.
     * @return An (immutable) list of sampled exemplars for this point. Implementers are expected to
     * filter out `pointAttributes` from the original recorded attributes.
     */
    fun collectAndReset(pointAttributes: Attributes): List<ExemplarData>

    companion object {
        /** An exemplar reservoir that stores no exemplars. */
        fun noSamples(): ExemplarReservoir {
            return NoExemplarReservoir.INSTANCE
        }

        /** Wraps a [ExemplarReservoir] with a measurement pre-filter. */
        fun filtered(filter: ExemplarFilter, original: ExemplarReservoir): ExemplarReservoir {
            // Optimisation on memory usage.
            return if (filter === ExemplarFilter.neverSample()) {
                noSamples()
            } else FilteredExemplarReservoir(filter, original)
        }

        /**
         * A Reservoir sampler with fixed size that stores the given number of exemplars.
         *
         * @param clock The clock to use when annotating measurements with time.
         * @param size The maximum number of exemplars to preserve.
         * @param randomSupplier The random number generater to use for sampling.
         */
        fun fixedSizeReservoir(
            clock: Clock,
            size: Int,
            randomSupplier: Supplier<Random>
        ): ExemplarReservoir {
            return FixedSizeExemplarReservoir(clock, size, randomSupplier)
        }

        /**
         * A Reservoir sampler that preserves the latest seen measurement per-histogram bucket.
         *
         * @param clock The clock to use when annotating measurements with time.
         * @param boundaries A list of (inclusive) upper bounds for the histogram. Should be in
         * order from lowest to highest.
         */
        fun histogramBucketReservoir(clock: Clock, boundaries: List<Double>): ExemplarReservoir {
            return HistogramBucketExemplarReservoir.create(clock, boundaries)
        }
    }
}
