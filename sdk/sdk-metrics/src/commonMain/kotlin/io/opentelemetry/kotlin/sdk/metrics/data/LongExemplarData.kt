/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

import io.opentelemetry.kotlin.api.common.Attributes

/** An [ExemplarData] with `long` measurements. */
interface LongExemplarData : ExemplarData {
    /** Numerical value of the measurement that was recorded. */
    val value: Long
    override val valueAsDouble: Double
        get() = value.toDouble()

    companion object {
        /**
         * Construct a new exemplar.
         *
         * @param filteredAttributes The set of [Attributes] not already associated with the [ ].
         * @param recordTimeNanos The time when the sample qas recorded in nanoseconds.
         * @param spanId (optional) The associated SpanId.
         * @param traceId (optional) The associated TraceId.
         * @param value The value recorded.
         */
        fun create(
            filteredAttributes: Attributes,
            recordTimeNanos: Long,
            spanId: String?,
            traceId: String?,
            value: Long
        ): LongExemplarData {
            return Implementation(filteredAttributes, recordTimeNanos, spanId, traceId, value)
        }

        data class Implementation(
            override val filteredAttributes: Attributes,
            override val epochNanos: Long,
            override val spanId: String?,
            override val traceId: String?,
            override val value: Long
        ) : LongExemplarData
    }
}
