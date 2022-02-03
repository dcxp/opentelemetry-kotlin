/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.context.propagation

import io.opentelemetry.kotlin.context.Context

internal class NoopTextMapPropagator : TextMapPropagator {
    override fun fields(): Collection<String> {
        return emptyList()
    }

    override fun <C> inject(context: Context, carrier: C, setter: TextMapSetter<C>) {}

    override fun <C> extract(context: Context, carrier: C, getter: TextMapGetter<C>): Context {
        return context
    }

    companion object {
        private val INSTANCE = NoopTextMapPropagator()
        val instance: TextMapPropagator
            get() = INSTANCE
    }
}
