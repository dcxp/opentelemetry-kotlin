/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.export

import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.metrics.BoundLongCounter
import io.opentelemetry.kotlin.api.metrics.GlobalMeterProvider
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.Clock
import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.trace.ReadWriteSpan
import io.opentelemetry.kotlin.sdk.trace.ReadableSpan
import io.opentelemetry.kotlin.sdk.trace.SpanProcessor
import io.opentelemetry.kotlin.sdk.trace.data.SpanData
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit

/**
 * Implementation of the [SpanProcessor] that batches spans exported by the SDK then pushes them to
 * the exporter pipeline.
 *
 * All spans reported by the SDK implementation are first added to a synchronized queue (with a
 * `maxQueueSize` maximum size, if queue is full spans are dropped). Spans are exported either when
 * there are `maxExportBatchSize` pending spans or `scheduleDelayNanos` has passed since the last
 * export finished.
 */
class BatchSpanProcessor
internal constructor(
    spanExporter: SpanExporter,
    scheduleDelayNanos: Long,
    maxQueueSize: Int,
    maxExportBatchSize: Int,
    exporterTimeoutNanos: Long
) : SpanProcessor {
    private val worker: Worker
    private val isShutdown = atomic(false)

    init {
        worker =
            Worker(
                spanExporter,
                scheduleDelayNanos,
                maxExportBatchSize,
                exporterTimeoutNanos,
                Channel(capacity = maxQueueSize)
            )
    }

    override fun onStart(parentContext: Context, span: ReadWriteSpan) {}

    override fun isStartRequired(): Boolean {
        return true
    }

    override fun onEnd(span: ReadableSpan) {
        if (!span.spanContext.isSampled()) {
            return
        }
        worker.addSpan(span)
    }

    override fun isEndRequired(): Boolean {
        return true
    }

    override fun shutdown(): CompletableResultCode {
        return if (isShutdown.getAndSet(true)) {
            CompletableResultCode.ofSuccess()
        } else worker.shutdown()
    }

    override fun forceFlush(): CompletableResultCode {
        return worker.forceFlush()
    }

    // Visible for testing
    val batch: List<SpanData>
        get() = worker.batch

    // Worker is a thread that batches multiple spans and calls the registered SpanExporter to
    // export
    // the data.
    private class Worker(
        private val spanExporter: SpanExporter,
        private val scheduleDelayNanos: Long,
        private val maxExportBatchSize: Int,
        private val exporterTimeoutNanos: Long,
        private val queue: Channel<ReadableSpan>
    ) {
        private val droppedSpans: BoundLongCounter
        private val exportedSpans: BoundLongCounter
        private val nextExportTime = atomic(0L)
        private val queueCounter = atomic(0)

        // When waiting on the spans queue, exporter thread sets this atomic to the number of more
        // spans it needs before doing an export. Writer threads would then wait for the queue to
        // reach
        // spansNeeded size before notifying the exporter thread about new entries.
        // Integer.MAX_VALUE is used to imply that exporter thread is not expecting any signal.
        // Since
        // exporter thread doesn't expect any signal initially, this value is initialized to
        // Integer.MAX_VALUE.
        private val spansNeeded = atomic(Int.MAX_VALUE)
        private val signal: Channel<Boolean> = Channel(capacity = 1)
        private val flushRequested = atomic<CompletableResultCode?>(null)
        private val continueWork = atomic(true)
        private val batchInternal = atomic(persistentListOf<SpanData>())

        val batch: List<SpanData>
            get() = batchInternal.value

        init {
            val meter =
                GlobalMeterProvider.get().meterBuilder("io.opentelemetry.kotlin.sdk.trace").build()
            val processedSpansCounter =
                meter
                    .counterBuilder("processedSpans")
                    .setUnit("1")
                    .setDescription(
                        "The number of spans processed by the BatchSpanProcessor. " +
                            "[dropped=true if they were dropped due to high throughput]"
                    )
                    .build()
            droppedSpans =
                processedSpansCounter.bind(
                    Attributes.of(
                        SPAN_PROCESSOR_TYPE_LABEL,
                        SPAN_PROCESSOR_TYPE_VALUE,
                        SPAN_PROCESSOR_DROPPED_LABEL,
                        true
                    )
                )
            exportedSpans =
                processedSpansCounter.bind(
                    Attributes.of(
                        SPAN_PROCESSOR_TYPE_LABEL,
                        SPAN_PROCESSOR_TYPE_VALUE,
                        SPAN_PROCESSOR_DROPPED_LABEL,
                        false
                    )
                )
            CoroutineScope(Dispatchers.Unconfined).launch { run() }.start()
        }

        fun addSpan(span: ReadableSpan) {
            val result = queue.trySend(span)
            result.onFailure { droppedSpans.add(1) }
            result.onSuccess {
                val count = queueCounter.incrementAndGet()
                if (count >= spansNeeded.value) {
                    signal.trySend(true)
                }
            }
        }

        suspend fun run() {
            updateNextExportTime()
            while (continueWork.value) {
                if (flushRequested.value != null) {
                    flush()
                }
                while (!queue.isEmpty && batchInternal.value.size < maxExportBatchSize) {
                    val spanData = queue.receive().toSpanData()
                    addSpanDataToBatch(spanData)
                    queueCounter.decrementAndGet()
                }
                if (batchInternal.value.size >= maxExportBatchSize ||
                        Clock.default.nanoTime() >= nextExportTime.value
                ) {
                    exportCurrentBatch()
                    updateNextExportTime()
                }
                if (queue.isEmpty) {
                    val pollWaitTime: Long = nextExportTime.value - Clock.default.nanoTime()
                    if (pollWaitTime > 0) {
                        spansNeeded.lazySet(maxExportBatchSize - batchInternal.value.size)
                        CoroutineScope(Dispatchers.Unconfined)
                            .launch {
                                delay(pollWaitTime)
                                signal.send(true)
                            }
                            .start()
                        signal.receive()
                        spansNeeded.lazySet(Int.MAX_VALUE)
                    }
                }
            }
        }

        private fun addSpanDataToBatch(spanData: SpanData) {
            batchInternal.update { it.add(spanData) }
        }
        private fun removeSpanDataFromBatch(spanData: List<SpanData>) {
            batchInternal.update { it.removeAll(spanData) }
        }

        private suspend fun flush() {
            var spansToFlush: Int = queueCounter.value
            while (spansToFlush > 0) {
                val span: ReadableSpan = queue.receive()
                val spanData = span.toSpanData()
                addSpanDataToBatch(spanData)
                spansToFlush--
                if (batchInternal.value.size >= maxExportBatchSize) {
                    exportCurrentBatch()
                }
            }
            exportCurrentBatch()
            val flushResult: CompletableResultCode? = flushRequested.value
            if (flushResult != null) {
                flushResult.succeed()
                flushRequested.value = null
            }
        }

        private fun updateNextExportTime() {
            nextExportTime.value = Clock.default.nanoTime() + scheduleDelayNanos
        }

        fun shutdown(): CompletableResultCode {
            val result = CompletableResultCode()
            val flushResult: CompletableResultCode = forceFlush()
            flushResult.whenComplete {
                continueWork.value = false
                val shutdownResult: CompletableResultCode = spanExporter.shutdown()
                shutdownResult.whenComplete {
                    if (!flushResult.isSuccess || !shutdownResult.isSuccess) {
                        result.fail()
                    } else {
                        result.succeed()
                    }
                }
            }
            return result
        }

        fun forceFlush(): CompletableResultCode {
            val flushResult = CompletableResultCode()
            // we set the atomic here to trigger the worker loop to do a flush of the entire queue.
            if (flushRequested.compareAndSet(null, flushResult)) {
                signal.trySend(true)
            }
            // there's a race here where the flush happening in the worker loop could complete
            // before we
            // get what's in the atomic. In that case, just return success, since we know it
            // succeeded in
            // the interim.
            return flushRequested.value ?: CompletableResultCode.ofSuccess()
        }

        private suspend fun exportCurrentBatch() {
            val currentBatch = batchInternal.value
            if (currentBatch.isEmpty()) {
                return
            }
            try {
                val result: CompletableResultCode = spanExporter.export(currentBatch)
                result.join(exporterTimeoutNanos, DateTimeUnit.NANOSECOND)
                if (result.isSuccess) {
                    exportedSpans.add(currentBatch.size.toLong())
                } else {
                    // logger.log(java.util.logging.Level.FINE, "Exporter failed")
                }
            } catch (e: RuntimeException) {
                // logger.log(java.util.logging.Level.WARNING, "Exporter threw an Exception", e)
            } finally {
                removeSpanDataFromBatch(currentBatch)
            }
        }

        companion object {
            // private val logger: java.util.logging.Logger =
            //   java.util.logging.Logger.getLogger(Worker::class.java.getName())
        }
    }

    companion object {
        private val WORKER_THREAD_NAME: String =
            BatchSpanProcessor::class.simpleName!! + "_WorkerThread"
        private val SPAN_PROCESSOR_TYPE_LABEL = AttributeKey.stringKey("spanProcessorType")
        private val SPAN_PROCESSOR_DROPPED_LABEL = AttributeKey.booleanKey("dropped")
        private val SPAN_PROCESSOR_TYPE_VALUE: String = BatchSpanProcessor::class.simpleName!!

        /**
         * Returns a new Builder for [BatchSpanProcessor].
         *
         * @param spanExporter the `SpanExporter` to where the Spans are pushed.
         * @return a new [BatchSpanProcessor].
         * @throws NullPointerException if the `spanExporter` is `null`.
         */
        fun builder(spanExporter: SpanExporter): BatchSpanProcessorBuilder {
            return BatchSpanProcessorBuilder(spanExporter)
        }
    }
}
