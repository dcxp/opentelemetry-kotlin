/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.exemplar

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context

/**
 * Exemplar filters are used to pre-filter measurements before attempting to store them in a
 * reservoir.
 */
interface ExemplarFilter {
    /** Returns whether or not a reservoir should attempt to filter a measurement. */
    fun shouldSampleMeasurement(value: Long, attributes: Attributes, context: Context): Boolean

    /** Returns whether or not a reservoir should attempt to filter a measurement. */
    fun shouldSampleMeasurement(value: Double, attributes: Attributes, context: Context): Boolean

    companion object {
        /**
         * A filter that only accepts measurements where there is a `Span` in [Context] that is
         * being sampled.
         */
        fun sampleWithTraces(): ExemplarFilter {
            return WithTraceExemplarFilter.INSTANCE
        }

        /** A filter that accepts any measurement. */
        fun alwaysSample(): ExemplarFilter {
            return AlwaysSampleFilter.INSTANCE
        }

        /** A filter that accepts no measurements. */
        fun neverSample(): ExemplarFilter {
            return NeverSampleFilter.INSTANCE
        }
    }
}
