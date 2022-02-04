/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import kotlinx.atomicfu.atomic
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal data class StressTestRunner(
    val operations: List<Operation>,
    val instrument: AbstractInstrument,
    val collectionIntervalMs: Int
) {
    private val countDownLatch = atomic(0L)

    suspend fun run() {
        val operations: List<Operation> = operations
        val numThreads = operations.size
        countDownLatch.lazySet(numThreads.toLong())
        val collectionThread =
            CoroutineScope(Dispatchers.Unconfined).launch {
                while (countDownLatch.value != 0L) {
                    delay(collectionIntervalMs.toLong())
                }
            }
        val operationThreads =
            operations.map { operation ->
                CoroutineScope(Dispatchers.Unconfined).launch {
                    for (i in 0 until operation.numOperations) {
                        operation.updater.update()
                        delay(collectionIntervalMs.toLong())
                    }
                    countDownLatch.decrementAndGet()
                }
            }

        // Start collection thread then the rest of the worker threads.
        collectionThread.start()
        for (thread in operationThreads) {
            thread.start()
        }

        // Wait for all the thread to finish.
        for (thread in operationThreads) {
            thread.join()
        }
        collectionThread.join()
    }

    data class Builder(
        private val operations: PersistentList<Operation> = persistentListOf(),
        private val instrument: AbstractInstrument? = null,
        private val collectionIntervalMs: Int = 0,
    ) {

        // TODO: Change this to MeterSdk when collect is available for the entire Meter.
        fun setInstrument(meterSdk: AbstractInstrument): Builder {
            return copy(instrument = meterSdk)
        }

        fun setCollectionIntervalMs(collectionInterval: Int): Builder {
            return copy(collectionIntervalMs = collectionInterval)
        }
        fun addOperation(operation: Operation): Builder {
            return copy(operations = operations.add(operation))
        }

        fun build(): StressTestRunner {
            return StressTestRunner(operations, instrument!!, collectionIntervalMs)
        }
    }

    interface Operation {
        val numOperations: Int
        val operationDelayMs: Int
        val updater: OperationUpdater

        companion object {
            fun create(
                numOperations: Int,
                operationDelayMs: Int,
                updater: OperationUpdater
            ): Operation {
                return Implementation(numOperations, operationDelayMs, updater)
            }
            class Implementation(
                override val numOperations: Int,
                override val operationDelayMs: Int,
                override val updater: OperationUpdater
            ) : Operation
        }
    }

    abstract class OperationUpdater {
        /** Called every operation. */
        abstract fun update()

        /** Called after all operations are completed. */
        abstract fun cleanup()
    }

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}
