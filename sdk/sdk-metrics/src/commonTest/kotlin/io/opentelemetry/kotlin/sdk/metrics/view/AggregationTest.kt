/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.view

import kotlin.test.Test

/** Simple tests of Aggregation classes API. */
internal class AggregationTest {
    @Test
    fun haveToString() {
        Aggregation.none().toString().contains("NoAggregation")
        Aggregation.defaultAggregation().toString().contains("Default")
        Aggregation.lastValue().toString().contains("LastValue")
        Aggregation.sum().toString().contains("Sum")
        Aggregation.explicitBucketHistogram().toString().contains("ExplicitBucketHistogram")
        Aggregation.explicitBucketHistogram(listOf(1.0))
            .toString()
            .contains("ExplicitBucketHistogram")
    }

    @Test
    fun histogramUsesExplicitBucket() {
        // Note: This will change when exponential histograms are launched.
        Aggregation.histogram().toString().contains("ExplicitBucketHistogram")
    }
}
