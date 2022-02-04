/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.baggage

/**
 * A builder of [Baggage].
 *
 * @see Baggage.builder
 */
interface BaggageBuilder {
    /**
     * Adds the key/value pair and metadata regardless of whether the key is present.
     *
     * @param key the `String` key which will be set.
     * @param value the `String` value to set for the given key.
     * @param entryMetadata the `BaggageEntryMetadata` metadata to set for the given key.
     * @return this
     */
    /**
     * Adds the key/value pair with empty metadata regardless of whether the key is present.
     *
     * @param key the `String` key which will be set.
     * @param value the `String` value to set for the given key.
     * @return this
     */
    fun put(
        key: String,
        value: String,
        entryMetadata: BaggageEntryMetadata = BaggageEntryMetadata.empty()
    ): BaggageBuilder

    /**
     * Removes the key if it exists.
     *
     * @param key the `String` key which will be removed.
     * @return this
     */
    fun remove(key: String?): BaggageBuilder

    /**
     * Creates a `Baggage` from this builder.
     *
     * @return a `Baggage` with the same entries as this builder.
     */
    fun build(): Baggage
}
