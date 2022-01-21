/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api

import io.opentelemetry.api.trace.TracerProvider
import io.opentelemetry.context.propagation.ContextPropagators

/**
 * The default OpenTelemetry API, which tries to find API implementations via SPI or otherwise falls
 * back to no-op default implementations.
 */
class DefaultOpenTelemetry(override val propagators: ContextPropagators) : OpenTelemetry {

    override val tracerProvider: TracerProvider
        get() = TracerProvider.noop()

    companion object {
        private val NO_OP: OpenTelemetry = DefaultOpenTelemetry(ContextPropagators.noop())
        val noop: OpenTelemetry
            get() = NO_OP

        fun getPropagating(propagators: ContextPropagators): OpenTelemetry {
            return DefaultOpenTelemetry(propagators)
        }
    }
}
