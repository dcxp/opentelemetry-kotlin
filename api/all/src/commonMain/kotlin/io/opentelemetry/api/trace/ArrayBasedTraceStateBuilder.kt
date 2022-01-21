/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.trace

internal class ArrayBasedTraceStateBuilder : TraceStateBuilder {
    private val entries: MutableList<String>

    constructor() {
        entries = mutableListOf()
    }

    constructor(parent: ArrayBasedTraceState) {
        entries = parent.entries.toMutableList()
    }

    /**
     * Allows key value pairs to be added to the TraceState.
     *
     * @param key is an opaque string up to 256 characters printable. It MUST begin with a lowercase
     * letter, and can only contain lowercase letters a-z, digits 0-9, underscores _, dashes -,
     * asterisks *, and forward slashes /. For multi-tenant vendor scenarios, an at sign (@) can be
     * used to prefix the vendor name. The tenant id (before the '@') is limited to 240 characters
     * and the vendor id is limited to 13 characters. If in the multi-tenant vendor format, then the
     * first character may additionally be numeric.
     */
    override fun put(key: String, value: String): io.opentelemetry.api.trace.TraceStateBuilder {
        if (!isKeyValid(key) || !isValueValid(value) || entries.size >= MAX_KEY_VALUE_PAIRS) {
            return this
        }
        removeEntry(key)
        // Inserts the element at the front of this list. (note: probably pretty inefficient with an
        // ArrayList as the underlying implementation!)
        entries.add(0, key)
        entries.add(1, value)
        return this
    }

    override fun remove(key: String): TraceStateBuilder {
        removeEntry(key)
        return this
    }

    private fun removeEntry(key: String) {
        val currentSize = entries.size
        var i = 0
        while (i < currentSize) {
            if (entries[i] == key) {
                // remove twice at i to get the key & the value (yes, this is pretty ugly).
                entries.removeAt(i)
                entries.removeAt(i)
                // Exit now because the entries list cannot contain duplicates.
                break
            }
            i += 2
        }
    }

    override fun build(): TraceState {
        return ArrayBasedTraceState.create(entries)
    }

    companion object {
        private const val MAX_VENDOR_ID_SIZE = 13

        // Needs to be in this class to avoid initialization deadlock because super class depends on
        // subclass (the auto-value generate class).
        private val EMPTY: ArrayBasedTraceState = ArrayBasedTraceState.create(emptyList<String>())
        private const val MAX_KEY_VALUE_PAIRS = 32
        private const val KEY_MAX_SIZE = 256
        private const val VALUE_MAX_SIZE = 256
        private const val MAX_TENANT_ID_SIZE = 240
        fun empty(): io.opentelemetry.api.trace.TraceState {
            return EMPTY
        }

        /**
         * Checks the validity of a key.
         *
         * @param key is an opaque string up to 256 characters printable. It MUST begin with a
         * lowercase letter, and can only contain lowercase letters a-z, digits 0-9, underscores _,
         * dashes -, asterisks *, and forward slashes /. For multi-tenant vendor scenarios, an at
         * sign (@) can be used to prefix the vendor name. The tenant id (before the '@') is limited
         * to 240 characters and the vendor id is limited to 13 characters. If in the multi-tenant
         * vendor format, then the first character may additionally be numeric.
         * @return boolean representing key validity
         */
        // todo: benchmark this implementation
        private fun isKeyValid(key: String?): Boolean {
            if (key == null) {
                return false
            }
            if (key.length > KEY_MAX_SIZE || key.isEmpty() || isNotLowercaseLetterOrDigit(key[0])) {
                return false
            }
            var isMultiTenantVendorKey = false
            for (i in 1 until key.length) {
                val c = key[i]
                if (isNotLegalKeyCharacter(c)) {
                    return false
                }
                if (c == '@') {
                    // you can't have 2 '@' signs
                    if (isMultiTenantVendorKey) {
                        return false
                    }
                    isMultiTenantVendorKey = true
                    // tenant id (the part to the left of the '@' sign) must be 240 characters or
                    // less
                    if (i > MAX_TENANT_ID_SIZE) {
                        return false
                    }
                    // vendor id (the part to the right of the '@' sign) must be 1-13 characters
                    // long
                    val remainingKeyChars = key.length - i - 1
                    if (remainingKeyChars > MAX_VENDOR_ID_SIZE || remainingKeyChars == 0) {
                        return false
                    }
                }
            }
            return if (!isMultiTenantVendorKey) {
                // if it's not the vendor format (with an '@' sign), the key must start with a
                // letter.
                isNotDigit(key[0])
            } else true
        }

        private fun isNotLegalKeyCharacter(c: Char): Boolean {
            return (isNotLowercaseLetterOrDigit(c) &&
                c != '_' &&
                c != '-' &&
                c != '@' &&
                c != '*' &&
                c != '/')
        }

        private fun isNotLowercaseLetterOrDigit(ch: Char): Boolean {
            return (ch < 'a' || ch > 'z') && isNotDigit(ch)
        }

        private fun isNotDigit(ch: Char): Boolean {
            return ch < '0' || ch > '9'
        }

        // Value is opaque string up to 256 characters printable ASCII RFC0020 characters (i.e., the
        // range
        // 0x20 to 0x7E) except comma , and =.
        private fun isValueValid(value: String): Boolean {
            if (value.isNullOrEmpty()) {
                return false
            }
            if (value.length > VALUE_MAX_SIZE || value[value.length - 1] == ' ' /* '\u0020' */) {
                return false
            }
            for (i in 0 until value.length) {
                val c = value[i]
                if (c == ',' || c == '=' || c < ' ' /* '\u0020' */ || c > '~' /* '\u007E' */) {
                    return false
                }
            }
            return true
        }
    }
}
