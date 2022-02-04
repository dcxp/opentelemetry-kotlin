/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.trace

/**
 * A valid trace flags is a byte or 2 character lowercase hex (base16) String.
 *
 * These options are propagated to all child [spans][Span]. These determine features such as whether
 * a `Span` should be traced.
 */
interface TraceFlags {
    /**
     * Returns `true` if the sampling bit is on for this [TraceFlags], otherwise `false`.
     *
     * @return `true` if the sampling bit is on for this [TraceFlags], otherwise `* false`.
     */
    fun isSampled(): Boolean

    /**
     * Returns the lowercase hex (base16) representation of this [TraceFlags].
     *
     * @return the byte representation of the [TraceFlags].
     */
    fun asHex(): String

    /**
     * Returns the byte representation of this [TraceFlags].
     *
     * @return the byte representation of the [TraceFlags].
     */
    fun asByte(): Byte

    companion object {
        /**
         * Returns the length of the lowercase hex (base16) representation of the [TraceFlags].
         *
         * @return the length of the lowercase hex (base16) representation of the [TraceFlags].
         */
        val length: Int
            get() = io.opentelemetry.kotlin.api.trace.ImmutableTraceFlags.Companion.HEX_LENGTH

        /**
         * Returns the default (with all flag bits off) byte representation of the [TraceFlags].
         *
         * @return the default (with all flag bits off) byte representation of the [TraceFlags].
         */
        val default: TraceFlags
            get() = ImmutableTraceFlags.DEFAULT

        /**
         * Returns the lowercase hex (base16) representation of the [TraceFlags] with the sampling
         * flag bit on.
         *
         * @return the lowercase hex (base16) representation of the [TraceFlags] with the sampling
         * flag bit on.
         */
        val sampled: TraceFlags
            get() = ImmutableTraceFlags.SAMPLED

        /**
         * Returns the [TraceFlags] converted from the given lowercase hex (base16) representation.
         *
         * This may throw runtime exceptions if the input is invalid.
         *
         * @param src the buffer where the hex (base16) representation of the [TraceFlags] is.
         * @param srcOffset the offset int buffer.
         * @return the [TraceFlags] converted from the given lowercase hex (base16) representation.
         */
        fun fromHex(src: CharSequence, srcOffset: Int): TraceFlags {
            return ImmutableTraceFlags.fromHex(src, srcOffset)
        }

        /**
         * Returns the [TraceFlags] converted from the given byte representation.
         *
         * @param traceFlagsByte the byte representation of the [TraceFlags].
         * @return the [TraceFlags] converted from the given byte representation.
         */
        fun fromByte(traceFlagsByte: Byte): TraceFlags {
            return ImmutableTraceFlags.fromByte(traceFlagsByte)
        }
    }
}
