/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore

internal data class StressTestRunner(
    val operations: ImmutableList<Operation>,
    val tracer: SdkTracer,
    val spanProcessor: SpanProcessor
) {

    suspend fun run() {
        val operations: List<Operation> = operations
        val operationThreads =
            operations.map { operation ->
                CoroutineScope(Dispatchers.Unconfined).launch {
                    for (i in 0 until operation.numOperations) {
                        operation.updater.update()
                        delay(operation.operationDelayMs.toLong())
                    }
                }
            }
        for (thread in operationThreads) {
            thread.start()
        }

        // Wait for all the threads to finish.
        for (thread in operationThreads) {
            thread.join()
        }
        val semaphore = Semaphore(1, 1)

        spanProcessor.shutdown().whenComplete { semaphore.release() }

        semaphore.acquire()
    }

    internal class Builder {
        private val tracer = atomic<SdkTracer?>(null)
        private val spanProcessor = atomic<SpanProcessor?>(null)
        private val operations = atomic<PersistentList<Operation>>(persistentListOf())

        fun setTracer(sdkTracer: SdkTracer): Builder {
            tracer.value = sdkTracer
            return this
        }
        fun setSpanProcessor(spanProcessor: SpanProcessor): Builder {
            this.spanProcessor.value = spanProcessor
            return this
        }
        fun addOperation(operation: Operation): Builder {
            operations.update { it.add(operation) }
            return this
        }
        fun addOperations(vararg operations: Operation): Builder {
            operations.forEach { addOperation(it) }
            return this
        }
        fun addOperations(operations: Collection<Operation>): Builder {
            operations.forEach { addOperation(it) }
            return this
        }
        fun build() = StressTestRunner(operations.value, tracer.value!!, spanProcessor.value!!)
    }

    internal data class Operation(
        val numOperations: Int,
        val operationDelayMs: Int,
        val updater: OperationUpdater
    ) {

        companion object {
            fun create(
                numOperations: Int,
                operationDelayMs: Int,
                updater: OperationUpdater
            ): Operation {
                return Operation(numOperations, operationDelayMs, updater)
            }
        }
    }

    internal interface OperationUpdater {
        /** Called every operation. */
        fun update()
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}
