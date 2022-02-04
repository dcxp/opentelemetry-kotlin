/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.exemplar

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.trace.Span
import io.opentelemetry.kotlin.api.trace.SpanContext
import io.opentelemetry.kotlin.api.trace.TraceFlags
import io.opentelemetry.kotlin.api.trace.TraceState
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.internal.RandomSupplier
import io.opentelemetry.kotlin.sdk.testing.time.TestClock
import kotlin.random.Random
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

internal class FixedSizeExemplarReservoirTest {
    @Test
    fun noMeasurement_returnsEmpty() {
        val clock: TestClock = TestClock.create()
        val reservoir: ExemplarReservoir =
            FixedSizeExemplarReservoir(clock, 1, RandomSupplier.platformDefault())
        reservoir.collectAndReset(Attributes.empty()).shouldBeEmpty()
    }

    @Test
    fun oneMeasurement_alwaysSamplesFirstMeasurement() {
        val clock: TestClock = TestClock.create()
        val reservoir: ExemplarReservoir =
            FixedSizeExemplarReservoir(clock, 1, RandomSupplier.platformDefault())
        reservoir.offerMeasurement(1L, Attributes.empty(), Context.root())
        val result = reservoir.collectAndReset(Attributes.empty())
        assertSoftly(result) {
            shouldHaveSize(1)
            single().epochNanos shouldBe clock.now()
            single().filteredAttributes shouldBe Attributes.empty()
            single().valueAsDouble shouldBe 1.0
        }
        // Measurement count is reset, we should sample a new measurement (and only one)
        clock.advance(1.seconds)
        reservoir.offerMeasurement(2L, Attributes.empty(), Context.root())
        val result2 = reservoir.collectAndReset(Attributes.empty())
        assertSoftly(result2) {
            shouldHaveSize(1)
            single().epochNanos shouldBe clock.now()
            single().filteredAttributes shouldBe Attributes.empty()
            single().valueAsDouble shouldBe 2.0
        }
    }

    @Test
    fun oneMeasurement_filtersAttributes() {
        val all = Attributes.builder().put("one", 1).put("two", "two").put("three", true).build()
        val partial = Attributes.builder().put("three", true).build()
        val remaining = Attributes.builder().put("one", 1).put("two", "two").build()
        val clock: TestClock = TestClock.create()
        val reservoir: ExemplarReservoir =
            FixedSizeExemplarReservoir(clock, 1, RandomSupplier.platformDefault())
        reservoir.offerMeasurement(1L, all, Context.root())
        val result = reservoir.collectAndReset(partial)
        assertSoftly(result) {
            shouldHaveSize(1)
            single().epochNanos shouldBe clock.now()
            single().filteredAttributes shouldBe remaining
            single().valueAsDouble shouldBe 1.0
        }
    }

    @Test
    fun oneMeasurement_includesTraceAndSpanIds() {
        val all = Attributes.builder().put("one", 1).put("two", "two").put("three", true).build()
        val context =
            Context.root()
                .with(
                    Span.wrap(
                        SpanContext.createFromRemoteParent(
                            TRACE_ID,
                            SPAN_ID,
                            TraceFlags.sampled,
                            TraceState.default
                        )
                    )
                )
        val clock: TestClock = TestClock.create()
        val reservoir: ExemplarReservoir =
            FixedSizeExemplarReservoir(clock, 1, RandomSupplier.platformDefault())
        reservoir.offerMeasurement(1L, all, context)
        val result = reservoir.collectAndReset(Attributes.empty())
        assertSoftly(result) {
            shouldHaveSize(1)
            single().epochNanos shouldBe clock.now()
            single().filteredAttributes shouldBe all
            single().valueAsDouble shouldBe 1.0
            single().traceId shouldBe TRACE_ID
            single().spanId shouldBe SPAN_ID
        }
    }

    @Test
    fun multiMeasurements_preservesLatestSamples() {
        val key = AttributeKey.longKey("K")
        // We cannot mock random in latest jdk, so we create an override.
        val mockRandom =
            object : Random() {
                override fun nextBits(bitCount: Int): Int {
                    TODO("Not yet implemented")
                }
                override fun nextInt(max: Int): Int {
                    return when (max) {
                        2 -> 1
                        else -> 0
                    }
                }
            }
        val clock: TestClock = TestClock.create()
        val reservoir: ExemplarReservoir = FixedSizeExemplarReservoir(clock, 2) { mockRandom }
        reservoir.offerMeasurement(1, Attributes.of(key, 1L), Context.root())
        reservoir.offerMeasurement(2, Attributes.of(key, 2L), Context.root())
        reservoir.offerMeasurement(3, Attributes.of(key, 3L), Context.root())
        val result = reservoir.collectAndReset(Attributes.empty())
        assertSoftly(result) {
            shouldHaveSize(2)
            first().epochNanos shouldBe clock.now()
            last().epochNanos shouldBe clock.now()
            assertSoftly(map { it.valueAsDouble }.toSet()) {
                shouldContain(2.0)
                shouldContain(3.0)
            }
        }
    }

    companion object {
        private const val TRACE_ID = "ff000000000000000000000000000041"
        private const val SPAN_ID = "ff00000000000041"
    }
}
