/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

interface ValueAtPercentile {
    /**
     * The percentile of a distribution. Must be in the interval [0.0, 100.0].
     *
     * @return the percentile.
     */
    val percentile: Double

    /**
     * The value at the given percentile of a distribution.
     *
     * @return the value at the percentile.
     */
    val value: Double

    companion object {
        fun create(percentile: Double, value: Double): ValueAtPercentile {
            return Implementation(percentile, value)
        }
        data class Implementation(override val percentile: Double, override val value: Double) :
            ValueAtPercentile
    }
}
