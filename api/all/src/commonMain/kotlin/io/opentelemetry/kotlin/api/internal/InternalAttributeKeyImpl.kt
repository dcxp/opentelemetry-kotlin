/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.internal

import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.common.AttributeType

/**
 * Default AttributeKey implementation which preencodes to UTF8 for OTLP export.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
class InternalAttributeKeyImpl<T>
private constructor(override val type: AttributeType, override val key: String) : AttributeKey<T> {
    private val hashCode: Int = buildHashCode(type, key)
    private val lazyKeyUtf8 = lazy<ByteArray> { key.encodeToByteArray() }
    /** Returns the key, encoded as UTF-8 bytes. */
    val keyUtf8: ByteArray
        get() {
            return lazyKeyUtf8.value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (other !is AttributeKey<*>) return false
        return AttributeKey.isEquals(this, other)
    }

    override fun hashCode(): Int {
        return hashCode
    }

    override fun toString(): String {
        return key
    }

    companion object {
        // Used by auto-instrumentation agent. Check with auto-instrumentation before making changes
        // to
        // this method.
        //
        // In particular, do not change this return type to AttributeKeyImpl because
        // auto-instrumentation
        // hijacks this method and returns a bridged implementation of Context.
        //
        // Ideally auto-instrumentation would hijack the public AttributeKey.*Key() instead of this
        // method, but auto-instrumentation also needs to inject its own implementation of
        // AttributeKey
        // into the class loader at the same time, which causes a problem because injecting a class
        // into
        // the class loader automatically resolves its super classes (interfaces), which in this
        // case is
        // Context, which would be the same class (interface) being instrumented at that time,
        // which would lead to the JVM throwing a LinkageError "attempted duplicate interface
        // definition"
        fun <T> create(key: String, type: AttributeType): AttributeKey<T> {
            return InternalAttributeKeyImpl(type, key)
        }

        // this method exists to make EqualsVerifier happy
        private fun buildHashCode(type: AttributeType, key: String): Int {
            var result = 1
            result *= 1000003
            result = result xor type.hashCode()
            result *= 1000003
            result = result xor key.hashCode()
            return result
        }
    }
}
