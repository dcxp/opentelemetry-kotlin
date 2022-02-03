/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.metrics.data.DoubleSummaryPointData
import io.opentelemetry.kotlin.sdk.metrics.data.ValueAtPercentile
import kotlin.test.Test

internal class MinMaxSumCountAccumulationTest {
    @Test
    fun toPoint() {
        val accumulation = MinMaxSumCountAccumulation.create(12, 25.0, 1.0, 3.0)
        val point = getPoint(accumulation)
        point.count shouldBe 12
        point.sum shouldBe 25
        point.percentileValues.shouldHaveSize(2)
        point.percentileValues[0] shouldBe ValueAtPercentile.create(0.0, 1.0)
        point.percentileValues[1] shouldBe ValueAtPercentile.create(100.0, 3.0)
    }

    companion object {
        private fun getPoint(accumulation: MinMaxSumCountAccumulation): DoubleSummaryPointData {
            val point =
                accumulation.toPoint(12345, 12358, Attributes.builder().put("key", "value").build())
            point.shouldNotBeNull()
            point.startEpochNanos shouldBe 12345
            point.epochNanos shouldBe 12358
            assertSoftly(point.attributes.asMap()) {
                shouldHaveSize(1)
                shouldContain(AttributeKey.stringKey("key"), "value")
            }
            return point
        }
    }
}
