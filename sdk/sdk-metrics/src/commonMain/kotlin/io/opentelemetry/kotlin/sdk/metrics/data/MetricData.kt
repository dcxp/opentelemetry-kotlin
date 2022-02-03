/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.resources.Resource

/** A [MetricDataImpl] represents the data exported as part of aggregating one `Instrument`. */
interface MetricData {
    /**
     * Returns the resource of this `MetricData`.
     *
     * @return the resource of this `MetricData`.
     */
    val resource: Resource

    /**
     * Returns the instrumentation library specified when creating the `Meter` which created the
     * `Instrument` that produces `MetricData`.
     *
     * @return an instance of [InstrumentationLibraryInfo]
     */
    val instrumentationLibraryInfo: InstrumentationLibraryInfo

    /**
     * Returns the metric name.
     *
     * @return the metric name.
     */
    val name: String

    /**
     * Returns the description of this metric.
     *
     * @return the description of this metric.
     */
    val description: String

    /**
     * Returns the unit of this metric.
     *
     * @return the unit of this metric.
     */
    val unit: String

    /**
     * Returns the type of this metric.
     *
     * @return the type of this metric.
     */
    val type: MetricDataType
    val data: Data<*>

    /**
     * Returns `true` if there are no points associated with this metric.
     *
     * @return `true` if there are no points associated with this metric.
     */
    val isEmpty: Boolean
        get() = data.points.isEmpty()

    /**
     * Returns the `DoubleGaugeData` if type is [MetricDataType.DOUBLE_GAUGE], otherwise a default
     * empty data.
     *
     * @return the `DoubleGaugeData` if type is [MetricDataType.DOUBLE_GAUGE], otherwise a default
     * empty data.
     */
    val doubleGaugeData: DoubleGaugeData
        get() =
            if (type == MetricDataType.DOUBLE_GAUGE) {
                data as DoubleGaugeData
            } else DoubleGaugeData.EMPTY

    /**
     * Returns the `LongGaugeData` if type is [MetricDataType.LONG_GAUGE], otherwise a default empty
     * data.
     *
     * @return the `LongGaugeData` if type is [MetricDataType.LONG_GAUGE], otherwise a default empty
     * data.
     */
    val longGaugeData: LongGaugeData
        get() =
            if (type == MetricDataType.LONG_GAUGE) {
                data as LongGaugeData
            } else LongGaugeData.EMPTY

    /**
     * Returns the `DoubleSumData` if type is [MetricDataType.DOUBLE_SUM], otherwise a default empty
     * data.
     *
     * @return the `DoubleSumData` if type is [MetricDataType.DOUBLE_SUM], otherwise a default empty
     * data.
     */
    val doubleSumData: DoubleSumData
        get() =
            if (type == MetricDataType.DOUBLE_SUM) {
                data as DoubleSumData
            } else DoubleSumData.EMPTY

    /**
     * Returns the `LongSumData` if type is [MetricDataType.LONG_SUM], otherwise a default empty
     * data.
     *
     * @return the `LongSumData` if type is [MetricDataType.LONG_SUM], otherwise a default empty
     * data.
     */
    val longSumData: LongSumData
        get() =
            if (type == MetricDataType.LONG_SUM) {
                data as LongSumData
            } else LongSumData.EMPTY

    /**
     * Returns the `DoubleSummaryData` if type is [MetricDataType.SUMMARY], otherwise a default
     * empty data.
     *
     * @return the `DoubleSummaryData` if type is [MetricDataType.SUMMARY], otherwise a default *
     * empty data.
     */
    val doubleSummaryData: DoubleSummaryData
        get() =
            if (type == MetricDataType.SUMMARY) {
                data as DoubleSummaryData
            } else DoubleSummaryData.EMPTY

    /**
     * Returns the `DoubleHistogramData` if type is [MetricDataType.HISTOGRAM], otherwise a default
     * empty data.
     *
     * @return the `DoubleHistogramData` if type is [MetricDataType.HISTOGRAM], otherwise a default
     * empty data.
     */
    val doubleHistogramData: DoubleHistogramData
        get() =
            if (type == MetricDataType.HISTOGRAM) {
                data as DoubleHistogramData
            } else DoubleHistogramData.EMPTY

    /**
     * Returns the [ExponentialHistogramData] if type is [ ][MetricDataType.EXPONENTIAL_HISTOGRAM],
     * otherwise a default empty data.
     *
     * @return the [ExponentialHistogramData] if type is [ ][MetricDataType.EXPONENTIAL_HISTOGRAM],
     * otherwise a default empty data.
     */
    val exponentialHistogramData: ExponentialHistogramData
        get() =
            if (type == MetricDataType.EXPONENTIAL_HISTOGRAM) {
                data as ExponentialHistogramData
            } else DoubleExponentialHistogramData.EMPTY

