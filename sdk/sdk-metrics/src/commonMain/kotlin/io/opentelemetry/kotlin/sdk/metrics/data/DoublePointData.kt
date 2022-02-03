/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

import io.opentelemetry.kotlin.api.common.Attributes

/**
 * DoublePoint is a single data point in a timeseries that describes the time-varying value of a
 * double metric.
 */
interface DoublePointData : PointData {
    /**
     * Returns the value of the data point.
     *
     * @return the value of the data point.
     */
    val value: Double

    companion object {
        /**
         * Creates a [DoublePointData].
         *
         * @param startEpochNanos The starting time for the period where this point was sampled.
         * Note: While start time is optional in OTLP, all SDKs should produce it for all their
         * metrics, so it is required here.
         * @param epochNanos The ending time for the period when this value was sampled.
         * @param attributes The set of attributes associated with this point.
         * @param value The value that was sampled.
         * @param exemplars A collection of interesting sampled values from this time period.
         */
        /**
         * Creates a [DoublePointData].
         *
         * @param startEpochNanos The starting time for the period where this point was sampled.
         * Note: While start time is optional in OTLP, all SDKs should produce it for all their
         * metrics, so it is required here.
         * @param epochNanos The ending time for the period when this value was sampled.
         * @param attributes The set of attributes associated with this point.
         * @param value The value that was sampled.
         */
        fun create(
            startEpochNanos: Long,
            epochNanos: Long,
            attributes: Attributes,
            value: Double,
            exemplars: List<ExemplarData> = emptyList()
        ): DoublePointData {
            return Implementation(startEpochNanos, epochNanos, attributes, exemplars, value)
        }

        data class Implementation(
            override val startEpochNanos: Long,
            override val epochNanos: Long,
            override val attributes: Attributes,
            override val exemplars: List<ExemplarData>,
            override val value: Double,
        ) : DoublePointData
    }
}
