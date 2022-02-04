/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.common.normalizeToNanos
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.resources.Resource
import kotlinx.datetime.DateTimeUnit
import kotlin.test.Test

/** Unit tests for [io.opentelemetry.kotlin.sdk.metrics.data.MetricData]. */
internal class MetricDataImplTest {
    @Test
    fun metricData_Getters() {
        val metricData =
            MetricData.createDoubleGauge(
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                "metric_name",
                "metric_description",
                "ms",
                DoubleGaugeData.create(emptyList())
            )
        metricData.name shouldBe "metric_name"
        metricData.description shouldBe "metric_description"
        metricData.unit shouldBe "ms"
        metricData.type shouldBe MetricDataType.DOUBLE_GAUGE
        metricData.resource shouldBe Resource.empty()
        metricData.instrumentationLibraryInfo shouldBe InstrumentationLibraryInfo.empty()
        metricData.isEmpty.shouldBeTrue()
    }

    @Test
    fun metricData_LongPoints() {
        LONG_POINT.startEpochNanos shouldBe START_EPOCH_NANOS
        LONG_POINT.epochNanos shouldBe EPOCH_NANOS
        LONG_POINT.attributes.size shouldBe 1
        LONG_POINT.attributes[KEY] shouldBe "value"
        LONG_POINT.value shouldBe LONG_VALUE
        var metricData =
            MetricData.createLongGauge(
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                "metric_name",
                "metric_description",
                "ms",
                LongGaugeData.create(listOf(LONG_POINT))
            )
        metricData.isEmpty.shouldBeFalse()
        metricData.longGaugeData.points.shouldContainExactly(LONG_POINT)
        metricData =
            MetricData.createLongSum(
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                "metric_name",
                "metric_description",
                "ms",
                LongSumData.create(
                    /* isMonotonic= */ false,
                    AggregationTemporality.CUMULATIVE,
                    listOf(LONG_POINT)
                )
            )
        metricData.isEmpty.shouldBeFalse()
        metricData.longSumData.points.shouldContainExactly(LONG_POINT)
    }

    @Test
    fun metricData_DoublePoints() {
        DOUBLE_POINT.startEpochNanos shouldBe START_EPOCH_NANOS
        DOUBLE_POINT.epochNanos shouldBe EPOCH_NANOS
        DOUBLE_POINT.attributes.size shouldBe 1
        DOUBLE_POINT.attributes[KEY] shouldBe "value"
        DOUBLE_POINT.value shouldBe DOUBLE_VALUE
        var metricData =
            MetricData.createDoubleGauge(
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                "metric_name",
                "metric_description",
                "ms",
                DoubleGaugeData.create(listOf(DOUBLE_POINT))
            )
        metricData.isEmpty.shouldBeFalse()
        metricData.doubleGaugeData.points.shouldContainExactly(DOUBLE_POINT)
        metricData =
            MetricData.createDoubleSum(
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                "metric_name",
                "metric_description",
                "ms",
                DoubleSumData.create(
                    /* isMonotonic= */ false,
                    AggregationTemporality.CUMULATIVE,
                    listOf(DOUBLE_POINT)
                )
            )
        metricData.isEmpty.shouldBeFalse()
        metricData.doubleSumData.points.shouldContainExactly(DOUBLE_POINT)
    }

    @Test
    fun metricData_SummaryPoints() {
        SUMMARY_POINT.startEpochNanos shouldBe START_EPOCH_NANOS
        SUMMARY_POINT.epochNanos shouldBe EPOCH_NANOS
        SUMMARY_POINT.attributes.size shouldBe 1
        SUMMARY_POINT.attributes[KEY] shouldBe "value"
        SUMMARY_POINT.count shouldBe LONG_VALUE
        SUMMARY_POINT.sum shouldBe DOUBLE_VALUE
        SUMMARY_POINT.percentileValues shouldBe listOf(MINIMUM_VALUE, MAXIMUM_VALUE)
        val metricData =
            MetricData.createDoubleSummary(
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                "metric_name",
                "metric_description",
                "ms",
                DoubleSummaryData.create(listOf(SUMMARY_POINT))
            )
        metricData.doubleSummaryData.points.shouldContainExactly(SUMMARY_POINT)
    }

