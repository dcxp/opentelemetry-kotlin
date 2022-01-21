/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.context.propagation

import io.opentelemetry.context.Context

internal class MultiTextMapPropagator(private val textPropagators: List<TextMapPropagator>) :
    TextMapPropagator {
    private val allFields: Collection<String> = getAllFields(this.textPropagators)

    constructor(
        vararg textPropagators: TextMapPropagator?
    ) : this(textPropagators.filterNotNull().toList())

    override fun fields(): Collection<String> {
        return allFields
    }

    override fun <C> inject(context: Context, carrier: C, setter: TextMapSetter<C>) {
        for (textPropagator in textPropagators) {
            textPropagator.inject(context, carrier, setter)
        }
    }

    override fun <C> extract(context: Context, carrier: C, getter: TextMapGetter<C>): Context {
        var processedContext = context
        for (textPropagator in textPropagators) {
            processedContext = textPropagator.extract(processedContext, carrier, getter)
        }
        return processedContext
    }

    companion object {
        private fun getAllFields(textPropagators: List<TextMapPropagator>): Collection<String> {
            val fields: MutableSet<String> = LinkedHashSet()
            for (textPropagator in textPropagators) {
                fields.addAll(textPropagator.fields())
            }
            return fields.toHashSet()
        }
    }
}
