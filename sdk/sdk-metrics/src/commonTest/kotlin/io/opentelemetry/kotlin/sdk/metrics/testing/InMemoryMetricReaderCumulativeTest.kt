/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.testing

import io.kotest.matchers.collections.shouldHaveSize
import io.opentelemetry.kotlin.sdk.metrics.SdkMeterProvider
import kotlinx.datetime.DateTimeUnit
import kotlin.test.Test

internal class InMemoryMetricReaderCumulativeTest {
    private val reader: InMemoryMetricReader = InMemoryMetricReader.create()
    private val provider: SdkMeterProvider =
        SdkMeterProvider.builder()
            .setMinimumCollectionInterval(DateTimeUnit.SECOND.duration.times(0))
            .registerMetricReader(reader)
            .build()

    private fun generateFakeMetric(index: Int) {
        provider["test"].counterBuilder("test$index").build().add(1)
    }

    @Test
    fun test_collectAllMetrics() {
        generateFakeMetric(1)
        generateFakeMetric(2)
        generateFakeMetric(3)
        reader.collectAllMetrics().shouldHaveSize(3)
    }

    @Test
    fun test_reset_preserves_cumulatives() {
        generateFakeMetric(1)
        generateFakeMetric(2)
        generateFakeMetric(3)
        reader.collectAllMetrics().shouldHaveSize(3)

        // Add more data, should join.
        generateFakeMetric(1)
        reader.collectAllMetrics().shouldHaveSize(1)
    }

    @Test
    fun test_flush() {
        generateFakeMetric(1)
        generateFakeMetric(2)
        generateFakeMetric(3)
        // TODO: Better assertions for CompletableResultCode.
        reader.flush()
        reader.collectAllMetrics().shouldHaveSize(0)
    }

    @Test
    fun test_shutdown() {
        generateFakeMetric(1)
        reader.collectAllMetrics().shouldHaveSize(1)
        reader.shutdown()
        // Post shutdown, collectAllMetrics should not be called.
    }
}
