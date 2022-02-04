/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.baggage

/** String-String key-value pair, along with [ImmutableEntryMetadata]. */
internal class ImmutableEntry(
    override val value: String,
    override val metadata: BaggageEntryMetadata
) : BaggageEntry {
    companion object {
        /**
         * Creates an `Entry` from the given key, value and metadata.
         *
         * @param value the entry value.
         * @param entryMetadata the entry metadata.
         * @return a `Entry`.
         */
        fun create(value: String, entryMetadata: BaggageEntryMetadata): ImmutableEntry {
            return ImmutableEntry(value, entryMetadata)
        }
    }
}
