/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.sdk.metrics.export
/*
import io.opentelemetry.sdk.common.CompletableResultCode
import io.opentelemetry.sdk.metrics.data.AggregationTemporality
import kotlin.jvm.Volatile

/**
 * Wraps a [MetricExporter] and automatically reads and exports the metrics every export
 * interval. Metrics may also be dropped when it becomes time to export again, and there is an
 * export in progress.
 */
class PeriodicMetricReader internal constructor(
    producer: MetricProducer,
    exporter: MetricExporter,
    scheduler: ScheduledExecutorService?
) : MetricReader {
    private val producer: MetricProducer
    private val exporter: MetricExporter
    private val scheduler: ScheduledExecutorService?
    private val scheduled: Scheduled
    private val lock = Any()

    @Nullable
    @Volatile
    private var scheduledFuture: ScheduledFuture<*>? = null

    init {
        this.producer = producer
        this.exporter = exporter
        this.scheduler = scheduler
        scheduled = Scheduled()
    }

    override val supportedTemporality: EnumSet<AggregationTemporality>
        get() = exporter.getSupportedTemporality()
    override val preferredTemporality: AggregationTemporality?
        get() = exporter.getPreferredTemporality()

    override fun flush(): CompletableResultCode {
        return scheduled.doRun()
    }

    override fun shutdown(): CompletableResultCode {
        val result = CompletableResultCode()
        val scheduledFuture: ScheduledFuture<*>? = scheduledFuture
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false)
        }
        scheduler.shutdown()
        try {
            scheduler.awaitTermination(5, TimeUnit.SECONDS)
            val flushResult = scheduled.doRun()
            flushResult.join(5, TimeUnit.SECONDS)
        } catch (e: InterruptedException) {
            // force a shutdown if the export hasn't finished.
            scheduler.shutdownNow()
            // reset the interrupted status
            java.lang.Thread.currentThread().interrupt()
        } finally {
            val shutdownResult = scheduled.shutdown()
            shutdownResult.whenComplete(
                kotlin.jvm.functions.Function1 < ?
                super kotlin . coroutines . Continuation <? super kotlin . Unit >, ? extends java.lang.Object>< in kotlin.coroutines.Continuation<? super kotlin.Unit>< in kotlin.Unit?>?, out Any?> ({
                if (!shutdownResult.isSuccess) {
                    result.fail()
                } else {
                    result.succeed()
                }
            }))
        }
        return result
    }

    fun start(intervalNanos: Long) {
        synchronized(lock) {
            if (scheduledFuture != null) {
                return
            }
            scheduledFuture = scheduler.scheduleAtFixedRate(
                scheduled, intervalNanos, intervalNanos, TimeUnit.NANOSECONDS
            )
        }
    }

    private inner class Scheduled private constructor() : Runnable {
        private val exportAvailable: java.util.concurrent.atomic.AtomicBoolean =
            java.util.concurrent.atomic.AtomicBoolean(true)

        override fun run() {
            // Ignore the CompletableResultCode from doRun() in order to keep run() asynchronous
            doRun()
        }

        // Runs a collect + export cycle.
        fun doRun(): CompletableResultCode {
            val flushResult = CompletableResultCode()
            if (exportAvailable.compareAndSet(true, false)) {
                try {
                    val result: CompletableResultCode = exporter.export(producer.collectAllMetrics())
                    result.whenComplete(
                        kotlin.jvm.functions.Function1 < ?
                        super kotlin . coroutines . Continuation <? super kotlin . Unit >, ? extends java.lang.Object>< in kotlin.coroutines.Continuation<? super kotlin.Unit>< in kotlin.Unit?>?, out Any?> ({
                        if (!result.isSuccess) {
                            logger.log(java.util.logging.Level.FINE, "Exporter failed")
                        }
                        flushResult.succeed()
                        exportAvailable.set(true)
                    }))
                } catch (t: Throwable) {
                    exportAvailable.set(true)
                    logger.log(java.util.logging.Level.WARNING, "Exporter threw an Exception", t)
                    flushResult.fail()
                }
            } else {
                logger.log(java.util.logging.Level.FINE, "Exporter busy. Dropping metrics.")
                flushResult.fail()
            }
            return flushResult
        }

        fun shutdown(): CompletableResultCode {
            return exporter.shutdown()
        }
    }

    companion object {
        private val logger: java.util.logging.Logger =
            java.util.logging.Logger.getLogger(PeriodicMetricReader::class.java.getName())

        /**
         * Returns a new [MetricReaderFactory] which can be registered to a [ ] to start a [PeriodicMetricReader]
         * exporting once every minute on a new daemon thread.
         */
        fun newMetricReaderFactory(exporter: MetricExporter): MetricReaderFactory {
            return builder(exporter).newMetricReaderFactory()
        }

        /** Returns a new [PeriodicMetricReaderBuilder].  */
        fun builder(exporter: MetricExporter): PeriodicMetricReaderBuilder {
            return PeriodicMetricReaderBuilder(exporter)
        }
    }
}
*/
