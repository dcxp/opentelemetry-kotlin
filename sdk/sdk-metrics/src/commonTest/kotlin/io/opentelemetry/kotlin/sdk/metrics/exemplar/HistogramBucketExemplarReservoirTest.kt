/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.exemplar

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.testing.time.TestClock
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

internal class HistogramBucketExemplarReservoirTest {
    @Test
    fun noMeasurement_returnsEmpty() {
        val clock: TestClock = TestClock.create()
        val reservoir: ExemplarReservoir = HistogramBucketExemplarReservoir(clock, doubleArrayOf())
        reservoir.collectAndReset(Attributes.empty()).shouldBeEmpty()
    }

    @Test
    fun oneBucket_samplesEverything() {
        val clock: TestClock = TestClock.create()
        val reservoir: ExemplarReservoir = HistogramBucketExemplarReservoir(clock, doubleArrayOf())
        reservoir.offerMeasurement(1L, Attributes.empty(), Context.root())
        assertSoftly(reservoir.collectAndReset(Attributes.empty())) {
            shouldHaveSize(1)
            single().epochNanos shouldBe clock.now()
            single().filteredAttributes shouldBe Attributes.empty()
            single().valueAsDouble shouldBe 1.0
        }
        // Measurement count is reset, we should sample a new measurement (and only one)
        clock.advance(1.seconds)
        reservoir.offerMeasurement(2L, Attributes.empty(), Context.root())
        assertSoftly(reservoir.collectAndReset(Attributes.empty())) {
            shouldHaveSize(1)
            single().epochNanos shouldBe clock.now()
            single().filteredAttributes shouldBe Attributes.empty()
            single().valueAsDouble shouldBe 2.0
        }
        // only latest measurement is kept per-bucket
        clock.advance(1.seconds)
        reservoir.offerMeasurement(3L, Attributes.empty(), Context.root())
        reservoir.offerMeasurement(4L, Attributes.empty(), Context.root())
        assertSoftly(reservoir.collectAndReset(Attributes.empty())) {
            shouldHaveSize(1)
            single().epochNanos shouldBe clock.now()
            single().filteredAttributes shouldBe Attributes.empty()
            single().valueAsDouble shouldBe 4.0
        }
    }

    @Test
    fun multipleBuckets_samplesIntoCorrectBucket() {
        val clock: TestClock = TestClock.create()
        val bucketKey = AttributeKey.longKey("bucket")
        val reservoir: ExemplarReservoir =
            HistogramBucketExemplarReservoir(clock, doubleArrayOf(0.0, 10.0, 20.0))
        reservoir.offerMeasurement(-1, Attributes.of(bucketKey, 0L), Context.root())
        reservoir.offerMeasurement(1, Attributes.of(bucketKey, 1L), Context.root())
        reservoir.offerMeasurement(11, Attributes.of(bucketKey, 2L), Context.root())
        reservoir.offerMeasurement(21, Attributes.of(bucketKey, 3L), Context.root())
        assertSoftly(reservoir.collectAndReset(Attributes.empty())) {
            shouldHaveSize(4)
            get(0).filteredAttributes shouldBe Attributes.of(bucketKey, 0L)
            get(0).valueAsDouble shouldBe -1.0
            get(1).filteredAttributes shouldBe Attributes.of(bucketKey, 1L)
            get(1).valueAsDouble shouldBe 1.0
            get(2).filteredAttributes shouldBe Attributes.of(bucketKey, 2L)
            get(2).valueAsDouble shouldBe 11.0
            get(3).filteredAttributes shouldBe Attributes.of(bucketKey, 3L)
            get(3).valueAsDouble shouldBe 21.0
        }
    }
}
