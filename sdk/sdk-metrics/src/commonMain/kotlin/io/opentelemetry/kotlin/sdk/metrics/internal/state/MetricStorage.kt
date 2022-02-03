/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.export.CollectionInfo
import io.opentelemetry.kotlin.sdk.resources.Resource

/**
 * Stores collected [MetricData].
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
interface MetricStorage {
    /** Returns a description of the metric produced in this storage. */
    val metricDescriptor: MetricDescriptor

    /**
     * Collects the metrics from this storage and resets for the next collection period.
     *
     * Note: This is a stateful operation and will reset any interval-related state for the
     * `collector`.
     *
     * @param collectionInfo The identity of the current reader of metrics and other information.
     * @param resource The resource associated with the metrics.
     * @param instrumentationLibraryInfo The instrumentation library generating the metrics.
     * @param startEpochNanos The start timestamp for this SDK.
     * @param epochNanos The timestamp for this collection.
     * @param suppressSynchronousCollection Whether or not to suppress active (blocking) collection
     * of metrics, meaning recently collected data is "fresh enough"
     * @return The [MetricData] from this collection period, or `null`.
     */
    fun collectAndReset(
        collectionInfo: CollectionInfo,
        resource: Resource,
        instrumentationLibraryInfo: InstrumentationLibraryInfo,
        startEpochNanos: Long,
        epochNanos: Long,
        suppressSynchronousCollection: Boolean
    ): MetricData?
}
