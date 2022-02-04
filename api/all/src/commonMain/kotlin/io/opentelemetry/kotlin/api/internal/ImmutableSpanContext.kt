/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.internal

import io.opentelemetry.kotlin.api.trace.SpanContext
import io.opentelemetry.kotlin.api.trace.SpanId
import io.opentelemetry.kotlin.api.trace.TraceFlags
import io.opentelemetry.kotlin.api.trace.TraceId
import io.opentelemetry.kotlin.api.trace.TraceState

/**
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
abstract class ImmutableSpanContext : SpanContext {

    companion object {
        val INVALID: SpanContext =
            createInternal(
                TraceId.invalid,
                SpanId.invalid,
                TraceFlags.default,
                TraceState.default,
                false
            )

        private fun createInternal(
            traceId: String,
            spanId: String,
            traceFlags: TraceFlags,
            traceState: TraceState,
            remote: Boolean
        ): Instance {
            return Instance(traceId, spanId, traceFlags, traceState, remote)
        }

        class Instance(
            override val traceId: String,
            override val spanId: String,
            override val traceFlags: TraceFlags,
            override val traceState: TraceState,
            override val isRemote: Boolean
        ) : ImmutableSpanContext() {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || this::class != other::class) return false

                other as Instance

                if (traceId != other.traceId) return false
                if (spanId != other.spanId) return false
                if (traceFlags != other.traceFlags) return false
                if (traceState != other.traceState) return false
                if (isRemote != other.isRemote) return false

                return true
            }

            override fun hashCode(): Int {
                var result = traceId.hashCode()
                result = 31 * result + spanId.hashCode()
                result = 31 * result + traceFlags.hashCode()
                result = 31 * result + traceState.hashCode()
                result = 31 * result + isRemote.hashCode()
                return result
            }

            override fun toString(): String {
                return "ImmutableSpanContext(traceId='$traceId', spanId='$spanId', traceFlags=$traceFlags, traceState=$traceState, isRemote=$isRemote, isValid=$isValid)"
            }
        }

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
         * @param remote the remote flag for the `SpanContext`.
         * @return a new `SpanContext` with the given identifiers and options.
         */
        fun create(
            traceIdHex: String,
            spanIdHex: String,
            traceFlags: TraceFlags,
            traceState: TraceState,
            remote: Boolean,
            skipIdValidation: Boolean
        ): SpanContext {
            return if (skipIdValidation || SpanId.isValid(spanIdHex) && TraceId.isValid(traceIdHex)
            ) {
                createInternal(traceIdHex, spanIdHex, traceFlags, traceState, remote)
            } else createInternal(TraceId.invalid, SpanId.invalid, traceFlags, traceState, remote)
        }
    }
}
