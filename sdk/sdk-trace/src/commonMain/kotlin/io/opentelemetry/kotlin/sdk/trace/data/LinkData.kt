/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.data

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.trace.SpanContext
import io.opentelemetry.kotlin.sdk.trace.SpanLimits

/**
 * Data representation of a link.
 *
 * Used (for example) in batching operations, where a single batch handler processes multiple
 * requests from different traces. Link can be also used to reference spans from the same trace.
 */
interface LinkData {
    /** Returns the [SpanContext] of the span this [LinkData] refers to. */
    val spanContext: SpanContext

    /**
     * Returns the set of attributes.
     *
     * @return the set of attributes.
     */
    val attributes: Attributes

    /**
     * The total number of attributes that were recorded on this Link. This number may be larger
     * than the number of attributes that are attached to this span, if the total number recorded
     * was greater than the configured maximum value. See: [ ]
     * [SpanLimits.getMaxNumberOfAttributesPerLink]
     *
     * @return The number of attributes on this link.
     */
    val totalAttributeCount: Int

    companion object {
        /**
         * Returns a new immutable [LinkData].
         *
         * @param spanContext the [SpanContext] of this [LinkData].
         * @return a new immutable [LinkData]
         */
        fun create(spanContext: SpanContext): LinkData {
            return ImmutableLinkData.create(spanContext)
        }

        /**
         * Returns a new immutable [LinkData].
         *
         * @param spanContext the [SpanContext] of this [LinkData].
         * @param attributes the attributes of this [LinkData].
         * @return a new immutable [LinkData]
         */
        fun create(spanContext: SpanContext, attributes: Attributes): LinkData {
            return ImmutableLinkData.create(spanContext, attributes)
        }

        /**
         * Returns a new immutable [LinkData].
         *
         * @param spanContext the [SpanContext] of this [LinkData].
         * @param attributes the attributes of this [LinkData].
         * @param totalAttributeCount the total number of attributed for this [LinkData].
         * @return a new immutable [LinkData]
         */
        fun create(
            spanContext: SpanContext,
            attributes: Attributes,
            totalAttributeCount: Int
        ): LinkData {
            return ImmutableLinkData.create(spanContext, attributes, totalAttributeCount)
        }
    }
}
