/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.baggage

/** An entry in a set of baggage. */
interface BaggageEntry {
    /** Returns the entry's value. */
    val value: String

    /** Returns the entry's [BaggageEntryMetadata]. */
    val metadata: BaggageEntryMetadata
}
