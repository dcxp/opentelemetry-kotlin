/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.metrics

/**
 * Provides instruments used to produce metrics.
 *
 * Instruments are obtained through builders provided by this interface. Each builder has a default
 * "type" associated with recordings that may be changed.
 *
 * A Meter is generally associated with an instrumentation library, e.g. "I monitor apache
 * httpclient".
 */
interface Meter {
    /**
     * Constructs a counter instrument.
     *
     * This is used to build both synchronous (in-context) instruments and asynchronous (callback)
     * instruments.
     *
     * @param name the name used for the counter.
     * @return a builder for configuring a new Counter instrument. Defaults to recording long
     * values, but may be changed.
     */
    fun counterBuilder(name: String): LongCounterBuilder

    /**
     * Constructs an up-down-counter instrument.
     *
     * This is used to build both synchronous (in-context) instruments and asynchronous (callback)
     * instruments.
     *
     * @param name the name used for the counter.
     * @return a builder for configuring a new Counter synchronous instrument. Defaults to recording
     * long values, but may be changed.
     */
    fun upDownCounterBuilder(name: String): LongUpDownCounterBuilder

    /**
     * Constructs a Histogram instrument.
     *
     * @param name the name used for the counter.
     * @return a builder for configuring a new Histogram synchronous instrument. Defaults to
     * recording double values, but may be changed.
     */
    fun histogramBuilder(name: String): DoubleHistogramBuilder

    /**
     * Constructs an asynchronous gauge.
     *
     * @return a builder used for configuring how to report gauge measurements on demand.
     */
    fun gaugeBuilder(name: String): DoubleGaugeBuilder
}
