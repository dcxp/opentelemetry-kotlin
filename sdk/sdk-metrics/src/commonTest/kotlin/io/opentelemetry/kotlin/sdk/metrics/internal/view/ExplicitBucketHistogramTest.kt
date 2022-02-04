/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.view

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.sdk.metrics.view.Aggregation
import kotlin.test.Test

/** Tests configuration errors in explicit bucket histograms. */
internal class ExplicitBucketHistogramTest {
    @Test
    fun goodConfig() {
        Aggregation.explicitBucketHistogram().shouldNotBeNull()
    }

    @Test
    fun badBuckets_throwArgumentException() {
        shouldThrow<IllegalArgumentException> {
                Aggregation.explicitBucketHistogram(listOf(Double.NEGATIVE_INFINITY))
            }
            .message shouldBe "invalid bucket boundary: -Inf"
        shouldThrow<IllegalArgumentException> {
                Aggregation.explicitBucketHistogram(listOf(1.0, Double.POSITIVE_INFINITY))
            }
            .message shouldBe "invalid bucket boundary: +Inf"
        shouldThrow<IllegalArgumentException> {
                Aggregation.explicitBucketHistogram(listOf(1.0, Double.NaN))
            }
            .message shouldBe "invalid bucket boundary: NaN"
        /// TODO fix test for JS Target
        /*shouldThrow<IllegalArgumentException> {
            Aggregation.explicitBucketHistogram(listOf(2.0, 1.0, 3.0))
        }
        .message shouldBe "Bucket boundaries must be in increasing order: 2.0 >= 1.0"*/
    }
}
