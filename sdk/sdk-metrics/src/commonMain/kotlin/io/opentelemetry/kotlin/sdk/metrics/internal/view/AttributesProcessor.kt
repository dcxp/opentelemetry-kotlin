/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.view

import io.opentelemetry.kotlin.api.baggage.Baggage
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context

/**
 * An `AttributesProcessor` is used by `View`s to define the actual recorded set of attributes.
 *
 * An AttributesProcessor is used to define the actual set of attributes that will be used in a
 * Metric vs. the inbound set of attributes from a measurement.
 */
abstract class AttributesProcessor internal constructor() {
    /**
     * Manipulates a set of attributes, returning the desired set.
     *
     * @param incoming Attributes associated with an incoming measurement.
     * @param context The context associated with the measurement.
     */
    abstract fun process(incoming: Attributes, context: Context): Attributes

    /**
     * If true, this ensures the `Context` argument of the attributes processor is always accurate.
     * This will prevents bound instruments from pre-locking their metric-attributes and defer until
     * context is available.
     */
    abstract fun usesContext(): Boolean

    /** Joins this attribute processor with another that operates after this one. */
    open fun then(other: AttributesProcessor): AttributesProcessor {
        if (other === NOOP) {
            return this
        }
        if (this === NOOP) {
            return other
        }
        return if (other is JoinedAttributesProcessor) {
            other.prepend(this)
        } else JoinedAttributesProcessor(listOf(this, other))
    }

    /** A [AttributesProcessor] that runs a sequence of processors. */
    internal class JoinedAttributesProcessor(
        private val processors: Collection<AttributesProcessor>
    ) : AttributesProcessor() {
        private val usesContextCache: Boolean =
            processors.map { obj: AttributesProcessor -> obj.usesContext() }.reduce {
                l: Boolean,
                r: Boolean ->
                l || r
            }

        override fun process(incoming: Attributes, context: Context): Attributes {
            var result = incoming
            for (processor in processors) {
                result = processor.process(result, context)
            }
            return result
        }

        override fun usesContext(): Boolean {
            return usesContextCache
        }

        override fun then(other: AttributesProcessor): AttributesProcessor {
            val newList: MutableList<AttributesProcessor> = processors.toMutableList()
            if (other is JoinedAttributesProcessor) {
                newList.addAll(other.processors)
            } else {
                newList.add(other)
            }
            return JoinedAttributesProcessor(newList)
        }

        fun prepend(other: AttributesProcessor): AttributesProcessor {
            val newList: MutableList<AttributesProcessor> = mutableListOf()
            newList.add(other)
            newList.addAll(processors)
            return JoinedAttributesProcessor(newList)
        }
    }

    companion object {
        /** No-op version of attributes processor, returns what it gets. */
        fun noop(): AttributesProcessor {
            return NOOP
        }

        /**
         * Creates a processor which filters down attributes from a measurement.
         *
         * @param nameFilter a filter for which attribute keys to preserve.
         */
        fun filterByKeyName(nameFilter: (String) -> Boolean): AttributesProcessor {
            return simple { incoming: Attributes ->
                incoming
                    .toBuilder()
                    .removeIf { attributeKey -> !nameFilter(attributeKey.key) }
                    .build()
            }
        }

        /**
         * Creates a processor which appends values from [Baggage].
         *
         * These attributes will not override those attributes provided by instrumentation.
         *
         * @param nameFilter a filter for which baggage keys to select.
         */
        fun appendBaggageByKeyName(nameFilter: (String) -> Boolean): AttributesProcessor {
            return onBaggage { incoming: Attributes, baggage: Baggage ->
                val result = Attributes.builder()
                baggage.forEach { entity ->
                    if (nameFilter(entity.key)) {
                        result.put(entity.key, entity.value.value)
                    }
                }
                // Override any baggage keys with existing keys.
                result.putAll(incoming)
                result.build()
            }
        }

        /**
         * Creates a processor which appends (exactly) the given attributes.
         *
         * These attributes will not override those attributes provided by instrumentation.
         *
         * @param attributes Attributes to append to measurements.
         */
        fun append(attributes: Attributes): AttributesProcessor {
            return simple { incoming: Attributes ->
                attributes.toBuilder().putAll(incoming).build()
            }
        }

        /** Creates a simple attributes processor with no access to context. */
        fun simple(processor: (Attributes) -> Attributes): AttributesProcessor {
            return object : AttributesProcessor() {
                override fun process(incoming: Attributes, context: Context): Attributes {
                    return processor(incoming)
                }

                override fun usesContext(): Boolean {
                    return false
                }
            }
        }

        /** Creates an Attributes processor that has access to baggage. */
        fun onBaggage(processor: (Attributes, Baggage) -> Attributes): AttributesProcessor {
            return object : AttributesProcessor() {
                override fun process(incoming: Attributes, context: Context): Attributes {
                    return processor(incoming, Baggage.fromContext(context))
                }

                override fun usesContext(): Boolean {
                    return true
                }
            }
        }

        val NOOP = simple { incoming: Attributes -> incoming }
    }
}
