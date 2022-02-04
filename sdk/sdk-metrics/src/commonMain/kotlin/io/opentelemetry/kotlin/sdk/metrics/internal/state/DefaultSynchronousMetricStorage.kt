/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.Aggregator
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.export.CollectionInfo
import io.opentelemetry.kotlin.sdk.metrics.internal.view.AttributesProcessor
import io.opentelemetry.kotlin.sdk.resources.Resource

/**
 * Stores aggregated [MetricData] for synchronous instruments.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
class DefaultSynchronousMetricStorage<T>
internal constructor(
    override val metricDescriptor: MetricDescriptor,
    aggregator: Aggregator<T>,
    private val attributesProcessor: AttributesProcessor
) : SynchronousMetricStorage {
    private val deltaMetricStorage: DeltaMetricStorage<T> =
        DeltaMetricStorage<T>(aggregator, metricDescriptor.sourceInstrument)
    private val temporalMetricStorage: TemporalMetricStorage<T> =
        TemporalMetricStorage<T>(aggregator, /* isSynchronous= */ true)

    // This is a storage handle to use when the attributes processor requires
    private val lateBoundStorageHandle: BoundStorageHandle =
        object : BoundStorageHandle {
            override fun release() {}
            override fun recordLong(value: Long, attributes: Attributes, context: Context) {
                this@DefaultSynchronousMetricStorage.recordLong(value, attributes, context)
            }

            override fun recordDouble(value: Double, attributes: Attributes, context: Context) {
                this@DefaultSynchronousMetricStorage.recordDouble(value, attributes, context)
            }
        }

    override fun bind(attributes: Attributes): BoundStorageHandle {
        return if (attributesProcessor.usesContext()) {
            // We cannot pre-bind attributes because we need to pull attributes from context.
            lateBoundStorageHandle
        } else deltaMetricStorage.bind(attributesProcessor.process(attributes, Context.current()))
    }

    // Overridden to make sure attributes processor can pull baggage.
    override fun recordLong(value: Long, attributes: Attributes, context: Context) {
        var attributes = attributes
        attributes = attributesProcessor.process(attributes, context)
        val handle: BoundStorageHandle = deltaMetricStorage.bind(attributes)
        try {
            handle.recordLong(value, attributes, context)
        } finally {
            handle.release()
        }
    }

    // Overridden to make sure attributes processor can pull baggage.
    override fun recordDouble(value: Double, attributes: Attributes, context: Context) {
        var attributes = attributes
        attributes = attributesProcessor.process(attributes, context)
        val handle: BoundStorageHandle = deltaMetricStorage.bind(attributes)
        try {
            handle.recordDouble(value, attributes, context)
        } finally {
            handle.release()
        }
    }

    override fun collectAndReset(
        collectionInfo: CollectionInfo,
        resource: Resource,
        instrumentationLibraryInfo: InstrumentationLibraryInfo,
        startEpochNanos: Long,
        epochNanos: Long,
        suppressSynchronousCollection: Boolean
    ): MetricData? {
        val temporality: AggregationTemporality =
            TemporalityUtils.resolveTemporality(
                collectionInfo.supportedAggregation,
                collectionInfo.preferredAggregation
            )
        val result: Map<Attributes, T> =
            deltaMetricStorage.collectFor(
                collectionInfo.collector,
                collectionInfo.allCollectors,
                suppressSynchronousCollection
            )
        return temporalMetricStorage.buildMetricFor(
            collectionInfo.collector,
            resource,
            instrumentationLibraryInfo,
            metricDescriptor,
            temporality,
            result,
            startEpochNanos,
            epochNanos
        )
    }
}
