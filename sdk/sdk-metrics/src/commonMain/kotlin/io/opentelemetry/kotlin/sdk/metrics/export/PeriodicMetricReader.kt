/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.export

import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

/**
 * Wraps a [MetricExporter] and automatically reads and exports the metrics every export interval.
 * Metrics may also be dropped when it becomes time to export again, and there is an export in
 * progress.
 */
class PeriodicMetricReader
internal constructor(
    producer: MetricProducer,
    exporter: MetricExporter,
) : MetricReader {
    private val producer: MetricProducer
    private val exporter: MetricExporter
    private val scheduled = Scheduled()

    private val scheduledFuture = atomic<Job?>(null)

    init {
        this.producer = producer
        this.exporter = exporter
    }

    override val supportedTemporality: Set<AggregationTemporality>
        get() = exporter.supportedTemporality
    override val preferredTemporality: AggregationTemporality?
        get() = exporter.preferredTemporality

    override fun flush(): CompletableResultCode {
        return scheduled.doRun()
    }

    override fun shutdown(): CompletableResultCode {
        this.scheduledFuture.value?.cancel()
        val lastRun = flush()
        val shutdownResult = scheduled.shutdown()
        return CompletableResultCode.ofAll(listOf(lastRun, shutdownResult))
    }
    fun start(intervalNanos: Long) {
        start(intervalNanos.nanoseconds)
    }
    fun start(interval: Duration) {
        scheduledFuture.update { it ?: scheduled.scheduleAtFixedRate(interval, interval) }
    }

    private inner class Scheduled constructor() : Runnable {
        private val exportAvailable = atomic(true)

        override fun run() {
            // Ignore the CompletableResultCode from doRun() in order to keep run() asynchronous
            doRun()
        }

        // Runs a collect + export cycle.
        fun doRun(): CompletableResultCode {
            val flushResult = CompletableResultCode()
            if (exportAvailable.compareAndSet(true, false)) {
                try {
                    val result: CompletableResultCode =
                        exporter.export(producer.collectAllMetrics())
                    result.whenComplete {
                        /*if (!result.isSuccess) {
                            logger.log(java.util.logging.Level.FINE, "Exporter failed")
                        }*/
                        flushResult.succeed()
                        exportAvailable.lazySet(true)
                    }
                } catch (t: Throwable) {
                    exportAvailable.lazySet(true)
                    // logger.log(java.util.logging.Level.WARNING, "Exporter threw an Exception", t)
                    flushResult.fail()
                }
            } else {
                // logger.log(java.util.logging.Level.FINE, "Exporter busy. Dropping metrics.")
                flushResult.fail()
            }
            return flushResult
        }

        fun shutdown(): CompletableResultCode {
            return exporter.shutdown()
        }
    }

    companion object {
        // private val logger: java.util.logging.Logger =
        //    java.util.logging.Logger.getLogger(PeriodicMetricReader::class.java.getName())

        /**
         * Returns a new [MetricReaderFactory] which can be registered to a [ ] to start a
         * [PeriodicMetricReader] exporting once every minute on a new daemon thread.
         */
        fun newMetricReaderFactory(exporter: MetricExporter): MetricReaderFactory {
            return builder(exporter).newMetricReaderFactory()
        }

        /** Returns a new [PeriodicMetricReaderBuilder]. */
        fun builder(exporter: MetricExporter): PeriodicMetricReaderBuilder {
            return PeriodicMetricReaderBuilder(exporter)
        }
    }
}

fun Runnable.scheduleAtFixedRate(initialDelay: Duration, period: Duration): Job {
    val runnable = this
    val job =
        CoroutineScope(Dispatchers.Unconfined).launch {
            delay(initialDelay)
            while (isActive) {
                runnable.run()
                delay(period)
            }
        }
    return job
}
