/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.baggage

import io.opentelemetry.kotlin.api.internal.ImmutableKeyValuePairs

internal class ImmutableBaggage private constructor(data: Array<Any?>) :
    ImmutableKeyValuePairs<String, BaggageEntry>(data, naturalOrder<Comparable<Any>>()), Baggage {

    override fun isEmpty(): Boolean {
        return size == 0
    }

    override fun forEach(consumer: (Map.Entry<String, BaggageEntry>) -> Unit) {
        forEach { s, baggageEntry ->
            val entity =
                object : Map.Entry<String, BaggageEntry> {
                    override val key: String
                        get() = s
                    override val value: BaggageEntry
                        get() = baggageEntry
                }
            consumer(entity)
        }
    }

    override fun getEntryValue(entryKey: String): String? {
        return get(entryKey)?.value
    }

    override fun toBuilder(): BaggageBuilder {
        return Builder(data().toMutableList())
    }

    internal class Builder(private val data: MutableList<Any?> = mutableListOf()) : BaggageBuilder {

        override fun put(
            key: String,
            value: String,
            entryMetadata: BaggageEntryMetadata
        ): BaggageBuilder {
            if (!isKeyValid(key) || !isValueValid(value)) {
                return this
            }
            data.add(key)
            data.add(ImmutableEntry.create(value, entryMetadata))
            return this
        }

        override fun remove(key: String?): BaggageBuilder {
            if (key == null) {
                return this
            }
            data.add(key)
            data.add(null)
            return this
        }

        override fun build(): Baggage {
            return sortAndFilterToBaggage(data.toTypedArray())
        }
    }

    companion object {
        private val EMPTY: Baggage = Builder().build()
        fun empty(): Baggage {
            return EMPTY
        }

        fun builder(): BaggageBuilder {
            return Builder()
        }

        private fun sortAndFilterToBaggage(data: Array<Any?>): Baggage {
            return ImmutableBaggage(data)
        }

        /**
         * Determines whether the given `String` is a valid entry key.
         *
         * @param name the entry key name to be validated.
         * @return whether the name is valid.
         */
        private fun isKeyValid(name: String?): Boolean {
            return name != null && name.isNotEmpty()
        }

        /**
         * Determines whether the given `String` is a valid entry value.
         *
         * @param value the entry value to be validated.
         * @return whether the value is valid.
         */
        private fun isValueValid(value: String?): Boolean {
            return value != null
        }
    }
}
