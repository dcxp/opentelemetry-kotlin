/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.metrics

import io.opentelemetry.context.Context

/** An up-down-counter instrument with pre-bound attributes. */
interface BoundLongUpDownCounter {
    /**
     * Records a value with pre-bound attributes.
     *
     * Note: This may use `Context.current()` to pull the context associated with this measurement.
     *
     * @param value The increment amount. May be positive, negative or zero.
     */
    fun add(value: Long)

    /**
     * Records a value with a pre-bound attributes.
     *
     * @param value The increment amount. May be positive, negative or zero.
     * @param context The explicit context to associate with this measurement.
     */
    fun add(value: Long, context: Context)

    /**
     * Unbinds the current bound instance from the [LongUpDownCounter].
     *
     * After this method returns the current instance is considered invalid (not being managed by
     * the instrument).
     */
    fun unbind()
}
