/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.trace

import io.opentelemetry.api.internal.OtelEncodingUtils

internal class ImmutableTraceFlags private constructor(byteRep: Byte) : TraceFlags {
    private val hexRep: String
    private val byteRep: Byte

    init {
        val result = CharArray(2)
        OtelEncodingUtils.byteToBase16(byteRep, result, 0)
        hexRep = result.concatToString()
        this.byteRep = byteRep
    }

    override fun isSampled(): Boolean {
        return byteRep.toInt() and SAMPLED_BIT.toInt() != 0
    }

    override fun asHex(): String {
        return hexRep
    }

    override fun asByte(): Byte {
        return byteRep
    }

    override fun toString(): String {
        return asHex()
    }

    companion object {
        private val INSTANCES = buildInstances()

        // Bit to represent whether trace is sampled or not.
        private const val SAMPLED_BIT: Byte = 0x01
        val DEFAULT = fromByte(0x00.toByte())
        val SAMPLED = fromByte(SAMPLED_BIT)
        const val HEX_LENGTH = 2

        // Implementation of the TraceFlags.fromHex().
        fun fromHex(src: CharSequence, srcOffset: Int): ImmutableTraceFlags {
            require(src.length >= 2) { "Char is to small" }
            return fromByte(OtelEncodingUtils.byteFromBase16(src[srcOffset], src[srcOffset + 1]))
        }

        // Implementation of the TraceFlags.fromByte().
        fun fromByte(traceFlagsByte: Byte): ImmutableTraceFlags {
            // Equivalent with Byte.toUnsignedInt(), but cannot use it because of Android.
            return INSTANCES[traceFlagsByte.toInt() and 255]
        }

        private fun buildInstances(): Array<ImmutableTraceFlags> {
            return (0..255).map { ImmutableTraceFlags(it.toByte()) }.toTypedArray()
        }
    }
}
