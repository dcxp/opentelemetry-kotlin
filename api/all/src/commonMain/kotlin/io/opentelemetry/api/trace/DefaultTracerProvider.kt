/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.trace

internal class DefaultTracerProvider private constructor() : TracerProvider {
    override operator fun get(instrumentationName: String): Tracer {
        return DefaultTracer.instance
    }

    override operator fun get(instrumentationName: String, instrumentationVersion: String): Tracer {
        return DefaultTracer.instance
    }

    companion object {
        private val INSTANCE: TracerProvider = DefaultTracerProvider()
        val instance: TracerProvider
            get() = INSTANCE
    }
}
