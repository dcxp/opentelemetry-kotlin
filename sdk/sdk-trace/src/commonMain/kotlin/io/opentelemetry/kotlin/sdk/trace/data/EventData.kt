/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.data

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.trace.SpanLimits

/** Data representation of a event. */
interface EventData {
    /**
     * Return the name of the [EventData].
     *
     * @return the name of the [EventData].
     */
    val name: String

    /**
     * Return the attributes of the [EventData].
     *
     * @return the attributes of the [EventData].
     */
    val attributes: Attributes

    /**
     * Returns the epoch time in nanos of this event.
     *
     * @return the epoch time in nanos of this event.
     */
    val epochNanos: Long

    /**
     * The total number of attributes that were recorded on this Event. This number may be larger
     * than the number of attributes that are attached to this span, if the total number recorded
     * was greater than the configured maximum value. See: [ ]
     * [SpanLimits.getMaxNumberOfAttributesPerEvent]
     *
     * @return The total number of attributes on this event.
     */
    val totalAttributeCount: Int

    /**
     * Returns the dropped attributes count of this event.
     *
     * @return the dropped attributes count of this event.
     */
    val droppedAttributesCount: Int
        get() = totalAttributeCount - attributes.size

    companion object {
        /**
         * Returns a new immutable [EventData].
         *
         * @param epochNanos epoch timestamp in nanos of the [EventData].
         * @param name the name of the [EventData].
         * @param attributes the attributes of the [EventData].
         * @return a new immutable [EventData]
         */
        fun create(epochNanos: Long, name: String, attributes: Attributes?): EventData? {
            return create(epochNanos, name, attributes)
        }

        /**
         * Returns a new immutable [EventData].
         *
         * @param epochNanos epoch timestamp in nanos of the [EventData].
         * @param name the name of the [EventData].
         * @param attributes the attributes of the [EventData].
         * @param totalAttributeCount the total number of attributes for this `` Event.
         * @return a new immutable [EventData]
         */
        fun create(
            epochNanos: Long,
            name: String,
            attributes: Attributes,
            totalAttributeCount: Int
        ): EventData {
            return ImmutableEventData.create(epochNanos, name, attributes, totalAttributeCount)
        }
    }
}
