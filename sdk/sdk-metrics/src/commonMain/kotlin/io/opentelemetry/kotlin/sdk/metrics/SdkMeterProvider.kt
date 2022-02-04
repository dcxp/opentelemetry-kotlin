/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.opentelemetry.kotlin.Closeable
import io.opentelemetry.kotlin.Supplier
import io.opentelemetry.kotlin.api.metrics.MeterBuilder
import io.opentelemetry.kotlin.api.metrics.MeterProvider
import io.opentelemetry.kotlin.sdk.common.Clock
import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.internal.ComponentRegistry
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarFilter
import io.opentelemetry.kotlin.sdk.metrics.export.MetricProducer
import io.opentelemetry.kotlin.sdk.metrics.export.MetricReader
import io.opentelemetry.kotlin.sdk.metrics.export.MetricReaderFactory
import io.opentelemetry.kotlin.sdk.metrics.internal.export.CollectionHandle
import io.opentelemetry.kotlin.sdk.metrics.internal.export.CollectionInfo
import io.opentelemetry.kotlin.sdk.metrics.internal.state.MeterProviderSharedState
import io.opentelemetry.kotlin.sdk.metrics.internal.view.ViewRegistry
import io.opentelemetry.kotlin.sdk.resources.Resource
import kotlinx.atomicfu.AtomicLong
import kotlinx.atomicfu.atomic

/** SDK implementation for [MeterProvider]. */
class SdkMeterProvider
internal constructor(
    readerFactories: List<MetricReaderFactory>,
    clock: Clock,
    resource: Resource,
    viewRegistry: ViewRegistry,
    exemplarSampler: ExemplarFilter,
    minimumCollectionIntervalNanos: Long
) : MeterProvider, Closeable {
    private val registry: ComponentRegistry<SdkMeter>
    internal val sharedState: MeterProviderSharedState
    private val collectionInfoMap: Map<CollectionHandle, CollectionInfo>
    private val isClosed = atomic(false)
    private val lastCollectionTimestamp: AtomicLong
    private val minimumCollectionIntervalNanos: Long

    init {
        sharedState =
            MeterProviderSharedState.create(clock, resource, viewRegistry, exemplarSampler)
        registry =
            ComponentRegistry { instrumentationLibraryInfo ->
                SdkMeter(sharedState, instrumentationLibraryInfo)
            }
        lastCollectionTimestamp = atomic(clock.nanoTime() - minimumCollectionIntervalNanos)

        this.minimumCollectionIntervalNanos = minimumCollectionIntervalNanos

        // Here we construct our own unique handle ids for this SDK.
        // These are guaranteed to be unique per-reader for this SDK, and only this SDK.
        // These are *only* mutated in our constructor, and safe to use concurrently after
        // construction.
        val collectors: MutableSet<CollectionHandle> = CollectionHandle.mutableSet()
        val handleSupplier: Supplier<CollectionHandle> = CollectionHandle.createSupplier()
        collectionInfoMap =
            readerFactories.associate { readerFactory ->
                val handle: CollectionHandle = handleSupplier.get()
                collectors.add(handle)
                val reader: MetricReader = readerFactory.apply(LeasedMetricProducer(handle))
                val collectionInfo = CollectionInfo.create(handle, collectors, reader)
                handle to collectionInfo
            }
    }

    override fun meterBuilder(instrumentationName: String): MeterBuilder {
        var newInstrumentationName: String = instrumentationName
        if (newInstrumentationName.isEmpty()) {
            // LOGGER.fine("Meter requested without instrumentation name.")
            newInstrumentationName = DEFAULT_METER_NAME
        }
        return SdkMeterBuilder(registry, newInstrumentationName)
    }

    /**
     * Call [MetricReader.flush] on all metric readers associated with this provider. The resulting
     * [CompletableResultCode] completes when all complete.
     */
    fun forceFlush(): CompletableResultCode {
        val results =
            collectionInfoMap.values.map { collectionInfo -> collectionInfo.reader.flush() }
        return CompletableResultCode.ofAll(results)
    }

    /**
     * Shutdown the provider. Calls [MetricReader.shutdown] on all metric readers associated with
     * this provider. The resulting [CompletableResultCode] completes when all complete.
     */
    fun shutdown(): CompletableResultCode {
        if (!isClosed.compareAndSet(false, true)) {
            // LOGGER.info("Multiple close calls")
            return CompletableResultCode.ofSuccess()
        }
        val results =
            collectionInfoMap.values.map { collectionInfo -> collectionInfo.reader.shutdown() }
        return CompletableResultCode.ofAll(results)
    }

    /** Close the meter provider. Calls [.shutdown] and blocks waiting for it to complete. */
    override fun close() {
        shutdown()
    }

    /** Helper class to expose registered metric exports. */
    private inner class LeasedMetricProducer(private val handle: CollectionHandle) :
        MetricProducer {

        override fun collectAllMetrics(): Collection<MetricData> {
            val meters: Collection<io.opentelemetry.kotlin.sdk.metrics.SdkMeter> =
                registry.components
            // Suppress too-frequent-collection.
            val currentNanoTime: Long = sharedState.clock.nanoTime()
            val pastNanoTime: Long = lastCollectionTimestamp.value
            // It hasn't been long enough since the last collection.
            val disableSynchronousCollection =
                currentNanoTime - pastNanoTime < minimumCollectionIntervalNanos
            // If we're not disabling metrics, write the current collection time.
            // We don't care if this happens in more than one thread, suppression is optimistic, and
            // the
            // interval is small enough some jitter isn't important.
            if (!disableSynchronousCollection) {
                lastCollectionTimestamp.lazySet(currentNanoTime)
            }
            val result =
                meters
                    .map { meter ->
                        meter.collectAll(
                            collectionInfoMap[handle]!!,
                            sharedState.clock.now(),
                            disableSynchronousCollection
                        )
                    }
                    .flatten()
            return result
        }
    }

    companion object {
        // private val LOGGER: java.util.logging.Logger =
        //   java.util.logging.Logger.getLogger(SdkMeterProvider::class.java.getName())
        const val DEFAULT_METER_NAME = "unknown"

        /**
         * Returns a new [SdkMeterProviderBuilder] for [SdkMeterProvider].
         *
         * @return a new [SdkMeterProviderBuilder] for [SdkMeterProvider].
         */
        fun builder(): SdkMeterProviderBuilder {
            return SdkMeterProviderBuilder()
        }
    }
}
