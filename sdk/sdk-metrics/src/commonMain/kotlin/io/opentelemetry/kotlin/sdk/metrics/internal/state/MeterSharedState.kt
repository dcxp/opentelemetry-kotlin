/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.opentelemetry.kotlin.api.metrics.ObservableDoubleMeasurement
import io.opentelemetry.kotlin.api.metrics.ObservableLongMeasurement
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.export.CollectionInfo
import io.opentelemetry.kotlin.sdk.metrics.view.View

/**
 * State for a `Meter`.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
class MeterSharedState(
    val instrumentationLibraryInfo: InstrumentationLibraryInfo,
    val metricStorageRegistry: MetricStorageRegistry
) {

    /** Collects all accumulated metric stream points. */
    fun collectAll(
        collectionInfo: CollectionInfo,
        meterProviderSharedState: MeterProviderSharedState,
        epochNanos: Long,
        suppressSynchronousCollection: Boolean
    ): List<MetricData> {
        return metricStorageRegistry
            .metrics
            .map {
                it.collectAndReset(
                    collectionInfo,
                    meterProviderSharedState.resource,
                    instrumentationLibraryInfo,
                    meterProviderSharedState.startEpochNanos,
                    epochNanos,
                    suppressSynchronousCollection
                )
            }
            .filterNotNull()
    }

    /** Registers new synchronous storage associated with a given instrument. */
    fun registerSynchronousMetricStorage(
        instrument: InstrumentDescriptor,
        meterProviderSharedState: MeterProviderSharedState
    ): WriteableMetricStorage {
        val views: List<View> =
            meterProviderSharedState.viewRegistry.findViews(instrument, instrumentationLibraryInfo)
        val storage: MutableList<WriteableMetricStorage> = mutableListOf()
        for (view in views) {
            val currentStorage: SynchronousMetricStorage =
                SynchronousMetricStorage.create<Any>(
                    view,
                    instrument,
                    meterProviderSharedState.exemplarFilter
                )
            // TODO - move this in a better location.
            if (SynchronousMetricStorage.empty() == currentStorage) {
                continue
            }
            try {
                storage.add(metricStorageRegistry.register(currentStorage))
            } catch (e: DuplicateMetricStorageException) {
                /*logger.log(
                    java.util.logging.Level.WARNING,
                    e,
                    Supplier<String> {
                        DebugUtils
                            .duplicateMetricErrorMessage(e)
                    }
                )*/
            }
        }
        return if (storage.size == 1) {
            storage[0]
        } else MultiWritableMetricStorage(storage)
        // If the size is 0, we return an, effectively, no-op writer.
    }

    /** Registers new asynchronous storage associated with a given `long` instrument. */
    fun registerLongAsynchronousInstrument(
        instrument: InstrumentDescriptor,
        meterProviderSharedState: MeterProviderSharedState,
        metricUpdater: (ObservableLongMeasurement) -> Unit
    ) {
        // TODO - we should avoid registering independent storage that calls observables over and
        // over.
        val views: List<View> =
            meterProviderSharedState.viewRegistry.findViews(instrument, instrumentationLibraryInfo)
        for (view in views) {
            val currentStorage: MetricStorage =
                AsynchronousMetricStorage.Companion.longAsynchronousAccumulator<Any>(
                    view,
                    instrument,
                    metricUpdater
                ) // TODO - move this in a better location.
            if (AsynchronousMetricStorage.Companion.empty() == currentStorage) {
                continue
            }
            try {
                metricStorageRegistry.register<MetricStorage>(currentStorage)
            } catch (e: DuplicateMetricStorageException) {
                /*logger.log( java.util.logging.Level.WARNING, e,
                Supplier<String> { "Failed to register metric." } )*/
            }
        }
    }
    /** Registers new asynchronous storage associated with a given `double` instrument. */
    fun registerDoubleAsynchronousInstrument(
        instrument: InstrumentDescriptor,
        meterProviderSharedState: MeterProviderSharedState,
        metricUpdater: (ObservableDoubleMeasurement) -> Unit
    ) {
        // TODO - we should avoid registering independent storage that calls observables over and
        // over.
        val views: List<View> =
            meterProviderSharedState.viewRegistry.findViews(instrument, instrumentationLibraryInfo)
        for (view in views) {
            val currentStorage: MetricStorage =
                AsynchronousMetricStorage.Companion.doubleAsynchronousAccumulator<Any>(
                    view,
                    instrument,
                    metricUpdater
                )
            // TODO - move this in a better location.
            if (AsynchronousMetricStorage.Companion.empty() === currentStorage) {
                continue
            }
            try {
                metricStorageRegistry.register<MetricStorage>(currentStorage)
            } catch (e: DuplicateMetricStorageException) {
                /*logger.log( java.util.logging.Level.WARNING, e,
                Supplier<String> { "Failed to register metric." } )*/
            }
        }
    }

    companion object {
        // private val logger: java.util.logging.Logger =
        //    java.util.logging.Logger.getLogger(MeterSharedState::class.java.getName())

        fun create(instrumentationLibraryInfo: InstrumentationLibraryInfo): MeterSharedState {
            return MeterSharedState(instrumentationLibraryInfo, MetricStorageRegistry())
        }
    }
}
