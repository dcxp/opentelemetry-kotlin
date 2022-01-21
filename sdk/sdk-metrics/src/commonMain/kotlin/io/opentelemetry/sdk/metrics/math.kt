package io.opentelemetry.sdk.metrics

internal object Math {
    fun scalb(d: Double, n: Int): Double {

        // first simple and fast handling when 2^n can be represented using normal numbers
        if (n > -1023 && n < 1024) {
            return d * ((n + 1023).toLong() shl 52).toDouble()
        }

        // handle special cases
        if (d.isNaN() || d.isInfinite() || d == 0.0) {
            return d
        }
        if (n < -2098) {
            return if (d > 0) 0.0 else -0.0
        }
        if (n > 2097) {
            return if (d > 0) Double.POSITIVE_INFINITY else Double.NEGATIVE_INFINITY
        }

        // decompose d
        val bits: Long = d.toRawBits()
        val sign = bits and (-0x800000000000000L).toLong()
        val exponent = (bits ushr 52).toInt() and 0x7ff
        var mantissa = bits and 0x000fffffffffffffL

        // compute scaled exponent
        var scaledExponent = exponent + n
        if (n < 0) {
            // we are really in the case n <= -1023
            if (scaledExponent > 0) {
                // both the input and the result are normal numbers, we only adjust the exponent
                return (sign or (scaledExponent.toLong() shl 52) or mantissa).toDouble()
            } else if (scaledExponent > -53) {
                // the input is a normal number and the result is a subnormal number

                // recover the hidden mantissa bit
                mantissa = mantissa or (1L shl 52)

                // scales down complete mantissa, hence losing least significant bits
                val mostSignificantLostBit = mantissa and (1L shl -scaledExponent)
                mantissa = mantissa ushr 1 - scaledExponent
                if (mostSignificantLostBit != 0L) {
                    // we need to add 1 bit to round up the result
                    mantissa++
                }
                return (sign or mantissa).toDouble()
            } else {
                // no need to compute the mantissa, the number scales down to 0
                return if (sign == 0L) 0.0 else -0.0
            }
        } else {
            // we are really in the case n >= 1024
            if (exponent == 0) {

                // the input number is subnormal, normalize it
                while (mantissa ushr 52 != 1L) {
                    mantissa = mantissa shl 1
                    --scaledExponent
                }
                ++scaledExponent
                mantissa = mantissa and 0x000fffffffffffffL
                if (scaledExponent < 2047) {
                    return (sign or (scaledExponent.toLong() shl 52) or mantissa).toDouble()
                } else {
                    return if (sign == 0L) Double.POSITIVE_INFINITY else Double.NEGATIVE_INFINITY
                }
            } else if (scaledExponent < 2047) {
                return (sign or (scaledExponent.toLong() shl 52) or mantissa).toDouble()
            } else {
                return if (sign == 0L) Double.POSITIVE_INFINITY else Double.NEGATIVE_INFINITY
            }
        }
    }
}
