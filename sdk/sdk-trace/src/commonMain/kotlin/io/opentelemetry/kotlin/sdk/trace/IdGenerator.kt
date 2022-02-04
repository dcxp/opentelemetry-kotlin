/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

/** Interface used by the [SdkTracer] to generate new [SpanId]s and [TraceId]s. */
interface IdGenerator {
    /**
     * Generates a new valid `SpanId`.
     *
     * @return a new valid `SpanId`.
     */
    fun generateSpanId(): String

    /**
     * Generates a new valid `TraceId`.
     *
     * @return a new valid `TraceId`.
     */
    fun generateTraceId(): String

    companion object {
        /**
         * Returns a [IdGenerator] that generates purely random IDs, which is the default for
         * OpenTelemetry.
         *
         * The underlying implementation uses [java.util.concurrent.ThreadLocalRandom] for
         * randomness but may change in the future.
         */
        fun random(): IdGenerator {
            // note: uses RandomHolder's platformDefault to account for android.
            return RandomIdGenerator.INSTANCE
        }
    }
}
