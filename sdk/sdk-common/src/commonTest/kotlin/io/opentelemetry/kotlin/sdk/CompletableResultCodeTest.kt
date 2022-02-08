@file:OptIn(ExperimentalCoroutinesApi::class)

package io.opentelemetry.kotlin.sdk

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

class CompletableResultCodeTest {
    @Test
    fun joinTimeoutTest() = runTest{
        val result = CompletableResultCode()
        shouldThrow<TimeoutCancellationException> {
            result.join(10.milliseconds)
        }
    }

    @Test
    fun joinOnSucceededResultTest() = runTest{
        val result = CompletableResultCode.ofSuccess()
        shouldNotThrowAny {
            result.join(10.milliseconds)
        }
    }

    @Test
    fun joinOnFailedResultTest() = runTest{
        val result = CompletableResultCode.ofFailure()
        shouldNotThrowAny {
            result.join(10.milliseconds)
        }
    }

    @Test
    fun joinOnDelayedResultTest() = runTest{
        val result = CompletableResultCode()
        launch {
            delay(10.milliseconds)
            result.succeed()
        }.start()
        shouldNotThrowAny {
            result.join(100.milliseconds)
        }
    }
}