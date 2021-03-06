/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.metrics

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context

/** A histogram instrument that records `long` values. */
interface LongHistogram {
    /**
     * Records a value.
     *
     * Note: This may use `Context.current()` to pull the context associated with this measurement.
     *
     * @param value The amount of the measurement.
     */
    fun record(value: Long)

    /**
     * Records a value with a set of attributes.
     *
     * Note: This may use `Context.current()` to pull the context associated with this measurement.
     *
     * @param value The amount of the measurement.
     * @param attributes A set of attributes to associate with the count.
     */
    fun record(value: Long, attributes: Attributes)

    /**
     * Records a value with a set of attributes.
     *
     * @param value The amount of the measurement.
     * @param attributes A set of attributes to associate with the count.
     * @param context The explicit context to associate with this measurement.
     */
    fun record(value: Long, attributes: Attributes, context: Context)

    /**
     * Construct a bound version of this instrument where all recorded values use the given
     * attributes.
     */
    fun bind(attributes: Attributes): BoundLongHistogram
}
