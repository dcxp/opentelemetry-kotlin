/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.common

/**
 * An immutable container for attributes.
 *
 * The keys are [AttributeKey]s and the values are Object instances that match the type of the
 * provided key.
 *
 * Null keys will be silently dropped.
 *
 * Note: The behavior of null-valued attributes is undefined, and hence strongly discouraged.
 *
 * Implementations of this interface *must* be immutable and have well-defined value-based
 * equals/hashCode implementations. If an implementation does not strictly conform to these
 * requirements, behavior of the OpenTelemetry APIs and default SDK cannot be guaranteed.
 *
 * For this reason, it is strongly suggested that you use the implementation that is provided here
 * via the factory methods and the [AttributesBuilder].
 */
interface Attributes {
    /** Returns the value for the given [AttributeKey], or `null` if not found. */
    operator fun <T : Any> get(key: AttributeKey<T>): T?

    /** Iterates over all the key-value pairs of attributes contained by this instance. */
    fun forEach(consumer: (AttributeKey<*>, Any) -> Unit)

    /** The number of attributes contained in this. */
    val size: Int

    /** Whether there are any attributes contained in this. */
    fun isEmpty(): Boolean

    /** Returns a read-only view of this [Attributes] as a [Map]. */
    fun asMap(): Map<AttributeKey<*>, Any>

    /** Returns a new [AttributesBuilder] instance populated with the data of this [ ]. */
    fun toBuilder(): AttributesBuilder

    companion object {
        /** Returns a [Attributes] instance with no attributes. */
        fun empty(): Attributes {
            return builder().build()
        }

        /** Returns a [Attributes] instance with a single key-value pair. */
        fun <T : Any> of(key: AttributeKey<T>, value: T): Attributes {
            return builder().put(key, value).build()
        }

        /**
         * Returns a [Attributes] instance with two key-value pairs. Order of the keys is not
         * preserved. Duplicate keys will be removed.
         */
        fun <T : Any, U : Any> of(
            key1: AttributeKey<T>,
            value1: T,
            key2: AttributeKey<U>,
            value2: U
        ): Attributes {
            return builder().put(key1, value1).put(key2, value2).build()
        }

        /**
         * Returns a [Attributes] instance with three key-value pairs. Order of the keys is not
         * preserved. Duplicate keys will be removed.
         */
        fun <T : Any, U : Any, V : Any> of(
            key1: AttributeKey<T>,
            value1: T,
            key2: AttributeKey<U>,
            value2: U,
            key3: AttributeKey<V>,
            value3: V
        ): Attributes {
            return builder().put(key1, value1).put(key2, value2).put(key3, value3).build()
        }

        /**
         * Returns a [Attributes] instance with four key-value pairs. Order of the keys is not
         * preserved. Duplicate keys will be removed.
         */
        fun <T : Any, U : Any, V : Any, W : Any> of(
            key1: AttributeKey<T>,
            value1: T,
            key2: AttributeKey<U>,
            value2: U,
            key3: AttributeKey<V>,
            value3: V,
            key4: AttributeKey<W>,
            value4: W
        ): Attributes {
            return builder()
                .put(key1, value1)
                .put(key2, value2)
                .put(key3, value3)
                .put(key4, value4)
                .build()
        }

        /**
         * Returns a [Attributes] instance with five key-value pairs. Order of the keys is not
         * preserved. Duplicate keys will be removed.
         */
        fun <T : Any, U : Any, V : Any, W : Any, X : Any> of(
            key1: AttributeKey<T>,
            value1: T,
            key2: AttributeKey<U>,
            value2: U,
            key3: AttributeKey<V>,
            value3: V,
            key4: AttributeKey<W>,
            value4: W,
            key5: AttributeKey<X>,
            value5: X
        ): Attributes {
            return builder()
                .put(key1, value1)
                .put(key2, value2)
                .put(key3, value3)
                .put(key4, value4)
                .put(key5, value5)
                .build()
        }

        /**
         * Returns a [Attributes] instance with the given key-value pairs. Order of the keys is not
         * preserved. Duplicate keys will be removed.
         */
        @kotlin.jvm.JvmStatic
        fun <T : Any, U : Any, V : Any, W : Any, X : Any, Y : Any> of(
            key1: AttributeKey<T>,
            value1: T,
            key2: AttributeKey<U>,
            value2: U,
            key3: AttributeKey<V>,
            value3: V,
            key4: AttributeKey<W>,
            value4: W,
            key5: AttributeKey<X>,
            value5: X,
            key6: AttributeKey<Y>,
            value6: Y
        ): Attributes {
            return builder()
                .put(key1, value1)
                .put(key2, value2)
                .put(key3, value3)
                .put(key4, value4)
                .put(key5, value5)
                .put(key6, value6)
                .build()
        }

        /** Returns a new [AttributesBuilder] instance for creating arbitrary [Attributes]. */
        fun builder(): AttributesBuilder {
            return ArrayBackedAttributesBuilder()
        }
    }
}
