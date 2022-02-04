/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.trace.SpanContext
import io.opentelemetry.kotlin.api.trace.SpanKind
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.trace.data.SpanData

/** The extend Span interface used by the SDK. */
interface ReadableSpan {
    /**
     * Returns the [SpanContext] of the `Span`.
     *
     * Equivalent with [Span.getSpanContext].
     *
     * @return the [SpanContext] of the `Span`.
     */
    val spanContext: SpanContext

    /**
     * Returns the parent [SpanContext] of the [Span], or [SpanContext.getInvalid] if this is a root
     * span.
     *
     * @return the parent [SpanContext] of the [Span]
     */
    val parentSpanContext: SpanContext

    /**
     * Returns the name of the `Span`.
     *
     * The name can be changed during the lifetime of the Span by using the [ ][Span.updateName] so
     * this value cannot be cached.
     *
     * Note: the implementation of this method performs locking to ensure thread-safe behavior.
     *
     * @return the name of the `Span`.
     */
    val name: String?

    /**
     * This converts this instance into an immutable SpanData instance, for use in export.
     *
     * @return an immutable [SpanData] instance.
     */
    fun toSpanData(): SpanData

    /**
     * Returns the instrumentation library specified when creating the tracer which produced this
     * span.
     *
     * @return an instance of [InstrumentationLibraryInfo] describing the instrumentation library
     */
    val instrumentationLibraryInfo: InstrumentationLibraryInfo

    /**
     * Returns the instrumentation library specified when creating the tracer which produced this
     * span.
     *
     * @return an instance of [InstrumentationLibraryInfo] describing the instrumentation library
     */
    // val instrumentationLibraryInfo: InstrumentationLibraryInfo?

    /**
     * Returns whether this Span has already been ended.
     *
     * Note: the implementation of this method performs locking to ensure thread-safe behavior.
     *
     * @return `true` if the span has already been ended, `false` if not.
     */
    fun hasEnded(): Boolean

    /**
     * Returns the latency of the `Span` in nanos. If still active then returns now() - start time.
     *
     * Note: the implementation of this method performs locking to ensure thread-safe behavior.
     *
     * @return the latency of the `Span` in nanos.
     */
    val latencyNanos: Long

    /**
     * Returns the kind of the span.
     *
     * @return the kind of the span.
     */
    val kind: SpanKind

    /**
     * Returns the value for the given [AttributeKey], or `null` if not found.
     *
     * The attribute values can be changed during the lifetime of the Span by using [ ]
     * [Span.setAttribute]} so this value cannot be cached.
     *
     * Note: the implementation of this method performs locking to ensure thread-safe behavior.
     *
     * @return the value for the given [AttributeKey], or `null` if not found.
     */
    fun <T : Any> getAttribute(key: AttributeKey<T>): T?
}
