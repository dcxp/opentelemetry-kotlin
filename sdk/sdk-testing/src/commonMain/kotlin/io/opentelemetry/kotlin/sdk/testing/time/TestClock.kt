package io.opentelemetry.kotlin.sdk.testing.time

import io.opentelemetry.kotlin.api.common.getNanoseconds
import io.opentelemetry.kotlin.sdk.common.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class TestClock private constructor(private var current: Instant) : Clock {
    override fun now(): Long {
        return current.getNanoseconds()
    }

    override fun nanoTime(): Long {
        return current.getNanoseconds()
    }

    @OptIn(ExperimentalTime::class)
    fun advance(duration: Duration) {
        current = current.plus(duration)
    }

    companion object {
        fun create(now: Instant): TestClock {
            return TestClock(now)
        }

        fun create(): TestClock {
            return TestClock(kotlinx.datetime.Clock.System.now())
        }
    }
}
