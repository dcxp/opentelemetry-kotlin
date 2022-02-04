/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.context.propagation

/**
 * Interface that allows a `TextMapPropagator` to read propagated fields from a carrier.
 *
 * `Getter` is stateless and allows to be saved as a constant to avoid runtime allocations.
 *
 * @param <C> carrier of propagation fields, such as an http request. </C>
 */
interface TextMapGetter<C> {
    /**
     * Returns all the keys in the given carrier.
     *
     * @param carrier carrier of propagation fields, such as an http request.
     * @since 0.10.0
     */
    fun keys(carrier: C): Iterable<String?>

    /**
     * Returns the first value of the given propagation `key` or returns `null`.
     *
     * @param carrier carrier of propagation fields, such as an http request.
     * @param key the key of the field.
     * @return the first value of the given propagation `key` or returns `null`.
     */
    operator fun get(carrier: C, key: String): String?

    fun tryGet(carrier: C, key: String, callback: (String) -> Unit) {
        val value = this[carrier, key]
        if (value != null) {
            callback(value)
        }
    }

    fun getOrElse(carrier: C, key: String, otherValue: String): String {
        var internalValue = otherValue
        tryGet(carrier, key) { internalValue = it }
        return internalValue
    }

    fun getOrElse(carrier: C, key: String, otherValueFactory: () -> String): String {
        var internalValue: String? = null
        tryGet(carrier, key) { internalValue = it }
        if (internalValue == null) {
            internalValue = otherValueFactory()
        }
        return internalValue!!
    }
}
