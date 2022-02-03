/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.trace.SpanContext
import io.opentelemetry.kotlin.api.trace.SpanKind
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.resources.Resource
import io.opentelemetry.kotlin.sdk.trace.data.EventData
import io.opentelemetry.kotlin.sdk.trace.data.LinkData
import io.opentelemetry.kotlin.sdk.trace.data.SpanData
import io.opentelemetry.kotlin.sdk.trace.data.StatusData

/**
 * Immutable class that stores [SpanData] based on a [RecordEventsReadableSpan].
 *
 * This class stores a reference to a mutable [RecordEventsReadableSpan] (`delegate`) which it uses
 * only the immutable parts from, and a copy of all the mutable parts.
 *
 * When adding a new field to [RecordEventsReadableSpan], store a copy if and only if the field is
 * mutable in the [RecordEventsReadableSpan]. Otherwise retrieve it from the referenced
 * [RecordEventsReadableSpan].
 */
internal abstract class SpanWrapper : SpanData {
    abstract val delegate: RecordEventsReadableSpan
    abstract val resolvedLinks: List<LinkData>
    abstract val resolvedEvents: List<EventData>
    abstract val internalHasEnded: Boolean

    override val links: List<LinkData>
        get() = resolvedLinks
    override val events: List<EventData>
        get() = resolvedEvents
    override val spanContext: SpanContext
        get() = delegate.spanContext
    override val parentSpanContext: SpanContext
        get() = delegate.parentSpanContext
    override val resource: Resource
        get() = delegate.resource
    override val kind: SpanKind
        get() = delegate.kind
    override val startEpochNanos: Long
        get() = delegate.startEpochNanos
    override val totalRecordedLinks: Int
        get() = delegate.totalRecordedLinks
    override val instrumentationLibraryInfo: InstrumentationLibraryInfo
        get() = delegate.instrumentationLibraryInfo

    override fun hasEnded(): Boolean {
        return internalHasEnded
    }

    override fun toString(): String {
        return ("SpanData{" +
            "spanContext=" +
            spanContext +
            ", " +
            "parentSpanContext=" +
            parentSpanContext +
            ", " +
            "resource=" +
            resource +
            ", " +
            ", " +
            "name=" +
            name +
            ", " +
            "kind=" +
            kind +
            ", " +
            "startEpochNanos=" +
            startEpochNanos +
            ", " +
            "endEpochNanos=" +
            endEpochNanos +
            ", " +
            "attributes=" +
            attributes +
            ", " +
            "totalAttributeCount=" +
            totalAttributeCount +
            ", " +
            "events=" +
            events +
            ", " +
            "totalRecordedEvents=" +
            totalRecordedEvents +
            ", " +
            "links=" +
            links +
            ", " +
            "totalRecordedLinks=" +
            totalRecordedLinks +
            ", " +
            "status=" +
            status +
            ", " +
            "hasEnded=" +
            hasEnded() +
            "}")
    }

    companion object {
        /**
         * Note: the collections that are passed into this creator method are assumed to be
         * immutable to preserve the overall immutability of the class.
         */
        fun create(
            delegate: RecordEventsReadableSpan,
            links: List<LinkData>,
            events: List<EventData>,
            attributes: Attributes,
            totalAttributeCount: Int,
            totalRecordedEvents: Int,
            status: StatusData,
            name: String,
            endEpochNanos: Long,
            hasEnded: Boolean
        ): SpanWrapper {
            return Implementation(
                delegate,
                links,
                events,
                attributes,
                totalAttributeCount,
                totalRecordedEvents,
                status,
                name,
                endEpochNanos,
                hasEnded
            )
        }

        private class Implementation(
            override val delegate: RecordEventsReadableSpan,
            override val resolvedLinks: List<LinkData>,
            override val resolvedEvents: List<EventData>,
            override val attributes: Attributes,
            override val totalAttributeCount: Int,
            override val totalRecordedEvents: Int,
            override val status: StatusData,
            override val name: String,
            override val endEpochNanos: Long,
            override val internalHasEnded: Boolean
        ) : SpanWrapper()
    }
}
