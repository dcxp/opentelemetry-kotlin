/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.trace

/**
 * Builder class for creating [Tracer] instances.
 *
 * @since 1.4.0
 */
interface TracerBuilder {
    /**
     * Assign an OpenTelemetry schema URL to the resulting Tracer.
     *
     * @param schemaUrl The URL of the OpenTelemetry schema being used by this instrumentation
     * library.
     * @return this
     */
    fun setSchemaUrl(schemaUrl: String?): TracerBuilder

    /**
     * Assign a version to the instrumentation library that is using the resulting Tracer.
     *
     * @param instrumentationVersion The version of the instrumentation library.
     * @return this
     */
    fun setInstrumentationVersion(instrumentationVersion: String?): TracerBuilder

    /**
     * Gets or creates a [Tracer] instance.
     *
     * @return a [Tracer] instance configured with the provided options.
     */
    fun build(): Tracer
}
