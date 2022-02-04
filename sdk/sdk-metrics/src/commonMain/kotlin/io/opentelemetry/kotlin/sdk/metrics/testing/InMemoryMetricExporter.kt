/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.testing

import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.metrics.export.MetricExporter
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.collections.immutable.persistentListOf

/**
 * A [MetricExporter] implementation that can be used to test OpenTelemetry integration.
 *
 * Can be created using `InMemoryMetricExporter.create()`
 *
 * Example usage:
 *
 * <pre>` public class InMemoryMetricExporterExample {
 *
 * // creating InMemoryMetricExporter private final InMemoryMetricExporter exporter =
 * InMemoryMetricExporter.create(); private final MeterSdkProvider meterSdkProvider =
 * OpenTelemetrySdk.getMeterProvider(); private final Meter meter =
 * meterSdkProvider.get("InMemoryMetricExporterExample"); private IntervalMetricReader
 * intervalMetricReader;
 *
 * void setup() { intervalMetricReader = IntervalMetricReader.builder() .setMetricExporter(exporter)
 * .setMetricProducers(Collections.singletonList(meterSdkProvider.getMetricProducer()))
 * .setExportIntervalMillis(1000) .build(); }
 *
 * LongCounter generateLongCounterMeter(String name) { return
 * meter.longCounterBuilder(name).setDescription("Sample LongCounter").build(); }
 *
 * public static void main(String[] args) throws InterruptedException {
 * InMemoryMetricExporterExample example = new InMemoryMetricExporterExample(); example.setup();
 * example.generateLongCounterMeter("counter-1"); } } `</pre> *
 */
class InMemoryMetricExporter private constructor() : MetricExporter {
    private val finishedMetricItems = atomic(persistentListOf<MetricData>())
    private val isStopped = atomic(false)

    /**
     * Returns a `List` of the finished `Metric`s, represented by `MetricData`.
     *
     * @return a `List` of the finished `Metric`s.
     */
    fun getFinishedMetricItems(): List<MetricData> {
        return finishedMetricItems.value
    }

    /**
     * Clears the internal `List` of finished `Metric`s.
     *
     * Does not reset the state of this exporter if already shutdown.
     */
    fun reset() {
        finishedMetricItems.update { it.clear() }
    }

    override val supportedTemporality: Set<AggregationTemporality>
        get() = setOf(AggregationTemporality.CUMULATIVE, AggregationTemporality.DELTA)
    override val preferredTemporality: AggregationTemporality
        get() = AggregationTemporality.CUMULATIVE

    /**
     * Exports the collection of `Metric`s into the inmemory queue.
     *
     * If this is called after `shutdown`, this will return `ResultCode.FAILURE`.
     */
    override fun export(metrics: Collection<MetricData>): CompletableResultCode {
        if (isStopped.value) {
            return CompletableResultCode.ofFailure()
        }
        finishedMetricItems.update { it.addAll(metrics) }
        return CompletableResultCode.ofSuccess()
    }

    /**
     * The InMemory exporter does not batch metrics, so this method will immediately return with
     * success.
     *
     * @return always Success
     */
    override fun flush(): CompletableResultCode {
        return CompletableResultCode.ofSuccess()
    }

    /**
     * Clears the internal `List` of finished `Metric`s.
     *
     * Any subsequent call to export() function on this MetricExporter, will return
     * `CompletableResultCode.ofFailure()`
     */
    override fun shutdown(): CompletableResultCode {
        isStopped.lazySet(true)
        reset()
        return CompletableResultCode.ofSuccess()
    }

    companion object {
        /**
         * Returns a new instance of the `InMemoryMetricExporter`.
         *
         * @return a new instance of the `InMemoryMetricExporter`.
         */
        fun create(): InMemoryMetricExporter {
            return InMemoryMetricExporter()
        }
    }
}
