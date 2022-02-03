/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.common.Attributes

internal object AttributeUtil {
    /**
     * Apply the `countLimit` and `lengthLimit` to the attributes.
     *
     * If all attributes fall within the limits, return as is. Else, return an attributes instance
     * with the limits applied. `countLimit` limits the number of unique attribute keys.
     * `lengthLimit` limits the length of attribute string and string list values.
     */
    fun applyAttributesLimit(
        attributes: Attributes,
        countLimit: Int,
        lengthLimit: Int
    ): Attributes {
        if (attributes.isEmpty() || attributes.size <= countLimit) {
            if (lengthLimit == Int.MAX_VALUE) {
                return attributes
            }
            val allValidLength =
                allMatch(attributes.asMap().values) { value: Any ->
                    isValidLength(value, lengthLimit)
                }
            if (allValidLength) {
                return attributes
            }
        }
        val result = Attributes.builder()
        var i = 0
        for ((key, value) in attributes.asMap()) {
            if (i >= countLimit) {
                break
            }
            result.put(key as AttributeKey<Any>, applyAttributeLengthLimit(value, lengthLimit))
            i++
        }
        return result.build()
    }

    private fun isValidLength(value: Any, lengthLimit: Int): Boolean {
        if (value is List<*>) {
            return allMatch(value as List<Any>) { entry: Any -> isValidLength(entry, lengthLimit) }
        } else if (value is String) {
            return value.length < lengthLimit
        }
        return true
    }

    private fun <T> allMatch(iterable: Iterable<T>, predicate: (T) -> Boolean): Boolean {
        for (value in iterable) {
            if (!predicate(value)) {
                return false
            }
        }
        return true
    }

    /**
     * Apply the `lengthLimit` to the attribute `value`. Strings and strings in lists which exceed
     * the length limit are truncated.
     */
    fun applyAttributeLengthLimit(value: Any, lengthLimit: Int): Any {
        if (lengthLimit == Int.MAX_VALUE) {
            return value
        }
        if (value is List<*>) {
            val values = value as List<Any>
            return values.map { entry -> applyAttributeLengthLimit(entry, lengthLimit) }.toList()
        }
        if (value is String) {
            return if (value.length < lengthLimit) value else value.substring(0, lengthLimit)
        }
        return value
    }
}
