/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.baggage
/**
 * Metadata associated with an [BaggageEntry]. For the moment this is an opaque wrapper for a String
 * metadata value.
 */
interface BaggageEntryMetadata {
    /** Returns the String value of this [BaggageEntryMetadata]. */
    val value: String

    companion object {
        /** Returns an empty [BaggageEntryMetadata]. */
        fun empty(): BaggageEntryMetadata {
            return ImmutableEntryMetadata.EMPTY
        }

        /** Returns a new [BaggageEntryMetadata] with the given value. */
        fun create(metadata: String): BaggageEntryMetadata {
            return ImmutableEntryMetadata.create(metadata)
        }
    }
}
