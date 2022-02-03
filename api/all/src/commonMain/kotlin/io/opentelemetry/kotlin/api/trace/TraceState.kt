/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.trace

/**
 * Carries tracing-system specific context in a list of key-value pairs. TraceState allows different
 * vendors propagate additional information and inter-operate with their legacy Id formats.
 *
 * Implementation is optimized for a small list of key-value pairs.
 *
 * Key is opaque string up to 256 characters printable. It MUST begin with a lowercase letter, and
 * can only contain lowercase letters a-z, digits 0-9, underscores _, dashes -, asterisks *, and
 * forward slashes /.
 *
 * Value is opaque string up to 256 characters printable ASCII RFC0020 characters (i.e., the range
 * 0x20 to 0x7E) except comma , and =.
 *
 * Implementations of this interface *must* be immutable and have well-defined value-based
 * equals/hashCode implementations. If an implementation does not strictly conform to these
 * requirements, behavior of the OpenTelemetry APIs and default SDK cannot be guaranteed.
 *
 * Implementations of this interface that do not conform to the W3C specification risk
 * incompatibility with W3C-compatible implementations.
 *
 * For these reasons, it is strongly suggested that you use the implementation that is provided here
 * via the [TraceState.builder].
 */
interface TraceState {
    /**
     * Returns the value to which the specified key is mapped, or null if this map contains no
     * mapping for the key.
     *
     * @param key with which the specified value is to be associated
     * @return the value to which the specified key is mapped, or null if this map contains no
     * mapping for the key.
     */
    operator fun get(key: String): String

    /** Returns the number of entries in this [TraceState]. */
    fun size(): Int

    /** Returns whether this [TraceState] is empty, containing no entries. */
    val isEmpty: Boolean

    /** Iterates over all the key-value entries contained in this [TraceState]. */
    fun forEach(consumer: (String, String) -> Unit)

    /** Returns a read-only view of this [TraceState] as a [Map]. */
    fun asMap(): Map<String, String>

    /**
     * Returns a `Builder` based on this `TraceState`.
     *
     * @return a `Builder` based on this `TraceState`.
     */
    fun toBuilder(): io.opentelemetry.kotlin.api.trace.TraceStateBuilder

    companion object {
        /**
         * Returns the default `TraceState` with no entries.
         *
         * This method is equivalent to calling `#builder().build()`, but avoids new allocations.
         *
         * @return the default `TraceState` with no entries.
         */
        val default: TraceState
            get() = ArrayBasedTraceStateBuilder.empty()

        /** Returns an empty `TraceStateBuilder`. */
        fun builder(): TraceStateBuilder {
            return ArrayBasedTraceStateBuilder()
        }
    }
}
