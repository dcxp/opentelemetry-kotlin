/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.export

import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.export.MetricReader

/**
 * Information about a [MetricReader] used when collecting metrics.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
interface CollectionInfo {
    /** The current collection. */
    val collector: CollectionHandle

    /** The set of all possible collectors. */
    val allCollectors: Set<CollectionHandle>
    val reader: MetricReader

    /** The set of supported temporalities for the current collection. */
    val supportedAggregation: Set<AggregationTemporality>
        get() = reader.supportedTemporality

    /** The preferred aggregation, if any, for the current metric collection. */
    val preferredAggregation: AggregationTemporality?
        get() = reader.preferredTemporality

    companion object {
        /**
         * Construct a new collection info object storing information for collection against a
         * reader.
         */
        fun create(
            handle: CollectionHandle,
            allCollectors: Set<CollectionHandle>,
            reader: MetricReader
        ): CollectionInfo {
            return Instance(handle, allCollectors, reader)
        }

        data class Instance(
            override val collector: CollectionHandle,
            override val allCollectors: Set<CollectionHandle>,
            override val reader: MetricReader
        ) : CollectionInfo
    }
}
