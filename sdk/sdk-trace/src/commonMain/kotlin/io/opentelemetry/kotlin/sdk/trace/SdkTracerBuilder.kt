/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.opentelemetry.kotlin.api.trace.Tracer
import io.opentelemetry.kotlin.api.trace.TracerBuilder
import io.opentelemetry.kotlin.sdk.internal.ComponentRegistry

internal class SdkTracerBuilder(
    private val registry: ComponentRegistry<SdkTracer>,
    private val instrumentationName: String
) : TracerBuilder {

    private var instrumentationVersion: String? = null

    private var schemaUrl: String? = null

    override fun setSchemaUrl(schemaUrl: String?): TracerBuilder {
        this.schemaUrl = schemaUrl
        return this
    }

    override fun setInstrumentationVersion(instrumentationVersion: String?): TracerBuilder {
        this.instrumentationVersion = instrumentationVersion
        return this
    }

    override fun build(): Tracer {
        return registry[instrumentationName, instrumentationVersion, schemaUrl]
    }
}
