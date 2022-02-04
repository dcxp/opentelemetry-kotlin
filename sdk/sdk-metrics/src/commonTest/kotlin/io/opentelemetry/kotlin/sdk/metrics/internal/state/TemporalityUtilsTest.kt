/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.internal.state.TemporalityUtils.resolveTemporality
import kotlin.test.Test

internal class TemporalityUtilsTest {
    @Test
    fun testUsePreferred() {
        resolveTemporality(
            AggregationTemporality.values().toSet(),
            AggregationTemporality.CUMULATIVE
        ) shouldBe AggregationTemporality.CUMULATIVE
        resolveTemporality(
            AggregationTemporality.values().toSet(),
            AggregationTemporality.DELTA
        ) shouldBe AggregationTemporality.DELTA
    }

    @Test
    fun testDefaultToCumulativeIfAble() {
        resolveTemporality(AggregationTemporality.values().toSet(), null) shouldBe
            AggregationTemporality.CUMULATIVE
        resolveTemporality(setOf(AggregationTemporality.CUMULATIVE), null) shouldBe
            AggregationTemporality.CUMULATIVE
        resolveTemporality(setOf(AggregationTemporality.DELTA), null) shouldBe
            AggregationTemporality.DELTA
    }

    @Test
    fun testHandleErrorScenarios() {
        // Default to cumulative if preferred/supported is empty.
        resolveTemporality(setOf(), null) shouldBe AggregationTemporality.CUMULATIVE
    }
}
