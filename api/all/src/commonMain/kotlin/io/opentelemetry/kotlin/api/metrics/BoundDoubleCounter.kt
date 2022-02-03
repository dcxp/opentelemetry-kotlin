/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.metrics

import io.opentelemetry.kotlin.context.Context

/** A counter instrument that records `double` values with pre-associated attributes. */
interface BoundDoubleCounter {
    /**
     * Records a value with pre-bound attributes.
     *
     * Note: This may use `Context.current()` to pull the context associated with this measurement.
     *
     * @param value The increment amount. MUST be non-negative.
     */
    fun add(value: Double)

    /**
     * Records a value with pre-bound attributes.
     *
     * @param value The increment amount. MUST be non-negative.
     * @param context The explicit context to associate with this measurement.
     */
    fun add(value: Double, context: Context)

    /**
     * Unbinds the current bound instance from the [DoubleCounter].
     *
     * After this method returns the current instance is considered invalid (not being managed by
     * the instrument).
     */
    fun unbind()
}
