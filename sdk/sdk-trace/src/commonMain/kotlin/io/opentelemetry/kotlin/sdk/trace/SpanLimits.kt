/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

/**
 * Class that holds limits enforced during span recording.
 *
 * Note: To allow dynamic updates of [SpanLimits] you should register a [ ] with [ ]
 * [io.opentelemetry.kotlin.sdk.trace.SdkTracerProviderBuilder.setSpanLimits] which supplies dynamic
 * configs when queried.
 */
abstract class SpanLimits internal constructor() {
    /** Create an instance. */
    /**
     * Returns the max number of attributes per [Span].
     *
     * @return the max number of attributes per [Span].
     */
    abstract val maxNumberOfAttributes: Int

    /**
     * Returns the max number of events per [Span].
     *
     * @return the max number of events per `Span`.
     */
    abstract val maxNumberOfEvents: Int

    /**
     * Returns the max number of links per [Span].
     *
     * @return the max number of links per `Span`.
     */
    abstract val maxNumberOfLinks: Int

    /**
     * Returns the max number of attributes per event.
     *
     * @return the max number of attributes per event.
     */
    abstract val maxNumberOfAttributesPerEvent: Int

    /**
     * Returns the max number of attributes per link.
     *
     * @return the max number of attributes per link.
     */
    abstract val maxNumberOfAttributesPerLink: Int

    /**
     * Override [SpanLimits.getMaxAttributeValueLength] to be abstract so autovalue can implement
     * it.
     */
    abstract val maxAttributeValueLength: Int

    /**
     * Returns a [SpanLimitsBuilder] initialized to the same property values as the current
     * instance.
     *
     * @return a [SpanLimitsBuilder] initialized to the same property values as the current
     * instance.
     */
    fun toBuilder(): SpanLimitsBuilder {
        return SpanLimitsBuilder()
            .setMaxNumberOfAttributes(maxNumberOfAttributes)
            .setMaxNumberOfEvents(maxNumberOfEvents)
            .setMaxNumberOfLinks(maxNumberOfLinks)
            .setMaxNumberOfAttributesPerEvent(maxNumberOfAttributesPerEvent)
            .setMaxNumberOfAttributesPerLink(maxNumberOfAttributesPerLink)
            .setMaxAttributeValueLength(maxAttributeValueLength)
    }

    companion object {
        /**
         * Returns the max number of characters for string attribute values. For string array
         * attribute values, applies to each entry individually.
         *
         * @return the max number of characters for attribute strings.
         */
        const val maxAttributeValueLength = Int.MAX_VALUE
        /** Returns the default [SpanLimits]. */
        val default: SpanLimits = SpanLimitsBuilder().build()

        /** Returns a new [SpanLimitsBuilder] to construct a [SpanLimits]. */
        fun builder(): SpanLimitsBuilder {
            return SpanLimitsBuilder()
        }

        fun create(
            maxNumAttributes: Int,
            maxNumEvents: Int,
            maxNumLinks: Int,
            maxNumAttributesPerEvent: Int,
            maxNumAttributesPerLink: Int,
            maxAttributeLength: Int
        ): SpanLimits {
            return Implementation(
                maxNumAttributes,
                maxNumEvents,
                maxNumLinks,
                maxNumAttributesPerEvent,
                maxNumAttributesPerLink,
                maxAttributeLength
            )
        }

        private class Implementation(
            override val maxNumberOfAttributes: Int,
            override val maxNumberOfEvents: Int,
            override val maxNumberOfLinks: Int,
            override val maxNumberOfAttributesPerEvent: Int,
            override val maxNumberOfAttributesPerLink: Int,
            override val maxAttributeValueLength: Int
        ) : SpanLimits()
    }
}
