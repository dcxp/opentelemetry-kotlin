/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.internal

object OtelEncodingUtils {
    val LONG_BYTES: Int = Long.SIZE_BITS / Byte.SIZE_BITS
    const val BYTE_BASE16 = 2
    val LONG_BASE16 = BYTE_BASE16 * LONG_BYTES
    private const val ALPHABET = "0123456789abcdef"
    private const val NUM_ASCII_CHARACTERS = 128
    private val ENCODING = buildEncodingArray()
    private val DECODING = buildDecodingArray()
    private val VALID_HEX = buildValidHexArray()
    private fun buildEncodingArray(): CharArray {
        val encoding = CharArray(512)
        for (i in 0..255) {
            encoding[i] = ALPHABET[i ushr 4]
            encoding[i or 0x100] = ALPHABET[i and 0xF]
        }
        return encoding
    }

    private fun buildDecodingArray(): ByteArray {
        val decoding = ByteArray(NUM_ASCII_CHARACTERS)
        decoding.fill(-1)
        for (i in ALPHABET.indices) {
            val c = ALPHABET[i]
            decoding[c.code] = i.toByte()
        }
        return decoding
    }

    private fun buildValidHexArray(): BooleanArray {
        val validHex = BooleanArray(Char.MAX_VALUE.code)
        var i = 0
        while (i < Char.MAX_VALUE.code) {
            validHex[i] = i in 48..57 || i in 97..102
            i++
        }
        return validHex
    }

    /**
     * Returns the `long` value whose base16 representation is stored in the first 16 chars of
     * `chars` starting from the `offset`.
     *
     * @param chars the base16 representation of the `long`.
     * @param offset the starting offset in the `CharSequence`.
     */
    fun longFromBase16String(chars: CharSequence, offset: Int): Long {
        return (byteFromBase16(chars[offset], chars[offset + 1]).toLong() and 0xFFL shl 56) or
            (byteFromBase16(chars[offset + 2], chars[offset + 3]).toLong() and 0xFFL shl 48) or
            (byteFromBase16(chars[offset + 4], chars[offset + 5]).toLong() and 0xFFL shl 40) or
            (byteFromBase16(chars[offset + 6], chars[offset + 7]).toLong() and 0xFFL shl 32) or
            (byteFromBase16(chars[offset + 8], chars[offset + 9]).toLong() and 0xFFL shl 24) or
            (byteFromBase16(chars[offset + 10], chars[offset + 11]).toLong() and 0xFFL shl 16) or
            (byteFromBase16(chars[offset + 12], chars[offset + 13]).toLong() and 0xFFL shl 8) or
            (byteFromBase16(chars[offset + 14], chars[offset + 15]).toLong() and 0xFFL)
    }

    /**
     * Appends the base16 encoding of the specified `value` to the `dest`.
     *
     * @param value the value to be converted.
     * @param dest the destination char array.
     * @param destOffset the starting offset in the destination char array.
     */
    fun longToBase16String(value: Long, dest: CharArray, destOffset: Int) {
        byteToBase16((value shr 56 and 0xFFL).toByte(), dest, destOffset)
        byteToBase16((value shr 48 and 0xFFL).toByte(), dest, destOffset + BYTE_BASE16)
        byteToBase16((value shr 40 and 0xFFL).toByte(), dest, destOffset + 2 * BYTE_BASE16)
        byteToBase16((value shr 32 and 0xFFL).toByte(), dest, destOffset + 3 * BYTE_BASE16)
        byteToBase16((value shr 24 and 0xFFL).toByte(), dest, destOffset + 4 * BYTE_BASE16)
        byteToBase16((value shr 16 and 0xFFL).toByte(), dest, destOffset + 5 * BYTE_BASE16)
        byteToBase16((value shr 8 and 0xFFL).toByte(), dest, destOffset + 6 * BYTE_BASE16)
        byteToBase16((value and 0xFFL).toByte(), dest, destOffset + 7 * BYTE_BASE16)
    }

    /** Returns the `byte[]` decoded from the given hex [CharSequence]. */
    fun bytesFromBase16(value: CharSequence, length: Int): ByteArray {
        val result = ByteArray(length / 2)
        var i = 0
        while (i < length) {
            result[i / 2] = byteFromBase16(value[i], value[i + 1])
            i += 2
        }
        return result
    }

    /** Fills `dest` with the hex encoding of `bytes`. */
    fun bytesToBase16(bytes: ByteArray, dest: CharArray, length: Int) {
        for (i in 0 until length) {
            byteToBase16(bytes[i], dest, i * 2)
        }
    }

    /**
     * Encodes the specified byte, and returns the encoded `String`.
     *
     * @param value the value to be converted.
     * @param dest the destination char array.
     * @param destOffset the starting offset in the destination char array.
     */
    fun byteToBase16(value: Byte, dest: CharArray, destOffset: Int) {
        val b: Int = value.toInt() and 0xFF
        dest[destOffset] = ENCODING[b]
        dest[destOffset + 1] = ENCODING[b or 0x100]
    }

    /**
     * Decodes the specified two character sequence, and returns the resulting `byte`.
     *
     * @param first the first hex character.
     * @param second the second hex character.
     * @return the resulting `byte`
     */
    fun byteFromBase16(first: Char, second: Char): Byte {
        require(!(first.code >= NUM_ASCII_CHARACTERS || DECODING[first.code].toInt() == -1)) {
            "invalid character $first"
        }
        require(!(second.code >= NUM_ASCII_CHARACTERS || DECODING[second.code].toInt() == -1)) {
            "invalid character $second"
        }
        val decoded: Int = DECODING[first.code].toInt() shl 4 or DECODING[second.code].toInt()
        return decoded.toByte()
    }

    /** Returns whether the [CharSequence] is a valid hex string. */
    fun isValidBase16String(value: CharSequence): Boolean {
        val len = value.length
        for (i in 0 until len) {
            val b = value[i]
            if (!isValidBase16Character(b)) {
                return false
            }
        }
        return true
    }

    /** Returns whether the given `char` is a valid hex character. */
    fun isValidBase16Character(b: Char): Boolean {
        return VALID_HEX[b.code]
    }
}
