/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.opentelemetry.kotlin.api.metrics.Meter
import io.opentelemetry.kotlin.api.metrics.MeterBuilder
import io.opentelemetry.kotlin.sdk.internal.ComponentRegistry

internal class SdkMeterBuilder(registry: ComponentRegistry<SdkMeter>, instrumentationName: String) :
    MeterBuilder {
    private val registry: ComponentRegistry<SdkMeter>
    private val instrumentationName: String
    private var instrumentationVersion: String? = null
    private var schemaUrl: String? = null

    init {
        this.registry = registry
        this.instrumentationName = instrumentationName
    }

    override fun setSchemaUrl(schemaUrl: String): MeterBuilder {
        this.schemaUrl = schemaUrl
        return this
    }

    override fun setInstrumentationVersion(instrumentationVersion: String): MeterBuilder {
        this.instrumentationVersion = instrumentationVersion
        return this
    }

    override fun build(): Meter {
        return registry[instrumentationName, instrumentationVersion, schemaUrl]
    }
}
