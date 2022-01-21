/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.trace

import io.opentelemetry.api.internal.ImmutableSpanContext
import io.opentelemetry.api.internal.OtelEncodingUtils

/**
 * A class that represents a span context. A span context contains the state that must propagate to
 * child [Span]s and across process boundaries. It contains the identifiers (a [ trace_id][TraceId]
 * and [span_id][SpanId]) associated with the [Span] and a set of options (currently only whether
 * the context is sampled or not), as well as the [ traceState][TraceState] and the [remote]
 * [boolean] flag.
 *
 * Implementations of this interface *must* be immutable and have well-defined value-based
 * equals/hashCode implementations. If an implementation does not strictly conform to these
 * requirements, behavior of the OpenTelemetry APIs and default SDK cannot be guaranteed. It is
 * strongly suggested that you use the implementation that is provided here via [ ][.create] or
 * [.createFromRemoteParent].
 */
interface SpanContext {
    /**
     * Returns the trace identifier associated with this [SpanContext] as 32 character lowercase hex
     * String.
     *
     * @return the trace identifier associated with this [SpanContext] as lowercase hex.
     */
    val traceId: String

    /**
     * Returns the trace identifier associated with this [SpanContext] as 16-byte array.
     *
     * @return the trace identifier associated with this [SpanContext] as 16-byte array.
     */
    val traceIdBytes: ByteArray
        get() = OtelEncodingUtils.bytesFromBase16(traceId, TraceId.length)

    /**
     * Returns the span identifier associated with this [SpanContext] as 16 character lowercase hex
     * String.
     *
     * @return the span identifier associated with this [SpanContext] as 16 character lowercase hex
     * (base16) String.
     */
    val spanId: String

    /**
     * Returns the span identifier associated with this [SpanContext] as 8-byte array.
     *
     * @return the span identifier associated with this [SpanContext] as 8-byte array.
     */
    val spanIdBytes: ByteArray
        get() = OtelEncodingUtils.bytesFromBase16(spanId, SpanId.length)

    /** Whether the span in this context is sampled. */
    fun isSampled(): Boolean {
        return traceFlags.isSampled()
    }
    /**
     * Returns the trace flags associated with this [SpanContext].
     *
     * @return the trace flags associated with this [SpanContext].
     */
    val traceFlags: TraceFlags

    /**
     * Returns the `TraceState` associated with this `SpanContext`.
     *
     * @return the `TraceState` associated with this `SpanContext`.
     */
    val traceState: TraceState

    /**
     * Returns `true` if this `SpanContext` is valid.
     *
     * @return `true` if this `SpanContext` is valid.
     */
    val isValid: Boolean
        get() = TraceId.isValid(traceId) && SpanId.isValid(spanId)

    val isNotValid: Boolean
        get() = !isValid

    /**
     * Returns `true` if the `SpanContext` was propagated from a remote parent.
     *
     * @return `true` if the `SpanContext` was propagated from a remote parent.
     */
    val isRemote: Boolean

    val isNotRemote: Boolean
        get() = !isRemote

    companion object {
        /**
         * Returns the invalid `SpanContext` that can be used for no-op operations.
         *
         * @return the invalid `SpanContext`.
         */
        @kotlin.jvm.JvmStatic
        val invalid: SpanContext
            get() = ImmutableSpanContext.INVALID

        /**
         * Creates a new `SpanContext` with the given identifiers and options.
         *
         * If the traceId or the spanId are invalid (ie. do not conform to the requirements for
         * hexadecimal ids of the appropriate lengths), both will be replaced with the standard
         * "invalid" versions (i.e. all '0's). See [SpanId.isValid] and [ ][TraceId.isValid] for
         * details.
         *
         * @param traceIdHex the trace identifier of the `SpanContext`.
         * @param spanIdHex the span identifier of the `SpanContext`.
         * @param traceFlags the trace flags of the `SpanContext`.
         * @param traceState the trace state for the `SpanContext`.
         * @return a new `SpanContext` with the given identifiers and options.
         */
        fun create(
            traceIdHex: String,
            spanIdHex: String,
            traceFlags: TraceFlags,
            traceState: TraceState
        ): SpanContext {
            return ImmutableSpanContext.create(
                traceIdHex,
                spanIdHex,
                traceFlags,
                traceState,
                /* remote=*/ false,
                /* skipIdValidation=*/ false
            )
        }

        /**
         * Creates a new `SpanContext` that was propagated from a remote parent, with the given
         * identifiers and options.
         *
         * If the traceId or the spanId are invalid (ie. do not conform to the requirements for
         * hexadecimal ids of the appropriate lengths), both will be replaced with the standard
         * "invalid" versions (i.e. all '0's). See [SpanId.isValid] and [ ][TraceId.isValid] for
         * details.
         *
         * @param traceIdHex the trace identifier of the `SpanContext`.
         * @param spanIdHex the span identifier of the `SpanContext`.
         * @param traceFlags the trace flags of the `SpanContext`.
         * @param traceState the trace state for the `SpanContext`.
         * @return a new `SpanContext` with the given identifiers and options.
         */
        fun createFromRemoteParent(
            traceIdHex: String,
            spanIdHex: String,
            traceFlags: TraceFlags,
            traceState: TraceState
        ): SpanContext {
            return ImmutableSpanContext.create(
                traceIdHex,
                spanIdHex,
                traceFlags,
                traceState,
                /* remote=*/ true,
                /* skipIdValidation=*/ false
            )
        }
    }
}
