/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.metrics

/** Builder class for [DoubleUpDownCounter]. */
interface DoubleUpDownCounterBuilder {
    /**
     * Sets the description for this instrument.
     *
     * Description strings should follow the instrument description rules:
     * https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/metrics/api.md#instrument-description
     */
    fun setDescription(description: String): DoubleUpDownCounterBuilder

    /**
     * Sets the unit of measure for this instrument.
     *
     * Unit strings should follow the instrument unit rules:
     * https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/metrics/api.md#instrument-unit
     */
    fun setUnit(unit: String): DoubleUpDownCounterBuilder

    /** Sets the counter for recording `long` values. */
    fun ofLongs(): LongUpDownCounterBuilder

    /**
     * Builds and returns a `DoubleUpDownCounter` with the desired options.
     *
     * @return a `DoubleUpDownCounter` with the desired options.
     */
    fun build(): DoubleUpDownCounter

    /**
     * Builds this asynchronous instrument with the given callback.
     *
     * The callback will only be called when the [Meter] is being observed.
     *
     * @param callback A state-capturing callback used to observe values on-demand.
     */
    fun buildWithCallback(callback: (ObservableDoubleMeasurement) -> Unit)
}
