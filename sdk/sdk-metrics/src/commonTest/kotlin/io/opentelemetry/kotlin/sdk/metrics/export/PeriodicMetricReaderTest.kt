/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.export

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.KotlinTarget
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.LongPointData
import io.opentelemetry.kotlin.sdk.metrics.data.LongSumData
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.resources.Resource
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.getAndUpdate
import kotlinx.atomicfu.update
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class PeriodicMetricReaderTest {
    private val metricProducer =
        object : MetricProducer {
            override fun collectAllMetrics(): Collection<MetricData> {
                return listOf(METRIC_DATA)
            }
        }

    private val metricExporter =
        object : MetricExporter {
            override fun export(metrics: Collection<MetricData>): CompletableResultCode {
                return CompletableResultCode.ofSuccess()
            }

            override fun flush(): CompletableResultCode {
                return CompletableResultCode.ofSuccess()
            }

            override fun shutdown(): CompletableResultCode {
                return CompletableResultCode.ofSuccess()
            }
        }
    /*
        TODO: fix missing ScheduledExecutorService
        @Test
        fun startOnlyOnce() {
            val scheduler: ScheduledExecutorService = mock(ScheduledExecutorService::class.java)
            val mock: ScheduledFuture = mock(ScheduledFuture::class.java)
            `when`(scheduler.scheduleAtFixedRate(any(), anyLong(), anyLong(), any())).thenReturn(mock)
            val factory: MetricReaderFactory = PeriodicMetricReader.builder(metricExporter)
                .setInterval(java.time.Duration.ofMillis(1))
                .setExecutor(scheduler)
                .newMetricReaderFactory()

            // Starts the interval reader.
            factory.apply(metricProducer!!)
            verify(scheduler, times(1)).scheduleAtFixedRate(any(), anyLong(), anyLong(), any())
        }
    */
    @Test
    fun periodicExport() = runTest( timeout = 200.seconds ) {
        if (KotlinTarget.isNative() || KotlinTarget.isJs()) {
            // This test has a deadlock on native and js so skip it
            // TODO: fix deadlock
            return@runTest
        }
        val waitingMetricExporter = WaitingMetricExporter()
        val factory: MetricReaderFactory =
            PeriodicMetricReader.builder(waitingMetricExporter)
                .setInterval(100.milliseconds)
                .newMetricReaderFactory()
        val reader = factory.apply(metricProducer)
        try {
            waitingMetricExporter.waitForNumberOfExports(1) shouldContainExactly
                listOf(listOf(METRIC_DATA))
            waitingMetricExporter.waitForNumberOfExports(2) shouldContainExactly
                listOf(listOf(METRIC_DATA), listOf(METRIC_DATA))
        } finally {
            reader.shutdown()
        }
    }
    @Test
    fun flush() = runTest( timeout = 200.seconds ) {
        val waitingMetricExporter = WaitingMetricExporter()
        val factory: MetricReaderFactory =
            PeriodicMetricReader.builder(waitingMetricExporter)
                .setInterval(Long.MAX_VALUE.milliseconds)
                .newMetricReaderFactory()
        val reader = factory.apply(metricProducer)
        reader.flush().join(10.seconds).isSuccess.shouldBeTrue()
        try {
            waitingMetricExporter.waitForNumberOfExports(1) shouldContainExactly
                listOf(listOf(METRIC_DATA))
        } finally {
            reader.shutdown()
        }
    }

    @Test
    fun intervalExport_exporterThrowsException() = runTest( timeout = 200.seconds ) {
        if (KotlinTarget.isNative() || KotlinTarget.isJs()) {
            // This test has a deadlock on native so skip it
            // TODO: fix deadlock
            return@runTest
        }
        val waitingMetricExporter = WaitingMetricExporter(/* shouldThrow=*/ true)
        val factory: MetricReaderFactory =
            PeriodicMetricReader.builder(waitingMetricExporter)
                .setInterval(100.milliseconds)
                .newMetricReaderFactory()
        val reader = factory.apply(metricProducer)
        try {
            waitingMetricExporter.waitForNumberOfExports(2) shouldContainExactly
                listOf(listOf(METRIC_DATA), listOf(METRIC_DATA))
        } finally {
            reader.shutdown()
        }
    }

    @Test
    fun oneLastExportAfterShutdown() = runTest( timeout = 200.seconds ) {
        val waitingMetricExporter = WaitingMetricExporter()
        val factory: MetricReaderFactory =
            PeriodicMetricReader.builder(waitingMetricExporter)
                .setInterval(100.seconds)
                .newMetricReaderFactory()
        val reader = factory.apply(metricProducer)
        // Assume that this will be called in less than 100 seconds.
        reader.shutdown()

        // This export was called during shutdown.
        waitingMetricExporter.waitForNumberOfExports(1) shouldContainExactly
            listOf(listOf(METRIC_DATA))
        waitingMetricExporter.hasShutdown.value.shouldBeTrue()
    }

    @Test
    // Testing the overload
    fun invalidConfig() {
        shouldThrow<IllegalArgumentException> {
                PeriodicMetricReader.builder(metricExporter).setInterval(-1, DateTimeUnit.SECOND)
            }
            .message shouldBe "interval must be positive"
        shouldThrow<IllegalArgumentException> {
                PeriodicMetricReader.builder(metricExporter).setInterval(-1.seconds)
            }
            .message shouldBe "interval must be positive"
    }

    private class WaitingMetricExporter(private val shouldThrow: Boolean = false) : MetricExporter {
        val hasShutdown = atomic(false)
        private val queue = atomic(persistentListOf<List<MetricData>>())
        private val exportTimes = atomic(persistentListOf<Long>())
        override val supportedTemporality: Set<AggregationTemporality>
            get() = AggregationTemporality.values().toSet()
        override val preferredTemporality: AggregationTemporality?
            get() = null

        override fun export(metricList: Collection<MetricData>): CompletableResultCode {
            exportTimes.update { it.add(Clock.System.now().toEpochMilliseconds()) }
            queue.update { it.add(metricList.toList()) }
            if (shouldThrow) {
                throw RuntimeException("Export Failed!")
            }
            return CompletableResultCode.ofSuccess()
        }

        override fun flush(): CompletableResultCode {
            return CompletableResultCode.ofSuccess()
        }

        override fun shutdown(): CompletableResultCode {
            hasShutdown.lazySet(true)
            return CompletableResultCode.ofSuccess()
        }

        /**
         * Waits until export is called for numberOfExports times. Returns the list of exported
         * lists of metrics.
         */
        suspend fun waitForNumberOfExports(numberOfExports: Int): List<List<MetricData>> {
            val result: MutableList<List<MetricData>> = mutableListOf()
            while (result.size < numberOfExports) {
                if (queue.value.isEmpty()) {
                    delay(10)
                    continue
                }
                val export: List<MetricData> = queue.getAndUpdate { it.removeAt(0) }[0]
                result.add(export)
            }
            return result
        }
    }

    companion object {
        private val LONG_POINT_LIST =
            listOf(LongPointData.create(1000, 3000, Attributes.empty(), 1234567))
        private val METRIC_DATA =
            MetricData.createLongSum(
                Resource.empty(),
                InstrumentationLibraryInfo.create("IntervalMetricReaderTest", null),
                "my metric",
                "my metric description",
                "us",
                LongSumData.create(true, AggregationTemporality.CUMULATIVE, LONG_POINT_LIST)
            )
    }
}
