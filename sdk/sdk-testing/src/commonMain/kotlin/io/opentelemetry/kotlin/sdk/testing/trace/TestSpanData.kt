/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.testing.trace

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
 * Immutable representation of all data collected by the [io.opentelemetry.kotlin.api.trace.Span]
 * class.
 */
data class TestSpanData
internal constructor(
    override val name: String,
    override val kind: SpanKind,
    override val spanContext: SpanContext,
    override val parentSpanContext: SpanContext,
    override val status: StatusData,
    override val startEpochNanos: Long,
    override val attributes: Attributes,
    override val events: List<EventData>,
    override val links: List<LinkData>,
    override val endEpochNanos: Long,
    override val totalRecordedEvents: Int,
    override val totalRecordedLinks: Int,
    override val totalAttributeCount: Int,
    override val instrumentationLibraryInfo: InstrumentationLibraryInfo,
    override val resource: Resource,
    val internalHasEnded: Boolean
) : SpanData {

    override fun hasEnded(): Boolean {
        return internalHasEnded
    }

    /** A `Builder` class for [TestSpanData]. */
    class Builder(
        private val internalTestSpanData: TestSpanData =
            TestSpanData(
                "",
                SpanKind.INTERNAL,
                SpanContext.invalid,
                SpanContext.invalid,
                StatusData.unset(),
                0,
                Attributes.empty(),
                listOf(),
                listOf(),
                0,
                0,
                0,
                0,
                InstrumentationLibraryInfo.empty(),
                Resource.empty(),
                false
            )
    ) {

        /**
         * Create a new SpanData instance from the data in this.
         *
         * @return a new SpanData instance
         */
        fun build(): TestSpanData {
            return internalTestSpanData
        }

        /**
         * Set the `SpanContext` on this builder.
         *
         * @param spanContext the `SpanContext`.
         * @return this builder (for chaining).
         */
        fun setSpanContext(spanContext: SpanContext): Builder {
            return Builder(internalTestSpanData.copy(spanContext = spanContext))
        }

        /**
         * The parent span context associated for this span, which may be null.
         *
         * @param parentSpanContext the SpanId of the parent
         * @return this.
         */
        fun setParentSpanContext(parentSpanContext: SpanContext): Builder {
            return Builder(internalTestSpanData.copy(parentSpanContext = parentSpanContext))
        }

        /**
         * Set the [Resource] associated with this span. Must not be null.
         *
         * @param resource the Resource that generated this span.
         * @return this
         */
        fun setResource(resource: Resource): Builder {
            return Builder(internalTestSpanData.copy(resource = resource))
        }

        /**
         * Sets the instrumentation library of the tracer which created this span. Must not be null.
         *
         * @param instrumentationLibraryInfo the instrumentation library of the tracer which created
         * this span.
         * @return this
         */
        fun setInstrumentationLibraryInfo(
            instrumentationLibraryInfo: InstrumentationLibraryInfo
        ): Builder {
            return Builder(
                internalTestSpanData.copy(instrumentationLibraryInfo = instrumentationLibraryInfo)
            )
        }

        /**
         * Set the name of the span. Must not be null.
         *
         * @param name the name.
         * @return this
         */
        fun setName(name: String): Builder {
            return Builder(internalTestSpanData.copy(name = name))
        }

        /**
         * Set the start timestamp of the span.
         *
         * @param epochNanos the start epoch timestamp in nanos.
         * @return this
         */
        fun setStartEpochNanos(epochNanos: Long): Builder {
            return Builder(internalTestSpanData.copy(startEpochNanos = epochNanos))
        }

        /**
         * Set the end timestamp of the span.
         *
         * @param epochNanos the end epoch timestamp in nanos.
         * @return this
         */
        fun setEndEpochNanos(epochNanos: Long): Builder {
            return Builder(internalTestSpanData.copy(endEpochNanos = epochNanos))
        }

        /**
         * Set the attributes that are associated with this span, in the form of [Attributes].
         *
         * @param attributes [Attributes] for this span.
         * @return this
         * @see Attributes
         */
        fun setAttributes(attributes: Attributes): Builder {
            return Builder(internalTestSpanData.copy(attributes = attributes))
        }

        /**
         * Set timed events that are associated with this span. Must not be null, may be empty.
         *
         * @param events A List&lt;Event&gt; of events associated with this span.
         * @return this
         * @see EventData
         */
        fun setEvents(events: List<EventData>): Builder {
            return Builder(internalTestSpanData.copy(events = events))
        }

        /**
         * Set the status for this span. Must not be null.
         *
         * @param status The Status of this span.
         * @return this
         */
        fun setStatus(status: StatusData): Builder {
            return Builder(internalTestSpanData.copy(status = status))
        }

        /**
         * Set the kind of span. Must not be null.
         *
         * @param kind The Kind of span.
         * @return this
         */
        fun setKind(kind: SpanKind): Builder {
            return Builder(internalTestSpanData.copy(kind = kind))
        }

        /**
         * Set the links associated with this span. Must not be null, may be empty.
         *
         * @param links A List&lt;Link&gt;
         * @return this
         */
        fun setLinks(links: List<LinkData>): Builder {
            return Builder(internalTestSpanData.copy(links = links))
        }

        /**
         * Sets to true if the span has been ended.
         *
         * @param hasEnded A boolean indicating if the span has been ended.
         * @return this
         */
        fun setHasEnded(hasEnded: Boolean): Builder {
            return Builder(internalTestSpanData.copy(internalHasEnded = hasEnded))
        }

        /**
         * Set the total number of events recorded on this span.
         *
         * @param totalRecordedEvents The total number of events recorded.
         * @return this
         */
        fun setTotalRecordedEvents(totalRecordedEvents: Int): Builder {
            return Builder(internalTestSpanData.copy(totalRecordedEvents = totalRecordedEvents))
        }

        /**
         * Set the total number of links recorded on this span.
         *
         * @param totalRecordedLinks The total number of links recorded.
         * @return this
         */
        fun setTotalRecordedLinks(totalRecordedLinks: Int): Builder {
            return Builder(internalTestSpanData.copy(totalRecordedLinks = totalRecordedLinks))
        }

        /**
         * Set the total number of attributes recorded on this span.
         *
         * @param totalAttributeCount The total number of attributes recorded.
         * @return this
         */
        fun setTotalAttributeCount(totalAttributeCount: Int): Builder {
            return Builder(internalTestSpanData.copy(totalAttributeCount = totalAttributeCount))
        }
    }

    companion object {
        /**
         * Creates a new Builder for creating an SpanData instance.
         *
         * @return a new Builder.
         */
        fun builder(): Builder {
            return Builder()
                .setSpanContext(SpanContext.invalid)
                .setParentSpanContext(SpanContext.invalid)
                .setInstrumentationLibraryInfo(InstrumentationLibraryInfo.empty())
                .setLinks(emptyList())
                .setTotalRecordedLinks(0)
                .setAttributes(Attributes.empty())
                .setEvents(emptyList())
                .setTotalRecordedEvents(0)
                .setResource(Resource.empty())
                .setTotalAttributeCount(0)
        }
    }
}
