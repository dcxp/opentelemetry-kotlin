/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.trace

import io.opentelemetry.kotlin.api.internal.OtelEncodingUtils
import io.opentelemetry.kotlin.api.internal.TemporaryBuffers

/**
 * Helper methods for dealing with a span identifier. A valid span identifier is a 16 character
 * lowercase hex (base16) String, where at least one of the characters is not a "0".
 *
 * There are two more other representation that this class helps with:
 *
 * * Bytes: a 8-byte array, where valid means that at least one of the bytes is not `\0`.
 * * Long: a `long` value, where valid means that the value is non-zero.
 */
object SpanId {
    private const val BYTES_LENGTH = 8

    /**
     * Returns the length of the lowercase hex (base16) representation of the `SpanId`.
     *
     * @return the length of the lowercase hex (base16) representation of the `SpanId`.
     */
    const val length = 2 * BYTES_LENGTH

    /**
     * Returns the invalid `SpanId` in lowercase hex (base16) representation. All characters are
     * "0".
     *
     * @return the invalid `SpanId` lowercase in hex (base16) representation.
     */
    const val invalid = "0000000000000000"

    /**
     * Returns whether the span identifier is valid. A valid span identifier is a 16 character hex
     * String, where at least one of the characters is not a '0'.
     *
     * @return `true` if the span identifier is valid.
     */
    fun isValid(spanId: CharSequence?): Boolean {
        return (spanId != null &&
            spanId.length == length &&
            !invalid.contentEquals(spanId) &&
            OtelEncodingUtils.isValidBase16String(spanId))
    }

    /**
     * Returns the lowercase hex (base16) representation of the `SpanId` converted from the given
     * bytes representation, or [.getInvalid] if input is `null` or the given byte array is too
     * short.
     *
     * It converts the first 8 bytes of the given byte array.
     *
     * @param spanIdBytes the bytes (8-byte array) representation of the `SpanId`.
     * @return the lowercase hex (base16) representation of the `SpanId`.
     */
    fun fromBytes(spanIdBytes: ByteArray?): String {
        if (spanIdBytes == null || spanIdBytes.size < BYTES_LENGTH) {
            return invalid
        }
        val result: CharArray = TemporaryBuffers.chars(length)
        OtelEncodingUtils.bytesToBase16(spanIdBytes, result, BYTES_LENGTH)
        return result.concatToString()
    }

    /**
     * Returns the lowercase hex (base16) representation of the `SpanId` converted from the given
     * `long` value representation.
     *
     * There is no restriction on the specified values, other than the already established validity
     * rules applying to `SpanId`. Specifying 0 for the long value will effectively return
     * [.getInvalid].
     *
     * This is equivalent to calling [.fromBytes] with the specified value stored as big-endian.
     *
     * @param id `long` value representation of the `SpanId`.
     * @return the lowercase hex (base16) representation of the `SpanId`.
     */
    fun fromLong(id: Long): String {
        if (id == 0L) {
            return invalid
        }
        val result: CharArray = TemporaryBuffers.chars(length)
        OtelEncodingUtils.longToBase16String(id, result, 0)
        return result.concatToString()
    }
}
