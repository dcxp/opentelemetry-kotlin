/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.testing

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.common.normalizeToNanos
import io.opentelemetry.kotlin.sdk.common.Clock
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.LongPointData
import io.opentelemetry.kotlin.sdk.metrics.data.LongSumData
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.resources.Resource
import kotlinx.datetime.DateTimeUnit
import kotlin.test.Test

/** Unit tests for [InMemoryMetricExporter]. */
internal class InMemoryMetricExporterTest {
    private val exporter = InMemoryMetricExporter.create()
    @Test
    fun test_getFinishedMetricItems() {
        val metrics: List<MetricData> =
            listOf(generateFakeMetric(), generateFakeMetric(), generateFakeMetric())
        exporter.export(metrics).isSuccess.shouldBeTrue()
        val metricItems = exporter.getFinishedMetricItems()
        metricItems.shouldNotBeNull()
        metricItems.size shouldBe 3
    }

    @Test
    fun test_reset() {
        val metrics: List<MetricData> =
            listOf(generateFakeMetric(), generateFakeMetric(), generateFakeMetric())
        exporter.export(metrics).isSuccess.shouldBeTrue()
        var metricItems: List<MetricData?> = exporter.getFinishedMetricItems()
        metricItems.shouldNotBeNull()
        metricItems.size shouldBe 3
        exporter.reset()
        metricItems = exporter.getFinishedMetricItems()
        metricItems.shouldNotBeNull()
        metricItems.size shouldBe 0
    }

    @Test
    fun test_shutdown() {
        val metrics: List<MetricData> =
            listOf(generateFakeMetric(), generateFakeMetric(), generateFakeMetric())
        exporter.export(metrics).isSuccess.shouldBeTrue()
        exporter.shutdown()
        val metricItems = exporter.getFinishedMetricItems()
        metricItems.shouldNotBeNull()
        metricItems.size shouldBe 0
    }

    @Test
    fun testShutdown_export() {
        val metrics: List<MetricData> =
            listOf(generateFakeMetric(), generateFakeMetric(), generateFakeMetric())
        exporter.export(metrics).isSuccess.shouldBeTrue()
        exporter.shutdown()
        exporter.export(metrics).isSuccess.shouldBeFalse()
    }

    @Test
    fun test_flush() {
        exporter.flush().isSuccess.shouldBeTrue()
    }

    companion object {
        private fun generateFakeMetric(): MetricData {
            val startNs: Long = Clock.default.nanoTime()
            val endNs: Long = startNs + DateTimeUnit.MILLISECOND.normalizeToNanos(900)
            return MetricData.createLongSum(
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                "name",
                "description",
                "1",
                LongSumData.create(
                    /* isMonotonic= */ true,
                    AggregationTemporality.CUMULATIVE,
                    listOf(
                        LongPointData.create(
                            startNs,
                            endNs,
                            Attributes.builder().put("k", "v").build(),
                            5
                        )
                    )
                )
            )
        }
    }
}
