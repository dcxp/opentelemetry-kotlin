/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.common

import io.opentelemetry.kotlin.api.internal.ImmutableKeyValuePairs

internal class ArrayBackedAttributes : ImmutableKeyValuePairs<AttributeKey<*>, Any>, Attributes {
    private constructor(
        data: Array<Any?>,
        keyComparator: Comparator<AttributeKey<*>>
    ) : super(data, keyComparator)

    /**
     * Only use this constructor if you can guarantee that the data has been de-duped, sorted by key
     * and contains no null values or null/empty keys.
     *
     * @param data the raw data
     */
    constructor(data: Array<Any>) : super(data)

    override fun toBuilder(): AttributesBuilder {
        return ArrayBackedAttributesBuilder(data())
    }

    @Suppress("UNCHECKED_CAST")
    override operator fun <T : Any> get(key: AttributeKey<T>): T? {
        val value = super.get(key)
        return value as T?
    }

    companion object {
        // We only compare the key name, not type, when constructing, to allow deduping keys with
        // the
        // same name but different type.
        val EMPTY: Attributes = Attributes.builder().build()

        fun sortAndFilterToAttributes(array: Array<Any?>): Attributes {
            var i = 0
            while (i < array.size) {
                if (array[i] != null) {
                    val key: AttributeKey<*> = array[i] as AttributeKey<*>
                    if (key.key.isEmpty()) {
                        array[i] = null
                    }
                }
                i += 2
            }
            return ArrayBackedAttributes(array, AttributeKey.Companion.KeyOnlyComparator)
        }
    }
}
