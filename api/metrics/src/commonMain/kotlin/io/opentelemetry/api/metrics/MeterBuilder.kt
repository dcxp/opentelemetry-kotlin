/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.metrics

/**
 * Builder class for creating [Meter] instances.
 *
 * @since 1.4.0
 */
interface MeterBuilder {
    /**
     * Assigns an OpenTelemetry schema URL to the resulting Meter.
     *
     * @param schemaUrl The URL of the OpenTelemetry schema being used by this instrumentation
     * library.
     * @return this
     */
    fun setSchemaUrl(schemaUrl: String): MeterBuilder

    /**
     * Assigns a version to the instrumentation library that is using the resulting Meter.
     *
     * @param instrumentationVersion The version of the instrumentation library.
     * @return this
     */
    fun setInstrumentationVersion(instrumentationVersion: String): MeterBuilder

    /**
     * Gets or creates a [Meter] instance.
     *
     * @return a [Meter] instance configured with the provided options.
     */
    fun build(): io.opentelemetry.api.metrics.Meter
}
