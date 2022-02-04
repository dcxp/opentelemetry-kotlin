/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.data

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.trace.SpanContext
import io.opentelemetry.kotlin.api.trace.SpanKind
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.resources.Resource

/**
 * Immutable representation of all data collected by the [io.opentelemetry.kotlin.api.trace.Span]
 * class.
 */
interface SpanData {
    /**
     * Returns the name of this `Span`.
     *
     * @return the name of this `Span`.
     */
    val name: String

    /**
     * Returns the kind of this `Span`.
     *
     * @return the kind of this `Span`.
     */
    val kind: SpanKind

    /** Returns the [SpanContext] of the Span. */
    val spanContext: SpanContext

    /**
     * Gets the trace id for this span.
     *
     * @return the trace id.
     */
    val traceId: String?
        get() = spanContext.traceId

    /**
     * Gets the span id for this span.
     *
     * @return the span id.
     */
    val spanId: String?
        get() = spanContext.spanId

    /**
     * Returns the parent [SpanContext]. If the span is a root span, the [SpanContext] returned will
     * be invalid.
     */
    val parentSpanContext: SpanContext

    /**
     * Returns the parent `SpanId`. If the `Span` is a root `Span`, the SpanId returned will be
     * invalid.
     *
     * @return the parent `SpanId` or an invalid SpanId if this is a root `Span`.
     */
    val parentSpanId: String?
        get() = parentSpanContext.spanId

    /**
     * Returns the `Status`.
     *
     * @return the `Status`.
     */
    val status: io.opentelemetry.kotlin.sdk.trace.data.StatusData

    /**
     * Returns the start epoch timestamp in nanos of this `Span`.
     *
     * @return the start epoch timestamp in nanos of this `Span`.
     */
    val startEpochNanos: Long

    /**
     * Returns the attributes recorded for this `Span`.
     *
     * @return the attributes recorded for this `Span`.
     */
    val attributes: Attributes

    /**
     * Returns the timed events recorded for this `Span`.
     *
     * @return the timed events recorded for this `Span`.
     */
    val events: List<io.opentelemetry.kotlin.sdk.trace.data.EventData>

    /**
     * Returns links recorded for this `Span`.
     *
     * @return links recorded for this `Span`.
     */
    val links: List<io.opentelemetry.kotlin.sdk.trace.data.LinkData>

    /**
     * Returns the end epoch timestamp in nanos of this `Span`.
     *
     * @return the end epoch timestamp in nanos of this `Span`.
     */
    val endEpochNanos: Long

    /**
     * Returns whether this Span has already been ended.
     *
     * @return `true` if the span has already been ended, `false` if not.
     */
    fun hasEnded(): Boolean

    /**
     * The total number of [EventData] events that were recorded on this span. This number may be
     * larger than the number of events that are attached to this span, if the total number recorded
     * was greater than the configured maximum value. See: [ ][SpanLimits.getMaxNumberOfEvents]
     *
     * @return The total number of events recorded on this span.
     */
    val totalRecordedEvents: Int

    /**
     * The total number of [LinkData] links that were recorded on this span. This number may be
     * larger than the number of links that are attached to this span, if the total number recorded
     * was greater than the configured maximum value. See: [SpanLimits.getMaxNumberOfLinks]
     *
     * @return The total number of links recorded on this span.
     */
    val totalRecordedLinks: Int

    /**
     * The total number of attributes that were recorded on this span. This number may be larger
     * than the number of attributes that are attached to this span, if the total number recorded
     * was greater than the configured maximum value. See: [SpanLimits.getMaxNumberOfAttributes]
     *
     * @return The total number of attributes on this span.
     */
    val totalAttributeCount: Int

    /**
     * Returns the instrumentation library specified when creating the tracer which produced this
     * `Span`.
     *
     * @return an instance of [InstrumentationLibraryInfo]
     */
    val instrumentationLibraryInfo: InstrumentationLibraryInfo

    /**
     * Returns the resource of this `Span`.
     *
     * @return the resource of this `Span`.
     */
    val resource: Resource
}
