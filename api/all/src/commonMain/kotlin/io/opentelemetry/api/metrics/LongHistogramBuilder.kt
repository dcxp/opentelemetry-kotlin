/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.metrics

/** Builder class for [LongHistogram]. */
interface LongHistogramBuilder {
    /**
     * Sets the description for this instrument.
     *
     * Description strings should follow the instrument description rules:
     * https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/metrics/api.md#instrument-description
     */
    fun setDescription(description: String): LongHistogramBuilder

    /**
     * Sets the unit of measure for this instrument.
     *
     * Unit strings should follow the instrument unit rules:
     * https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/metrics/api.md#instrument-unit
     */
    fun setUnit(unit: String): LongHistogramBuilder

    /** Sets the histogram for recording `double` values. */
    fun ofDoubles(): DoubleHistogramBuilder

    /**
     * Builds and returns a `LongHistogram` with the desired options.
     *
     * @return a `LongHistogram` with the desired options.
     */
    fun build(): LongHistogram
}
