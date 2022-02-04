/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.internal

/**
 * [ThreadLocal] buffers for use when creating new derived objects such as [String]s. These buffers
 * are reused within a single thread - it is _not safe_ to use the buffer to generate multiple
 * derived objects at the same time because the same memory will be used. In general, you should get
 * a temporary buffer, fill it with data, and finish by converting into the derived object within
 * the same method to avoid multiple usages of the same buffer.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
object TemporaryBuffers {
    // private val CHAR_ARRAY: ThreadLocal<CharArray> = ThreadLocal<CharArray>()

    /**
     * A [ThreadLocal] `char[]` of size `len`. Take care when using a large value of `len` as this
     * buffer will remain for the lifetime of the thread. The returned buffer will not be zeroed and
     * may be larger than the requested size, you must make sure to fill the entire content to the
     * desired value and set the length explicitly when converting to a [String].
     */
    fun chars(len: Int): CharArray {
        return CharArray(len)
    }

    // Visible for testing
    fun clearChars() {}
}
