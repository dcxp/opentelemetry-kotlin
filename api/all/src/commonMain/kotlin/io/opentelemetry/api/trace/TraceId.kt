/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.trace

import io.opentelemetry.api.internal.OtelEncodingUtils
import io.opentelemetry.api.internal.TemporaryBuffers

/**
 * Helper methods for dealing with a trace identifier. A valid trace identifier is a 32 character
 * lowercase hex (base16) String, where at least one of the characters is not a "0".
 *
 * There are two more other representation that this class helps with:
 *
 * * Bytes: a 16-byte array, where valid means that at least one of the bytes is not `\0`.
 * * Long: two `long` values, where valid means that at least one of values is non-zero. To avoid
 * allocating new objects this representation uses two parts, "high part" representing the left most
 * part of the `TraceId` and "low part" representing the right most part of the `TraceId`. This is
 * equivalent with the values being stored as big-endian.
 */
object TraceId {
    private const val BYTES_LENGTH = 16

    /**
     * Returns the length of the lowercase hex (base16) representation of the `TraceId`.
     *
     * @return the length of the lowercase hex (base16) representation of the `TraceId`.
     */
    const val length = 2 * BYTES_LENGTH

    /**
     * Returns the invalid `TraceId` in lowercase hex (base16) representation. All characters are
     * "0".
     *
     * @return the invalid `TraceId` in lowercase hex (base16) representation.
     */
    const val invalid = "00000000000000000000000000000000"

    /**
     * Returns whether the `TraceId` is valid. A valid trace identifier is a 32 character hex
     * String, where at least one of the characters is not a '0'.
     *
     * @return `true` if the `TraceId` is valid.
     */
    @kotlin.jvm.JvmStatic
    fun isValid(traceId: CharSequence?): Boolean {
        return (traceId != null &&
            traceId.length == length &&
            !invalid.contentEquals(traceId) &&
            OtelEncodingUtils.isValidBase16String(traceId))
    }

    /**
     * Returns the lowercase hex (base16) representation of the `TraceId` converted from the given
     * bytes representation, or [.getInvalid] if input is `null` or the given byte array is too
     * short.
     *
     * It converts the first 26 bytes of the given byte array.
     *
     * @param traceIdBytes the bytes (16-byte array) representation of the `TraceId`.
     * @return the lowercase hex (base16) representation of the `TraceId`.
     */
    fun fromBytes(traceIdBytes: ByteArray?): String {
        if (traceIdBytes == null || traceIdBytes.size < BYTES_LENGTH) {
            return invalid
        }
        val result: CharArray = TemporaryBuffers.chars(length)
        OtelEncodingUtils.bytesToBase16(traceIdBytes, result, BYTES_LENGTH)
        return result.concatToString()
    }

    /**
     * Returns the bytes (16-byte array) representation of the `TraceId` converted from the given
     * two `long` values representing the lower and higher parts.
     *
     * There is no restriction on the specified values, other than the already established validity
     * rules applying to `TraceId`. Specifying 0 for both values will effectively return [
     * ][.getInvalid].
     *
     * This is equivalent to calling [.fromBytes] with the specified values stored as big-endian.
     *
     * @param traceIdLongHighPart the higher part of the long values representation of the
     * `TraceId`.
     * @param traceIdLongLowPart the lower part of the long values representation of the `TraceId`.
     * @return the lowercase hex (base16) representation of the `TraceId`.
     */
    fun fromLongs(traceIdLongHighPart: Long, traceIdLongLowPart: Long): String {
        if (traceIdLongHighPart == 0L && traceIdLongLowPart == 0L) {
            return invalid
        }
        val chars: CharArray = TemporaryBuffers.chars(length)
        OtelEncodingUtils.longToBase16String(traceIdLongHighPart, chars, 0)
        OtelEncodingUtils.longToBase16String(traceIdLongLowPart, chars, 16)
        return chars.concatToString()
    }
}
