/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.export.CollectionInfo
import io.opentelemetry.kotlin.sdk.resources.Resource

internal class EmptyMetricStorage private constructor() : SynchronousMetricStorage {
    private val descriptor: MetricDescriptor = MetricDescriptor.create("", "", "")
    private val emptyHandle: BoundStorageHandle =
        object : BoundStorageHandle {
            override fun recordLong(value: Long, attributes: Attributes, context: Context) {}
            override fun recordDouble(value: Double, attributes: Attributes, context: Context) {}
            override fun release() {}
        }
    override val metricDescriptor: MetricDescriptor
        get() = descriptor

    override fun bind(attributes: Attributes): BoundStorageHandle {
        return emptyHandle
    }

    override fun collectAndReset(
        collectionInfo: CollectionInfo,
        resource: Resource,
        instrumentationLibraryInfo: InstrumentationLibraryInfo,
        startEpochNanos: Long,
        epochNanos: Long,
        suppressSynchronousCollection: Boolean
    ): MetricData? {
        return null
    }

    companion object {
        val INSTANCE = EmptyMetricStorage()
    }
}
