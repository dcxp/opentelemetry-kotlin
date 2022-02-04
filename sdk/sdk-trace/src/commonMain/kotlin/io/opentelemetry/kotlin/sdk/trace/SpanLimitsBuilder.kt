/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

/** Builder for [SpanLimits]. */
class SpanLimitsBuilder internal constructor() {
    private var maxNumAttributes = DEFAULT_SPAN_MAX_NUM_ATTRIBUTES
    private var maxNumEvents = DEFAULT_SPAN_MAX_NUM_EVENTS
    private var maxNumLinks = DEFAULT_SPAN_MAX_NUM_LINKS
    private var maxNumAttributesPerEvent = DEFAULT_SPAN_MAX_NUM_ATTRIBUTES_PER_EVENT
    private var maxNumAttributesPerLink = DEFAULT_SPAN_MAX_NUM_ATTRIBUTES_PER_LINK
    private var maxAttributeValueLength: Int = SpanLimits.maxAttributeValueLength

    /**
     * Sets the max number of attributes per [Span].
     *
     * @param maxNumberOfAttributes the max number of attributes per [Span]. Must be positive.
     * @return this.
     * @throws IllegalArgumentException if `maxNumberOfAttributes` is not positive.
     */
    fun setMaxNumberOfAttributes(maxNumberOfAttributes: Int): SpanLimitsBuilder {
        require(maxNumberOfAttributes > 0) { "maxNumberOfAttributes must be greater than 0" }
        maxNumAttributes = maxNumberOfAttributes
        return this
    }

    /**
     * Sets the max number of events per [Span].
     *
     * @param maxNumberOfEvents the max number of events per [Span]. Must be positive.
     * @return this.
     * @throws IllegalArgumentException if `maxNumberOfEvents` is not positive.
     */
    fun setMaxNumberOfEvents(maxNumberOfEvents: Int): SpanLimitsBuilder {
        require(maxNumberOfEvents > 0) { "maxNumberOfEvents must be greater than 0" }
        maxNumEvents = maxNumberOfEvents
        return this
    }

    /**
     * Sets the max number of links per [Span].
     *
     * @param maxNumberOfLinks the max number of links per [Span]. Must be positive.
     * @return this.
     * @throws IllegalArgumentException if `maxNumberOfLinks` is not positive.
     */
    fun setMaxNumberOfLinks(maxNumberOfLinks: Int): SpanLimitsBuilder {
        require(maxNumberOfLinks > 0) { "maxNumberOfLinks must be greater than 0" }
        maxNumLinks = maxNumberOfLinks
        return this
    }

    /**
     * Sets the max number of attributes per event.
     *
     * @param maxNumberOfAttributesPerEvent the max number of attributes per event. Must be
     * positive.
     * @return this.
     * @throws IllegalArgumentException if `maxNumberOfAttributesPerEvent` is not positive.
     */
    fun setMaxNumberOfAttributesPerEvent(maxNumberOfAttributesPerEvent: Int): SpanLimitsBuilder {
        require(maxNumberOfAttributesPerEvent > 0) {
            "maxNumberOfAttributesPerEvent must be greater than 0"
        }
        maxNumAttributesPerEvent = maxNumberOfAttributesPerEvent
        return this
    }

    /**
     * Sets the max number of attributes per link.
     *
     * @param maxNumberOfAttributesPerLink the max number of attributes per link. Must be positive.
     * @return this.
     * @throws IllegalArgumentException if `maxNumberOfAttributesPerLink` is not positive.
     */
    fun setMaxNumberOfAttributesPerLink(maxNumberOfAttributesPerLink: Int): SpanLimitsBuilder {
        require(maxNumberOfAttributesPerLink > 0) {
            "maxNumberOfAttributesPerLink must be greater than 0"
        }
        maxNumAttributesPerLink = maxNumberOfAttributesPerLink
        return this
    }

    /**
     * Sets the max number of characters for string attribute values. For string array attribute
     * values, applies to each entry individually.
     *
     * @param maxAttributeValueLength the max number of characters for attribute strings. Must not
     * be negative.
     * @return this.
     * @throws IllegalArgumentException if `maxAttributeValueLength` is negative.
     */
    fun setMaxAttributeValueLength(maxAttributeValueLength: Int): SpanLimitsBuilder {
        require(maxAttributeValueLength > -1) { "maxAttributeValueLength must be greater than 0" }
        this.maxAttributeValueLength = maxAttributeValueLength
        return this
    }

    /** Builds and returns a [SpanLimits] with the values of this builder. */
    fun build(): io.opentelemetry.kotlin.sdk.trace.SpanLimits {
        return io.opentelemetry.kotlin.sdk.trace.SpanLimits.Companion.create(
            maxNumAttributes,
            maxNumEvents,
            maxNumLinks,
            maxNumAttributesPerEvent,
            maxNumAttributesPerLink,
            maxAttributeValueLength
        )
    }

    companion object {
        private const val DEFAULT_SPAN_MAX_NUM_ATTRIBUTES = 128
        private const val DEFAULT_SPAN_MAX_NUM_EVENTS = 128
        private const val DEFAULT_SPAN_MAX_NUM_LINKS = 128
        private const val DEFAULT_SPAN_MAX_NUM_ATTRIBUTES_PER_EVENT = 128
        private const val DEFAULT_SPAN_MAX_NUM_ATTRIBUTES_PER_LINK = 128
    }
}
