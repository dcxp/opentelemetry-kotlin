/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.context.propagation

/**
 * `DefaultContextPropagators` is the default, built-in implementation of [ ].
 *
 * All the registered propagators are stored internally as a simple list, and are invoked
 * synchronically upon injection and extraction.
 *
 * The propagation fields retrieved from all registered propagators are de-duplicated.
 */
internal class DefaultContextPropagators(override val textMapPropagator: TextMapPropagator) :
    ContextPropagators {

    companion object {
        private val NOOP: ContextPropagators =
            DefaultContextPropagators(NoopTextMapPropagator.instance)

        fun noop(): ContextPropagators {
            return NOOP
        }
    }
}
