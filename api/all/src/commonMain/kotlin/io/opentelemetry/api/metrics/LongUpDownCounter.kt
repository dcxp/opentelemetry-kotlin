/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.metrics

import io.opentelemetry.api.common.Attributes
import io.opentelemetry.context.Context

/** An up-down-counter instrument that records `long` values. */
interface LongUpDownCounter {
    /**
     * Records a value.
     *
     * Note: This may use `Context.current()` to pull the context associated with this measurement.
     *
     * @param value The increment amount. May be positive, negative or zero.
     */
    fun add(value: Long)

    /**
     * Record a value with a set of attributes.
     *
     * Note: This may use `Context.current()` to pull the context associated with this measurement.
     *
     * @param value The increment amount. May be positive, negative or zero.
     * @param attributes A set of attributes to associate with the count.
     */
    fun add(value: Long, attributes: Attributes)

    /**
     * Records a value with a set of attributes.
     *
     * @param value The increment amount. May be positive, negative or zero.
     * @param attributes A set of attributes to associate with the count.
     * @param context The explicit context to associate with this measurement.
     */
    fun add(value: Long, attributes: Attributes, context: Context)

    /**
     * Construct a bound version of this instrument where all recorded values use the given
     * attributes.
     */
    fun bind(attributes: Attributes): BoundLongUpDownCounter
}
