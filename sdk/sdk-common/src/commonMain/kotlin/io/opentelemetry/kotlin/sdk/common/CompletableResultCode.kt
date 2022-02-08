/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.common

import io.opentelemetry.kotlin.api.common.normalizeToNanos
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.atomicfu.update
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withTimeout
import kotlinx.datetime.DateTimeUnit

/**
 * This class models JDK 8's CompletableFuture to afford migration should Open Telemetry's SDK
 * select JDK 8 or greater as a baseline, and also to offer familiarity to developers.
 *
 * The implementation of Export operations are often asynchronous in nature, hence the need to
 * convey a result at a later time. CompletableResultCode facilitates this.
 */
class CompletableResultCode private constructor(state: State) {
    private enum class State {
        NotReady,
        Success,
        Failed
    }
    private val internalState: AtomicRef<State>
    private val completionActions = atomic<PersistentList<suspend () -> Unit>>(persistentListOf())
    private val lock = reentrantLock()
    init {
        this.internalState = atomic(state)
    }

    constructor() : this(State.NotReady)

    /** Complete this [CompletableResultCode] successfully if it is not already completed. */
    fun succeed(): CompletableResultCode {
        if (isNotDone) {
            lock.withLock {
                if (internalState.compareAndSet(State.NotReady, State.Success)) {
                    CoroutineScope(Dispatchers.Unconfined)
                        .launch {
                            for (action in completionActions.value) {
                                action()
                            }
                        }
                        .start()
                }
            }
        }
        return this
    }

    /** Complete this [CompletableResultCode] unsuccessfully if it is not already completed. */
    fun fail(): CompletableResultCode {
        if (isNotDone) {
            lock.withLock {
                if (internalState.compareAndSet(State.NotReady, State.Failed)) {
                    CoroutineScope(Dispatchers.Unconfined)
                        .launch {
                            for (action in completionActions.value) {
                                action()
                            }
                        }
                        .start()
                }
            }
        }
        return this
    }

    /**
     * Obtain the current state of completion. Generally call once completion is achieved via the
     * thenRun method.
     *
     * @return the current state of completion
     */
    val isSuccess: Boolean
        get() {
            return internalState.value == State.Success
        }
    /** Returns whether this [CompletableResultCode] has completed. */
    val isDone: Boolean
        get() {
            return internalState.value != State.NotReady
        }

    val isNotDone: Boolean
        get() {
            return !isDone
        }

    /**
     * Perform an action on completion. Actions are guaranteed to be called only once.
     *
     * @param action the action to perform
     * @return this completable result so that it may be further composed
     */
    fun whenComplete(action: suspend () -> Unit): CompletableResultCode {
        lock.withLock {
            if (isDone) {
                CoroutineScope(Dispatchers.Unconfined).launch { action() }.start()
            } else {
                completionActions.update { it.add(action) }
            }
        }
        return this
    }

    /**
     * Waits up to the specified amount of time for this [CompletableResultCode] to complete. Even
     * after this method returns, the result may not be complete yet - you should always check
     * [.isSuccess] or [.isDone] after calling this method to determine the result.
     *
     * @return this [CompletableResultCode]
     */
    suspend fun join(timeout: Duration): CompletableResultCode {
        if (isDone) {
            return this
        }
        val semaphore = Semaphore(1, 1)
        this.whenComplete { semaphore.release() }
        withTimeout(timeout){
            semaphore.acquire()
        }
        return this
    }

    suspend fun join(timeout: Long, unit: DateTimeUnit): CompletableResultCode {
        return join(unit.normalizeToNanos(timeout).nanoseconds)
    }

    companion object {
        /** Returns a [CompletableResultCode] that has been completed successfully. */
        fun ofSuccess(): CompletableResultCode {
            return SUCCESS
        }

        /** Returns a [CompletableResultCode] that has been completed unsuccessfully. */
        fun ofFailure(): CompletableResultCode {
            return FAILURE
        }

        /**
         * Returns a [CompletableResultCode] that completes after all the provided [ ]s complete. If
         * any of the results fail, the result will be failed.
         */
        fun ofAll(codes: Collection<CompletableResultCode>): CompletableResultCode {
            if (codes.isEmpty()) {
                return ofSuccess()
            }
            val result = CompletableResultCode()

            class ShearedState {
                private val pending = atomic(codes.size)
                private val failed = atomic(false)
                fun decrementAndGetPending(): Int {
                    return pending.decrementAndGet()
                }

                fun setFailed() {
                    failed.lazySet(true)
                }
                fun isFailed(): Boolean {
                    return failed.value
                }
            }
            val shearedState = ShearedState()
            for (code in codes) {
                code.whenComplete {
                    if (!code.isSuccess) {
                        shearedState.setFailed()
                    }
                    if (shearedState.decrementAndGetPending() == 0) {
                        if (shearedState.isFailed()) {
                            result.fail()
                        } else {
                            result.succeed()
                        }
                    }
                }
            }
            return result
        }

        private val SUCCESS = CompletableResultCode(State.Success)
        private val FAILURE = CompletableResultCode(State.Failed)
    }
}
