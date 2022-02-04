/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.metrics

import io.opentelemetry.kotlin.api.common.Attributes

/** An interface for observing measurements with `long` values. */
interface ObservableLongMeasurement : ObservableMeasurement {
    /**
     * Records a measurement.
     *
     * @param value The measurement amount. MUST be non-negative.
     */
    fun observe(value: Long)

    /**
     * Records a measurement with a set of attributes.
     *
     * @param value The measurement amount. MUST be non-negative.
     * @param attributes A set of attributes to associate with the count.
     */
    fun observe(value: Long, attributes: Attributes)
}
