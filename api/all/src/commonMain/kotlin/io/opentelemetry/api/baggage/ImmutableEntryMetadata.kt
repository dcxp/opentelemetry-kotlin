/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.baggage

internal interface ImmutableEntryMetadata : BaggageEntryMetadata {
    /**
     * Returns the String value of this [ImmutableEntryMetadata].
     *
     * @return the raw metadata value.
     */
    override val value: String

    companion object {
        /** Returns an empty metadata. */
        val EMPTY = create("")

        /**
         * Creates an [ImmutableEntryMetadata] with the given value.
         *
         * @param metadata TTL of an `Entry`.
         * @return an `EntryMetadata`.
         */
        fun create(metadata: String?): ImmutableEntryMetadata {
            return metadata?.let { Implementation(it) } ?: EMPTY
        }

        class Implementation(override val value: String) : ImmutableEntryMetadata
    }
}
