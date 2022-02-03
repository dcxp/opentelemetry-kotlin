/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.metrics

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context

/** An up-down-counter instrument that records `double` values. */
interface DoubleUpDownCounter {
    /**
     * Records a value.
     *
     * Note: This may use `Context.current()` to pull the context associated with this measurement.
     *
     * @param value The increment amount. May be positive, negative or zero.
     */
    fun add(value: Double)

    /**
     * Records a value with a set of attributes.
     *
     * Note: This may use `Context.current()` to pull the context associated with this measurement.
     *
     * @param value The increment amount. May be positive, negative or zero.
     * @param attributes A set of attributes to associate with the count.
     */
    fun add(value: Double, attributes: Attributes)

    /**
     * Records a value with a set of attributes.
     *
     * @param value The increment amount. May be positive, negative or zero.
     * @param attributes A set of attributes to associate with the count.
     * @param context The explicit context to associate with this measurement.
     */
    fun add(value: Double, attributes: Attributes, context: Context)

    /**
     * Constructs a bound version of this instrument where all recorded values use the given
     * attributes.
     */
    fun bind(attributes: Attributes): BoundDoubleUpDownCounter
}