    companion object {
        /**
         * Returns a new MetricData wih a [MetricDataType.DOUBLE_GAUGE] type.
         *
         * @return a new MetricData wih a [MetricDataType.DOUBLE_GAUGE] type.
         */
        fun createDoubleGauge(
            resource: Resource,
            instrumentationLibraryInfo: InstrumentationLibraryInfo,
            name: String,
            description: String,
            unit: String,
            data: DoubleGaugeData
        ): MetricData {
            return MetricDataImpl.create(
                resource,
                instrumentationLibraryInfo,
                name,
                description,
                unit,
                MetricDataType.DOUBLE_GAUGE,
                data
            )
        }

        /**
         * Returns a new MetricData wih a [MetricDataType.LONG_GAUGE] type.
         *
         * @return a new MetricData wih a [MetricDataType.LONG_GAUGE] type.
         */
        fun createLongGauge(
            resource: Resource,
            instrumentationLibraryInfo: InstrumentationLibraryInfo,
            name: String,
            description: String,
            unit: String,
            data: LongGaugeData
        ): MetricData {
            return MetricDataImpl.create(
                resource,
                instrumentationLibraryInfo,
                name,
                description,
                unit,
                MetricDataType.LONG_GAUGE,
                data
            )
        }

        /**
         * Returns a new MetricData wih a [MetricDataType.DOUBLE_SUM] type.
         *
         * @return a new MetricData wih a [MetricDataType.DOUBLE_SUM] type.
         */
        fun createDoubleSum(
            resource: Resource,
            instrumentationLibraryInfo: InstrumentationLibraryInfo,
            name: String,
            description: String,
            unit: String,
            data: DoubleSumData
        ): MetricData {
            return MetricDataImpl.create(
                resource,
                instrumentationLibraryInfo,
                name,
                description,
                unit,
                MetricDataType.DOUBLE_SUM,
                data
            )
        }

        /**
         * Returns a new MetricData wih a [MetricDataType.LONG_SUM] type.
         *
         * @return a new MetricData wih a [MetricDataType.LONG_SUM] type.
         */
        fun createLongSum(
            resource: Resource,
            instrumentationLibraryInfo: InstrumentationLibraryInfo,
            name: String,
            description: String,
            unit: String,
            data: LongSumData
        ): MetricData {
            return MetricDataImpl.create(
                resource,
                instrumentationLibraryInfo,
                name,
                description,
                unit,
                MetricDataType.LONG_SUM,
                data
            )
        }

        /**
         * Returns a new MetricData wih a [MetricDataType.SUMMARY] type.
         *
         * @return a new MetricData wih a [MetricDataType.SUMMARY] type.
         */
        fun createDoubleSummary(
            resource: Resource,
            instrumentationLibraryInfo: InstrumentationLibraryInfo,
            name: String,
            description: String,
            unit: String,
            data: DoubleSummaryData
        ): MetricData {
            return MetricDataImpl.create(
                resource,
                instrumentationLibraryInfo,
                name,
                description,
                unit,
                MetricDataType.SUMMARY,
                data
            )
        }

        /**
         * Returns a new MetricData with a [MetricDataType.HISTOGRAM] type.
         *
         * @return a new MetricData wih a [MetricDataType.HISTOGRAM] type.
         */
        fun createDoubleHistogram(
            resource: Resource,
            instrumentationLibraryInfo: InstrumentationLibraryInfo,
            name: String,
            description: String,
            unit: String,
            data: DoubleHistogramData
        ): MetricData {
            return MetricDataImpl.create(
                resource,
                instrumentationLibraryInfo,
                name,
                description,
                unit,
                MetricDataType.HISTOGRAM,
                data
            )
        }

        /**
         * Returns a new MetricData with a [MetricDataType.EXPONENTIAL_HISTOGRAM] type.
         *
         * @return a new MetricData wih a [MetricDataType.EXPONENTIAL_HISTOGRAM] type.
         */
        fun createExponentialHistogram(
            resource: Resource,
            instrumentationLibraryInfo: InstrumentationLibraryInfo,
            name: String,
            description: String,
            unit: String,
            data: ExponentialHistogramData
        ): MetricData {
            return MetricDataImpl.create(
                resource,
                instrumentationLibraryInfo,
                name,
                description,
                unit,
                MetricDataType.EXPONENTIAL_HISTOGRAM,
                data
            )
        }
    }
}
