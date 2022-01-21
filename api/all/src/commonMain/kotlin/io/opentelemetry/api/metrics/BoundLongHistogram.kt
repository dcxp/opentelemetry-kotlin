/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.metrics

import io.opentelemetry.context.Context

/** A histogram instrument that records `long` values with pre-associated attributes. */
interface BoundLongHistogram {
    /**
     * Records a value with a pre-bound set of attributes.
     *
     * Note: This may use `Context.current()` to pull the context associated with this measurement.
     *
     * @param value The amount of the measurement.
     */
    fun record(value: Long)

    /**
     * Records a value with a pre-bound set of attributes.
     *
     * @param value The amount of the measurement.
     * @param context The explicit context to associate with this measurement.
     */
    fun record(value: Long, context: Context)

    /**
     * Unbinds the current bound instance from the [LongHistogram].
     *
     * After this method returns the current instance is considered invalid (not being managed by
     * the instrument).
     */
    fun unbind()
}
