/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.opentelemetry.kotlin.sdk.metrics.data.ExemplarData

/**
 * An accumulation representing `long` values and exemplars.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 *
 * Visible for testing.
 */
interface DoubleAccumulation {
    /** The current value. */
    val value: Double

    /** Sampled measurements recorded during this accumulation. */
    val exemplars: List<ExemplarData>

    companion object {
        fun create(value: Double, exemplars: List<ExemplarData>): DoubleAccumulation {
            return Implementation(value, exemplars)
        }

        fun create(value: Double): DoubleAccumulation {
            return create(value, emptyList())
        }

        data class Implementation(
            override val value: Double,
            override val exemplars: List<ExemplarData>
        ) : DoubleAccumulation
    }
}
