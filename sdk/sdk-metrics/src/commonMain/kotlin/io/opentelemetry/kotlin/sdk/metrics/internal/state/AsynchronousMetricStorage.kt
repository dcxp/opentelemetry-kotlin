/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.metrics.ObservableDoubleMeasurement
import io.opentelemetry.kotlin.api.metrics.ObservableLongMeasurement
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarFilter
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.Aggregator
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.export.CollectionInfo
import io.opentelemetry.kotlin.sdk.metrics.internal.view.AttributesProcessor
import io.opentelemetry.kotlin.sdk.metrics.view.View
import io.opentelemetry.kotlin.sdk.resources.Resource
import kotlinx.coroutines.Runnable

/**
 * Stores aggregated [MetricData] for asynchronous instruments.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
class AsynchronousMetricStorage<T>
private constructor(
    override val metricDescriptor: MetricDescriptor,
    aggregator: Aggregator<T>,
    private val asyncAccumulator: AsyncAccumulator<T>,
    private val metricUpdater: Runnable
) : MetricStorage {
    private val storage: TemporalMetricStorage<T> = TemporalMetricStorage<T>(aggregator, false)

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
        metricUpdater.run()
        return storage.buildMetricFor(
            collectionInfo.collector,
            resource,
            instrumentationLibraryInfo,
            metricDescriptor,
            temporality,
            asyncAccumulator.collectAndReset(),
            startEpochNanos,
            epochNanos
        )
    }

    /** Helper class to record async measurements on demand. */
    private class AsyncAccumulator<T>(instrument: InstrumentDescriptor) {
        private val instrument: InstrumentDescriptor
        private var currentAccumulation: MutableMap<Attributes, T> = HashMap()

        init {
            this.instrument = instrument
        }

        fun record(attributes: Attributes, accumulation: T) {
            if (currentAccumulation.size >= MetricStorageUtils.MAX_ACCUMULATIONS) {
                /*logger.log(
                    java.util.logging.Level.WARNING,
                    "Instrument " +
                        instrument.name +
                        " has exceeded the maximum allowed accumulations (" +
                        MetricStorageUtils
                            .MAX_ACCUMULATIONS +
                        ")."
                )*/
                return
            }
            // TODO: error on metric overwrites
            currentAccumulation[attributes] = accumulation
        }

        fun collectAndReset(): Map<Attributes, T> {
            val result: Map<Attributes, T> = currentAccumulation
            currentAccumulation = HashMap()
            return result
        }
    }

    companion object {
        /*private val logger: ThrottlingLogger =
        ThrottlingLogger(
            java.util.logging.Logger.getLogger(
                DeltaMetricStorage::class.java
                    .getName()
            )
        )*/

        /** Constructs asynchronous metric storage which stores nothing. */
        fun empty(): MetricStorage {
            return EmptyMetricStorage.Companion.INSTANCE
        }

        /** Constructs storage for `double` valued instruments. */
        fun <T> doubleAsynchronousAccumulator(
            view: View,
            instrument: InstrumentDescriptor,
            metricUpdater: (ObservableDoubleMeasurement) -> Unit
        ): MetricStorage {
            val metricDescriptor: MetricDescriptor = MetricDescriptor.create(view, instrument)
            val aggregator: Aggregator<T> =
                view.aggregation.createAggregator(instrument, ExemplarFilter.neverSample())
            val measurementAccumulator = AsyncAccumulator<T>(instrument)
            if (Aggregator.empty() === aggregator) {
                return empty()
            }
            val attributesProcessor: AttributesProcessor = view.attributesProcessor
            // TODO: Find a way to grab the measurement JUST ONCE for all async metrics.
            val result: ObservableDoubleMeasurement =
                object : ObservableDoubleMeasurement {
                    override fun observe(value: Double, attributes: Attributes) {
                        val result =
                            aggregator.accumulateDoubleMeasurement(
                                value,
                                attributes,
                                Context.current()
                            )
                        result?.let {
                            measurementAccumulator.record(
                                attributesProcessor.process(attributes, Context.current()),
                                it
                            )
                        }
                    }

                    override fun observe(value: Double) {
                        observe(value, Attributes.empty())
                    }
                }
            return AsynchronousMetricStorage(
                metricDescriptor,
                aggregator,
                measurementAccumulator,
                Runnable { metricUpdater(result) }
            )
        }

        /** Constructs storage for `long` valued instruments. */
        fun <T> longAsynchronousAccumulator(
            view: View,
            instrument: InstrumentDescriptor,
            metricUpdater: (ObservableLongMeasurement) -> Unit
        ): MetricStorage {
            val metricDescriptor: MetricDescriptor = MetricDescriptor.create(view, instrument)
            val aggregator: Aggregator<T> =
                view.aggregation.createAggregator(instrument, ExemplarFilter.neverSample())
            val measurementAccumulator = AsyncAccumulator<T>(instrument)
            val attributesProcessor: AttributesProcessor = view.attributesProcessor
            // TODO: Find a way to grab the measurement JUST ONCE for all async metrics.
            val result: ObservableLongMeasurement =
                object : ObservableLongMeasurement {
                    override fun observe(value: Long, attributes: Attributes) {
                        val result =
                            aggregator.accumulateLongMeasurement(
                                value,
                                attributes,
                                Context.current()
                            )
                        result?.let {
                            measurementAccumulator.record(
                                attributesProcessor.process(attributes, Context.current()),
                                it
                            )
                        }
                    }

                    override fun observe(value: Long) {
                        observe(value, Attributes.empty())
                    }
                }
            return AsynchronousMetricStorage(
                metricDescriptor,
                aggregator,
                measurementAccumulator,
                Runnable { metricUpdater(result) }
            )
        }
    }
}
