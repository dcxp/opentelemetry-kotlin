/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.trace

/**
 * A builder of [TraceState]. This implementation does full validation of the keys and values in the
 * entries, and will ignore any entries that do not conform to the W3C specification.
 */
interface TraceStateBuilder {
    /**
     * Adds or updates the `Entry` that has the given `key` if it is present. The new `Entry` will
     * always be added in the front of the list of entries.
     *
     * @param key the key for the `Entry` to be added.
     * @param value the value for the `Entry` to be added.
     * @return this.
     */
    fun put(key: String, value: String): TraceStateBuilder

    /**
     * Removes the `Entry` that has the given `key` if it is present.
     *
     * @param key the key for the `Entry` to be removed.
     * @return this.
     */
    fun remove(key: String): TraceStateBuilder

    /**
     * Builds a TraceState by adding the entries to the parent in front of the key-value pairs list
     * and removing duplicate entries.
     *
     * @return a TraceState with the new entries.
     */
    fun build(): TraceState
}
