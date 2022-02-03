/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.export.CollectionInfo
import io.opentelemetry.kotlin.sdk.resources.Resource
import kotlin.test.Test

/** Unit tests for [MetricStorageRegistry]. */
internal class MetricStorageRegistryTest {
    @Test
    fun register() {
        val meterSharedState = MeterSharedState.create(InstrumentationLibraryInfo.empty())
        val testInstrument = TestMetricStorage(METRIC_DESCRIPTOR)
        meterSharedState.metricStorageRegistry.register(testInstrument) shouldBe testInstrument
        meterSharedState.metricStorageRegistry.register(testInstrument) shouldBe testInstrument

        meterSharedState.metricStorageRegistry.register(
            TestMetricStorage(METRIC_DESCRIPTOR)
        ) shouldBe testInstrument
    }

    @Test
    fun register_OtherDescriptor() {
        val meterSharedState = MeterSharedState.create(InstrumentationLibraryInfo.empty())
        val testInstrument = TestMetricStorage(METRIC_DESCRIPTOR)
        meterSharedState.metricStorageRegistry.register(testInstrument) shouldBe testInstrument
        shouldThrow<IllegalArgumentException> {
                meterSharedState.metricStorageRegistry.register(
                    TestMetricStorage(OTHER_METRIC_DESCRIPTOR)
                )
            }
            .message shouldStartWith
            "Metric with same name and different descriptor already created."
    }

    @Test
    fun register_OtherInstance() {
        val meterSharedState = MeterSharedState.create(InstrumentationLibraryInfo.empty())
        val testInstrument = TestMetricStorage(METRIC_DESCRIPTOR)
        meterSharedState.metricStorageRegistry.register(testInstrument) shouldBe testInstrument
        shouldThrow<IllegalArgumentException> {
                meterSharedState.metricStorageRegistry.register(
                    OtherTestMetricStorage(METRIC_DESCRIPTOR)
                )
            }
            .message shouldStartWith
            "Metric with same name and different instrument already created."
    }

    private class TestMetricStorage(override val metricDescriptor: MetricDescriptor) :
        MetricStorage, WriteableMetricStorage {

        override fun collectAndReset(
            collectionInfo: CollectionInfo,
            resource: Resource,
            instrumentationLibraryInfo: InstrumentationLibraryInfo,
            startEpochNanos: Long,
            epochNanos: Long,
            suppressSynchronousCollection: Boolean
        ): MetricData? {
            return null
        }

        override fun bind(attributes: Attributes): BoundStorageHandle {
            return object : BoundStorageHandle {
                override fun recordLong(value: Long, attributes: Attributes, context: Context) {}

                override fun recordDouble(
                    value: Double,
                    attributes: Attributes,
                    context: Context
                ) {}

                override fun release() {}
            }
        }
    }

    private class OtherTestMetricStorage(override val metricDescriptor: MetricDescriptor) :
        MetricStorage, WriteableMetricStorage {

        override fun collectAndReset(
            collectionInfo: CollectionInfo,
            resource: Resource,
            instrumentationLibraryInfo: InstrumentationLibraryInfo,
            startEpochNanos: Long,
            epochNanos: Long,
            suppressSynchronousCollection: Boolean
        ): MetricData? {
            return null
        }

        override fun bind(attributes: Attributes): BoundStorageHandle {
            return object : BoundStorageHandle {
                override fun recordLong(value: Long, attributes: Attributes, context: Context) {}

                override fun recordDouble(
                    value: Double,
                    attributes: Attributes,
                    context: Context
                ) {}

                override fun release() {}
            }
        }
    }

    companion object {
        private val METRIC_DESCRIPTOR = MetricDescriptor.create("name", "description", "1")
        private val OTHER_METRIC_DESCRIPTOR =
            MetricDescriptor.create("name", "other_description", "1")
    }
}
