/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.data

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.trace.SpanContext

internal object ImmutableLinkData {
    private val DEFAULT_ATTRIBUTE_COLLECTION = Attributes.empty()
    private const val DEFAULT_ATTRIBUTE_COUNT = 0
    fun create(spanContext: SpanContext): LinkData {
        return Implementation(spanContext, DEFAULT_ATTRIBUTE_COLLECTION, DEFAULT_ATTRIBUTE_COUNT)
    }

    fun create(spanContext: SpanContext, attributes: Attributes): LinkData {
        return Implementation(spanContext, attributes, attributes.size)
    }

    fun create(
        spanContext: SpanContext,
        attributes: Attributes,
        totalAttributeCount: Int
    ): LinkData {
        return Implementation(spanContext, attributes, totalAttributeCount)
    }

    private class Implementation(
        override val spanContext: SpanContext,
        override val attributes: Attributes,
        override val totalAttributeCount: Int
    ) : LinkData {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Implementation

            if (spanContext != other.spanContext) return false
            if (attributes != other.attributes) return false
            if (totalAttributeCount != other.totalAttributeCount) return false

            return true
        }

        override fun hashCode(): Int {
            var result = spanContext.hashCode()
            result = 31 * result + attributes.hashCode()
            result = 31 * result + totalAttributeCount
            return result
        }

        override fun toString(): String {
            return "Implementation(spanContext=$spanContext, attributes=$attributes, totalAttributeCount=$totalAttributeCount)"
        }
    }
}
