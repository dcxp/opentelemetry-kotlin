/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

/**
 * These are extra test cases for buckets. Much of this class is already tested via more complex
 * test cases at [DoubleExponentialHistogramAggregatorTest].
 */
class DoubleExponentialHistogramBucketsTest {

    // TODO fix scaleb
    /*
        @Test
        fun testRecordSimple() {
            // Can only effectively test recording of one value here due to downscaling required.
            // More complex recording/downscaling operations are tested in the aggregator.
            val b = DoubleExponentialHistogramBuckets()
            b.record(1.0)
            b.record(1.0)
            b.record(1.0)
            b.totalCount shouldBe 3
            b.bucketCounts shouldContainInOrder listOf(3L)
        }
    */
    @Test
    fun testRecordShouldError() {
        val b = DoubleExponentialHistogramBuckets()
        shouldThrow<IllegalStateException> { b.record(0.0) }
    }

    // @Test
    // TODO fix test
    fun testDownscale() {
        val b = DoubleExponentialHistogramBuckets()
        b.downscale(20) // scale of zero is easy to reason with without a calculator
        b.record(1.0)
        b.record(2.0)
        b.record(4.0)
        b.scale shouldBe 0
        b.totalCount shouldBe 3
        b.bucketCounts shouldContainInOrder listOf(1L, 1L, 1L)
        b.offset shouldBe 0
    }

    @Test
    fun testDownscaleShouldError() {
        val b = DoubleExponentialHistogramBuckets()
        shouldThrow<IllegalStateException> { b.downscale(-1) }
    }

    @Test
    fun testEqualsAndHashCode() {
        val a = DoubleExponentialHistogramBuckets()
        val b = DoubleExponentialHistogramBuckets()
        a.shouldNotBeNull()
        a shouldBe b
        b shouldBe a
        a.hashCode() shouldBe b.hashCode()

        a.record(1.0)
        a shouldNotBe b
        b shouldNotBe a
        a.hashCode() shouldNotBe b.hashCode()

        b.record(1.0)
        a shouldBe b
        b shouldBe a
        a.hashCode() shouldBe b.hashCode()
    }

    @Test
    fun testToString() {
        // Note this test may break once difference implementations for counts are developed since
        // the counts may have different toStrings().
        val b = DoubleExponentialHistogramBuckets()
        b.record(1.0)
        b.toString() shouldBe
            "DoubleExponentialHistogramBuckets{scale: 20, offset: 0, counts: {0=1} }"
    }
}
