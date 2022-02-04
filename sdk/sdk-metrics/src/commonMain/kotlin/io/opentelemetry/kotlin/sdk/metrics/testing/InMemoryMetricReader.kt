/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.testing

import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.metrics.export.MetricProducer
import io.opentelemetry.kotlin.sdk.metrics.export.MetricReader
import io.opentelemetry.kotlin.sdk.metrics.export.MetricReaderFactory
import kotlinx.atomicfu.atomic

/**
 * A [MetricReader] implementation that can be used to test OpenTelemetry integration.
 *
 * Can be created using `InMemoryMetricReader.create(sdkMeterProvider)`
 *
 * Example usage:
 *
 * <pre>` public class InMemoryMetricReaderExample { private final InMemoryMetricReader reader =
 * InMemoryMetricReader.create(); private final SdkMeterProvider sdkMeterProvider =
 * SdkMeterProvider.builder().registerMetricReader(reader).build(); private final Meter meter =
 * sdkMeterProvider.get("example"); private final LongCounter metricCallCount =
 * meter.counterBuilder("num_collects");
 *
 * public void printMetrics() { metricCallCount.add(1);
 * System.out.println(reader.collectAllMetrics()); }
 *
 * public static void main(String[] args) { InMemoryMetricReaderExample example = new
 * InMemoryMetricReaderExample(); example.printMetrics(); } } `</pre> *
 */
class InMemoryMetricReader
private constructor(override val preferredTemporality: AggregationTemporality) :
    MetricReader, MetricReaderFactory {

    private val metricProducer = atomic<MetricProducer?>(null)

    /** Returns all metrics accumulated since the last call. */
    fun collectAllMetrics(): Collection<MetricData> {
        val metricProducer = metricProducer
        return metricProducer.value?.collectAllMetrics() ?: emptyList()
    }

    override val supportedTemporality: Set<AggregationTemporality>
        get() =
            setOf<AggregationTemporality>(
                AggregationTemporality.CUMULATIVE,
                AggregationTemporality.DELTA
            )

    override fun flush(): CompletableResultCode {
        val metricProducer = metricProducer
        metricProducer.value?.collectAllMetrics()
        return CompletableResultCode.ofSuccess()
    }

    override fun shutdown(): CompletableResultCode {
        return CompletableResultCode.ofSuccess()
    }

    override fun apply(producer: MetricProducer): MetricReader {
        metricProducer.lazySet(producer)
        return this
    }

    companion object {
        /** Returns a new [InMemoryMetricReader]. */
        fun create(): InMemoryMetricReader {
            return InMemoryMetricReader(AggregationTemporality.CUMULATIVE)
        }

        /** Creates a new [InMemoryMetricReader] that prefers DELTA aggregation. */
        fun createDelta(): InMemoryMetricReader {
            return InMemoryMetricReader(AggregationTemporality.DELTA)
        }
    }
}
