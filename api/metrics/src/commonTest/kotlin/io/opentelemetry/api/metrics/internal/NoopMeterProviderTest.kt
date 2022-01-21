/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.api.metrics.internal

import io.opentelemetry.api.metrics.MeterProvider
import kotlin.test.Test

class NoopMeterProviderTest {
    @Test
    fun noopMeterProvider_getDoesNotThrow() {
        val provider = MeterProvider.noop()
        provider["user-instrumentation"]
    }

    @Test
    fun noopMeterProvider_builderDoesNotThrow() {
        val provider = MeterProvider.noop()
        provider.meterBuilder("user-instrumentation").build()
        provider.meterBuilder("advanced-instrumetnation").setInstrumentationVersion("1.0").build()
        provider.meterBuilder("schema-instrumentation").setSchemaUrl("myschema://url").build()
        provider
            .meterBuilder("schema-instrumentation")
            .setInstrumentationVersion("1.0")
            .setSchemaUrl("myschema://url")
            .build()
    }
}
