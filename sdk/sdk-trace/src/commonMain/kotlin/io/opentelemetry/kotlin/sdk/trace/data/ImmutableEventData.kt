/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.data

import io.opentelemetry.kotlin.api.common.Attributes

/** An immutable implementation of the [EventData]. */
internal object ImmutableEventData {

    /**
     * Returns a new immutable `Event`.
     *
     * @param epochNanos epoch timestamp in nanos of the `Event`.
     * @param name the name of the `Event`.
     * @param attributes the attributes of the `Event`.
     * @return a new immutable `Event<T>`
     */
    fun create(
        epochNanos: Long,
        name: String,
        attributes: Attributes,
        totalAttributeCount: Int = attributes.size
    ): EventData {
        return Implementation(name, attributes, epochNanos, totalAttributeCount)
    }

    class Implementation(
        override val name: String,
        override val attributes: Attributes,
        override val epochNanos: Long,
        override val totalAttributeCount: Int
    ) : EventData {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Implementation

            if (name != other.name) return false
            if (attributes != other.attributes) return false
            if (epochNanos != other.epochNanos) return false
            if (totalAttributeCount != other.totalAttributeCount) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + attributes.hashCode()
            result = 31 * result + epochNanos.hashCode()
            result = 31 * result + totalAttributeCount
            return result
        }
    }
}
