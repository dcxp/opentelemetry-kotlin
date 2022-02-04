/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.export

/** A constructor of [MetricReader]s. */
fun interface MetricReaderFactory {
    /**
     * Construct a new MetricReader.
     *
     * @param producer the mechanism of reading SDK metrics.
     * @return a controller for this metric reader.
     */
    fun apply(producer: MetricProducer): MetricReader
}