    @Test
    fun metricData_HistogramPoints() {
        HISTOGRAM_POINT.startEpochNanos shouldBe START_EPOCH_NANOS
        HISTOGRAM_POINT.epochNanos shouldBe EPOCH_NANOS
        HISTOGRAM_POINT.attributes.size shouldBe 1
        HISTOGRAM_POINT.attributes[KEY] shouldBe "value"
        HISTOGRAM_POINT.count shouldBe 2L
        HISTOGRAM_POINT.sum shouldBe DOUBLE_VALUE
        HISTOGRAM_POINT.boundaries shouldBe listOf(1.0)
        HISTOGRAM_POINT.counts shouldBe listOf(1L, 1L)
        val metricData =
            MetricData.createDoubleHistogram(
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                "metric_name",
                "metric_description",
                "ms",
                DoubleHistogramData.create(AggregationTemporality.DELTA, setOf(HISTOGRAM_POINT))
            )
        metricData.doubleHistogramData.points.shouldContainExactly(HISTOGRAM_POINT)
        shouldThrow<IllegalArgumentException> {
            DoubleHistogramPointData.create(0, 0, Attributes.empty(), 0.0, listOf(), listOf())
        }
        shouldThrow<IllegalArgumentException> {
            DoubleHistogramPointData.create(
                0,
                0,
                Attributes.empty(),
                0.0,
                listOf(1.0, 1.0),
                listOf(0L, 0L, 0L)
            )
        }
        shouldThrow<IllegalArgumentException> {
            DoubleHistogramPointData.create(
                0,
                0,
                Attributes.empty(),
                0.0,
                listOf(Double.NEGATIVE_INFINITY),
                listOf(0L, 0L)
            )
        }
    }

    @Test
    fun metricData_GetDefault() {
        var metricData =
            MetricData.createDoubleSummary(
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                "metric_name",
                "metric_description",
                "ms",
                DoubleSummaryData.create(listOf(SUMMARY_POINT))
            )
        metricData.doubleGaugeData.points.shouldBeEmpty()
        metricData.longGaugeData.points.shouldBeEmpty()
        metricData.doubleSumData.points.shouldBeEmpty()
        metricData.longGaugeData.points.shouldBeEmpty()
        metricData.doubleHistogramData.points.shouldBeEmpty()
        metricData.doubleSummaryData.points.shouldContainExactly(SUMMARY_POINT)
        metricData =
            MetricData.createDoubleGauge(
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                "metric_name",
                "metric_description",
                "ms",
                DoubleGaugeData.create(listOf(DOUBLE_POINT))
            )
        metricData.doubleGaugeData.points.shouldContainExactly(DOUBLE_POINT)
        metricData.longGaugeData.points.shouldBeEmpty()
        metricData.doubleSumData.points.shouldBeEmpty()
        metricData.longGaugeData.points.shouldBeEmpty()
        metricData.doubleHistogramData.points.shouldBeEmpty()
        metricData.doubleSummaryData.points.shouldBeEmpty()
    }

    companion object {
        private val START_EPOCH_NANOS: Long = DateTimeUnit.MILLISECOND.normalizeToNanos(1000)
        private val EPOCH_NANOS: Long = DateTimeUnit.MILLISECOND.normalizeToNanos(2000)
        private const val LONG_VALUE: Long = 10
        private const val DOUBLE_VALUE = 1.234
        private val KEY = AttributeKey.stringKey("key")
        private val MINIMUM_VALUE = ValueAtPercentile.create(0.0, DOUBLE_VALUE)
        private val MAXIMUM_VALUE = ValueAtPercentile.create(100.0, DOUBLE_VALUE)
        private val LONG_POINT =
            LongPointData.create(
                START_EPOCH_NANOS,
                EPOCH_NANOS,
                Attributes.of(KEY, "value"),
                LONG_VALUE
            )
        private val DOUBLE_POINT =
            DoublePointData.create(
                START_EPOCH_NANOS,
                EPOCH_NANOS,
                Attributes.of(KEY, "value"),
                DOUBLE_VALUE
            )
        private val SUMMARY_POINT =
            DoubleSummaryPointData.create(
                START_EPOCH_NANOS,
                EPOCH_NANOS,
                Attributes.of(KEY, "value"),
                LONG_VALUE,
                DOUBLE_VALUE,
                listOf(
                    ValueAtPercentile.create(0.0, DOUBLE_VALUE),
                    ValueAtPercentile.create(100.0, DOUBLE_VALUE)
                )
            )
        private val HISTOGRAM_POINT =
            DoubleHistogramPointData.create(
                START_EPOCH_NANOS,
                EPOCH_NANOS,
                Attributes.of(KEY, "value"),
                DOUBLE_VALUE,
                listOf(1.0),
                listOf(1L, 1L)
            )
    }
}
