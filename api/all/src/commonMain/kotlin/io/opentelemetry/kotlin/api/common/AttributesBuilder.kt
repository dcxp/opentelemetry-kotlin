/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.common

import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.booleanArrayKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.booleanKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.doubleArrayKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.doubleKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.longArrayKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.longKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.stringArrayKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.stringKey

/** A builder of [Attributes] supporting an arbitrary number of key-value pairs. */
interface AttributesBuilder {
    /** Create the [Attributes] from this. */
    fun build(): Attributes

    /** Puts a [AttributeKey] with associated value into this. */
    fun <T : Any> put(key: AttributeKey<T>, value: T): AttributesBuilder

    /**
     * Puts a String attribute into this.
     *
     * Note: It is strongly recommended to use [.put], and pre-allocate your keys, if possible.
     *
     * @return this Builder
     */
    fun put(key: String, value: String): AttributesBuilder {
        return put(stringKey(key), value)
    }

    /**
     * Puts a long attribute into this.
     *
     * Note: It is strongly recommended to use [.put], and pre-allocate your keys, if possible.
     *
     * @return this Builder
     */
    fun put(key: String, value: Long): AttributesBuilder {
        return put(longKey(key), value)
    }

    /**
     * Puts a double attribute into this.
     *
     * Note: It is strongly recommended to use [.put], and pre-allocate your keys, if possible.
     *
     * @return this Builder
     */
    fun put(key: String, value: Double): AttributesBuilder {
        return put(doubleKey(key), value)
    }

    /**
     * Puts a boolean attribute into this.
     *
     * Note: It is strongly recommended to use [.put], and pre-allocate your keys, if possible.
     *
     * @return this Builder
     */
    fun put(key: String, value: Boolean): AttributesBuilder {
        return put(booleanKey(key), value)
    }

    /**
     * Puts a String array attribute into this.
     *
     * Note: It is strongly recommended to use [.put], and pre-allocate your keys, if possible.
     *
     * @return this Builder
     */
    fun put(key: String, vararg value: String): AttributesBuilder {
        return put(stringArrayKey(key), value.toList())
    }

    /**
     * Puts a Long array attribute into this.
     *
     * Note: It is strongly recommended to use [.put], and pre-allocate your keys, if possible.
     *
     * @return this Builder
     */
    fun put(key: String, vararg value: Long): AttributesBuilder {
        return put(longArrayKey(key), value.toList())
    }

    /**
     * Puts a Double array attribute into this.
     *
     * Note: It is strongly recommended to use [.put], and pre-allocate your keys, if possible.
     *
     * @return this Builder
     */
    fun put(key: String, vararg value: Double): AttributesBuilder {
        return put(doubleArrayKey(key), value.toList())
    }

    /**
     * Puts a Boolean array attribute into this.
     *
     * Note: It is strongly recommended to use [.put], and pre-allocate your keys, if possible.
     *
     * @return this Builder
     */
    fun put(key: String, vararg value: Boolean): AttributesBuilder {
        return put(booleanArrayKey(key), value.toList())
    }

    /**
     * Puts all the provided attributes into this Builder.
     *
     * @return this Builder
     */
    fun putAll(attributes: Attributes): AttributesBuilder

    /**
     * Remove all attributes where [AttributeKey.getKey] and [AttributeKey.getType] match the `key`.
     *
     * @return this Builder
     */
    fun <T> remove(key: io.opentelemetry.kotlin.api.common.AttributeKey<T>): AttributesBuilder {
        // default implementation is no-op
        return this
    }

    /**
     * Remove all attributes that satisfy the given predicate. Errors or runtime exceptions thrown
     * by the predicate are relayed to the caller.
     *
     * @return this Builder
     */
    fun removeIf(filter: (AttributeKey<*>) -> Boolean): AttributesBuilder {
        // default implementation is no-op
        return this
    }
}
