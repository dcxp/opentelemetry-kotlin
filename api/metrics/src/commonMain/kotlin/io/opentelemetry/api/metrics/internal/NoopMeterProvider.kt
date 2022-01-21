/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.metrics.internal

import io.opentelemetry.api.metrics.Meter
import io.opentelemetry.api.metrics.MeterBuilder
import io.opentelemetry.api.metrics.MeterProvider

/** A [MeterProvider] that does nothing. */
class NoopMeterProvider private constructor() : MeterProvider {
    override fun meterBuilder(instrumentationName: String): MeterBuilder {
        return BUILDER_INSTANCE
    }

    private class NoopMeterBuilder : MeterBuilder {
        override fun setSchemaUrl(schemaUrl: String): MeterBuilder {
            return this
        }

        override fun setInstrumentationVersion(instrumentationVersion: String): MeterBuilder {
            return this
        }

        override fun build(): Meter {
            return NoopMeter.instance
        }
    }

    companion object {
        private val INSTANCE = NoopMeterProvider()
        private val BUILDER_INSTANCE: MeterBuilder = NoopMeterBuilder()
        val instance: MeterProvider
            get() = INSTANCE
    }
}
