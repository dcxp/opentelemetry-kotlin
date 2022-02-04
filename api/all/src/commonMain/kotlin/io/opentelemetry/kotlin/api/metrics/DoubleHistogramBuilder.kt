/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.metrics

/** Builder class for [DoubleHistogram]. */
interface DoubleHistogramBuilder {
    /**
     * Sets the description for this instrument.
     *
     * Description strings should follow the instrument description rules:
     * https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/metrics/api.md#instrument-description
     */
    fun setDescription(description: String): DoubleHistogramBuilder

    /**
     * Sets the unit of measure for this instrument.
     *
     * Unit strings should follow the instrument unit rules:
     * https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/metrics/api.md#instrument-unit
     */
    fun setUnit(unit: String): DoubleHistogramBuilder

    /** Sets the counter for recording `long` values. */
    fun ofLongs(): LongHistogramBuilder

    /**
     * Builds and returns a `DoubleHistogram` with the desired options.
     *
     * @return a `DoubleHistogram` with the desired options.
     */
    fun build(): DoubleHistogram
}
