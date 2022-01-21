/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.trace

internal class DefaultTracerBuilder : TracerBuilder {
    override fun setSchemaUrl(schemaUrl: String?): TracerBuilder {
        return this
    }

    override fun setInstrumentationVersion(instrumentationVersion: String?): TracerBuilder {
        return this
    }

    override fun build(): Tracer {
        return DefaultTracer.instance
    }

    companion object {
        private val INSTANCE = DefaultTracerBuilder()

        val instance: TracerBuilder
            get() = INSTANCE
    }
}
