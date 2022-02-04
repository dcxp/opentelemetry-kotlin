/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.opentelemetry.kotlin.sdk.metrics.data.ExemplarData

/** An accumulation representing `long` values and exemplars. */
interface LongAccumulation {
    /** The current value. */
    val value: Long

    /** Sampled measurements recorded during this accumulation. */
    val exemplars: List<ExemplarData>

    companion object {
        fun create(value: Long, exemplars: List<ExemplarData> = emptyList()): LongAccumulation {
            return Implementation(value, exemplars)
        }
        data class Implementation(
            override val value: Long,
            override val exemplars: List<ExemplarData>
        ) : LongAccumulation
    }
}
