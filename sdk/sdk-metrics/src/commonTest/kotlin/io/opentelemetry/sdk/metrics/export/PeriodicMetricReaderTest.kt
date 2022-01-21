/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.sdk.metrics.export












/*
internal class PeriodicMetricReaderTest {
    private val metricProducer = object : MetricProducer{
        override fun collectAllMetrics(): Collection<MetricData> {
            return listOf(METRIC_DATA)
        }

    }

    private val metricExporter = object : MetricExporter{
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

    @Test
    @Throws(java.lang.Exception::class)
    fun periodicExport() {
        val waitingMetricExporter = WaitingMetricExporter()
        val factory: MetricReaderFactory = PeriodicMetricReader.builder(waitingMetricExporter)
            .setInterval(java.time.Duration.ofMillis(100))
            .newMetricReaderFactory()
        val reader = factory.apply(metricProducer!!)
        try {
            assertThat(waitingMetricExporter.waitForNumberOfExports(1))
                .containsExactly(listOf(METRIC_DATA))
            assertThat(waitingMetricExporter.waitForNumberOfExports(2))
                .containsExactly(listOf(METRIC_DATA), listOf(METRIC_DATA))
        } finally {
            reader.shutdown()
        }
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun flush() {
        val waitingMetricExporter = WaitingMetricExporter()
        val factory: MetricReaderFactory = PeriodicMetricReader.builder(waitingMetricExporter)
            .setInterval(java.time.Duration.ofNanos(Long.MAX_VALUE))
            .newMetricReaderFactory()
        val reader = factory.apply(metricProducer!!)
        assertThat(reader.flush().join(10, TimeUnit.SECONDS).isSuccess()).isTrue()
        try {
            assertThat(waitingMetricExporter.waitForNumberOfExports(1))
                .containsExactly(listOf(METRIC_DATA))
        } finally {
            reader.shutdown()
        }
    }

    @Test
    @Timeout(2)
    @Throws(java.lang.Exception::class)
    fun intervalExport_exporterThrowsException() {
        val waitingMetricExporter = WaitingMetricExporter( /* shouldThrow=*/true)
        val factory: MetricReaderFactory = PeriodicMetricReader.builder(waitingMetricExporter)
            .setInterval(java.time.Duration.ofMillis(100))
            .newMetricReaderFactory()
        val reader = factory.apply(metricProducer!!)
        try {
            assertThat(waitingMetricExporter.waitForNumberOfExports(2))
                .containsExactly(listOf(METRIC_DATA), listOf(METRIC_DATA))
        } finally {
            reader.shutdown()
        }
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun oneLastExportAfterShutdown() {
        val waitingMetricExporter = WaitingMetricExporter()
        val factory: MetricReaderFactory = PeriodicMetricReader.builder(waitingMetricExporter)
            .setInterval(java.time.Duration.ofSeconds(100))
            .newMetricReaderFactory()
        val reader = factory.apply(metricProducer!!)
        // Assume that this will be called in less than 100 seconds.
        reader.shutdown()

        // This export was called during shutdown.
        assertThat(waitingMetricExporter.waitForNumberOfExports(1))
            .containsExactly(listOf(METRIC_DATA))
        assertThat(waitingMetricExporter.hasShutdown.get()).isTrue()
    }

    @Test
    fun  // Testing the overload
            invalidConfig() {
        assertThatThrownBy { PeriodicMetricReader.builder(metricExporter).setInterval(1, null) }
            .isInstanceOf(NullPointerException::class.java)
            .hasMessage("unit")
        assertThatThrownBy { PeriodicMetricReader.builder(metricExporter).setInterval(-1, TimeUnit.MILLISECONDS) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("interval must be positive")
        assertThatThrownBy { PeriodicMetricReader.builder(metricExporter).setInterval(null) }
            .isInstanceOf(NullPointerException::class.java)
            .hasMessage("interval")
        assertThatThrownBy { PeriodicMetricReader.builder(metricExporter).setExecutor(null) }
            .isInstanceOf(NullPointerException::class.java)
            .hasMessage("executor")
    }

    private class WaitingMetricExporter(private val shouldThrow: Boolean = false) : MetricExporter {
        val hasShutdown: java.util.concurrent.atomic.AtomicBoolean = java.util.concurrent.atomic.AtomicBoolean(false)
        private val queue: BlockingQueue<List<MetricData>> = LinkedBlockingQueue<List<MetricData>>()
        private val exportTimes: MutableList<Long> = Collections.synchronizedList<Long>(java.util.ArrayList<Long>())
        override val supportedTemporality: EnumSet<AggregationTemporality>
            get() = EnumSet.allOf<AggregationTemporality>(AggregationTemporality::class.java)
        override val preferredTemporality: AggregationTemporality?
            get() = null

        fun export(metricList: Collection<MetricData?>?): CompletableResultCode {
            exportTimes.add(java.lang.System.currentTimeMillis())
            queue.offer(java.util.ArrayList<MetricData>(metricList))
            if (shouldThrow) {
                throw RuntimeException("Export Failed!")
            }
            return CompletableResultCode.ofSuccess()
        }

        override fun flush(): CompletableResultCode {
            return CompletableResultCode.ofSuccess()
        }

        override fun shutdown(): CompletableResultCode {
            hasShutdown.set(true)
            return CompletableResultCode.ofSuccess()
        }

        /**
         * Waits until export is called for numberOfExports times. Returns the list of exported lists of
         * metrics.
         */
        @Nullable
        @Throws(java.lang.Exception::class)
        fun waitForNumberOfExports(numberOfExports: Int): List<List<MetricData>> {
            val result: MutableList<List<MetricData>> = java.util.ArrayList<List<MetricData>>()
            while (result.size < numberOfExports) {
                val export: List<MetricData> = queue.poll(5, TimeUnit.SECONDS)
                assertThat(export).isNotNull()
                result.add(export)
            }
            return result
        }
    }

    companion object {
        private val LONG_POINT_LIST = listOf(LongPointData.create(1000, 3000, Attributes.empty(), 1234567))
        private val METRIC_DATA = MetricData.createLongSum(
            Resource.empty(),
            InstrumentationLibraryInfo.create("IntervalMetricReaderTest", null),
            "my metric",
            "my metric description",
            "us",
            LongSumData.create( /* isMonotonic= */
                true, AggregationTemporality.CUMULATIVE, LONG_POINT_LIST
            )
        )
    }
}*/
