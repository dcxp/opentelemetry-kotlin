/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.common

internal class ArrayBackedAttributesBuilder : AttributesBuilder {
    private val data: MutableList<Any?>

    constructor() {
        data = mutableListOf()
    }

    constructor(data: List<Any>) {
        this.data = data.toMutableList()
    }

    override fun build(): Attributes {
        // If only one key-value pair AND the entry hasn't been set to null (by
        // #remove(AttributeKey<T>)
        // or #removeIf(Predicate<AttributeKey<?>>)), then we can bypass sorting and filtering
        val array = data.toTypedArray()
        return ArrayBackedAttributes.sortAndFilterToAttributes(array)
    }

    override fun <T : Any> put(key: AttributeKey<T>, value: T): AttributesBuilder {
        return putIntern(key as AttributeKey<*>, value)
    }

    private fun putIntern(key: AttributeKey<*>, value: Any): AttributesBuilder {
        if (key.key.isEmpty()) {
            return this
        }
        data.add(key)
        data.add(value)
        return this
    }

    override fun putAll(attributes: Attributes): AttributesBuilder {
        attributes.forEach { key, value -> putIntern(key, value) }
        return this
    }

    override fun <T> remove(key: AttributeKey<T>): AttributesBuilder {
        return if (key.key.isEmpty()) {
            this
        } else removeIf { entryKey: AttributeKey<*> -> AttributeKey.isEquals(key, entryKey) }
    }

    override fun removeIf(filter: (AttributeKey<*>) -> Boolean): AttributesBuilder {
        var i = 0
        while (i < data.size - 1) {
            val entry = data[i]
            if (entry is AttributeKey<*> && filter(entry)) {
                // null items are filtered out in ArrayBackedAttributes
                data[i] = null
                data[i + 1] = null
            }
            i += 2
        }
        return this
    }
}
