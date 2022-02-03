/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.common.AttributesBuilder
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.getAndUpdate
import kotlinx.collections.immutable.toPersistentMap

/**
 * A map with a fixed capacity that drops attributes when the map gets full, and which truncates
 * string and array string attribute values to the [.lengthLimit].
 */
internal class AttributesMap(
    private val capacity: Long,
    private val lengthLimit: Int,
    attributeMap: Map<AttributeKey<*>, Any>
) : Attributes {
    private val internalMap = atomic(attributeMap.toPersistentMap())
    private val totalAddedValuesIntern = atomic(0)

    constructor(
        capacity: Int,
        lengthLimit: Int,
        attributeMap: Map<AttributeKey<*>, Any> = mutableMapOf()
    ) : this(capacity.toLong(), lengthLimit, attributeMap)

    fun put(key: AttributeKey<*>, value: Any): Any? {
        totalAddedValuesIntern.incrementAndGet()
        if (size >= capacity && !internalMap.value.containsKey(key)) {
            return null
        }
        return internalMap.getAndUpdate {
            it.put(key, AttributeUtil.applyAttributeLengthLimit(value, lengthLimit))
        }
    }

    val totalAddedValues: Int
        get() {
            return totalAddedValuesIntern.value
        }

    override fun <T : Any> get(key: AttributeKey<T>): T? {
        val value = internalMap.value[key] ?: return null
        return value as T
    }
    operator fun <T : Any> set(key: AttributeKey<T>, value: T) {
        put(key, value)
    }
    override fun forEach(consumer: (AttributeKey<*>, Any) -> Unit) {
        internalMap.value.forEach { consumer(it.key, it.value) }
    }

    override val size: Int
        get() = internalMap.value.size

    override fun isEmpty(): Boolean {
        return internalMap.value.isEmpty()
    }

    override fun asMap(): Map<AttributeKey<*>, Any> {
        return internalMap.value
    }

    override fun toBuilder(): AttributesBuilder {
        return Attributes.builder().putAll(this)
    }

    override fun toString(): String {
        return ("AttributesMap{" +
            "data=" +
            super.toString() +
            ", capacity=" +
            capacity +
            ", totalAddedValues=" +
            totalAddedValues +
            '}')
    }

    fun immutableCopy(): Attributes {
        return Attributes.builder().putAll(this).build()
    }
}
