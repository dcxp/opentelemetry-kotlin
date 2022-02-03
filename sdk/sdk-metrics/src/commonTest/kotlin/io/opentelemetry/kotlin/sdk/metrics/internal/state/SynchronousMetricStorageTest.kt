/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentType
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentValueType
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarFilter
import io.opentelemetry.kotlin.sdk.metrics.exemplar.mock.AttributesProcessorMock
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.Aggregator
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.export.CollectionHandle
import io.opentelemetry.kotlin.sdk.metrics.internal.export.CollectionInfo
import io.opentelemetry.kotlin.sdk.metrics.internal.view.AttributesProcessor
import io.opentelemetry.kotlin.sdk.metrics.mock.MetricReaderMock
import io.opentelemetry.kotlin.sdk.metrics.view.Aggregation
import io.opentelemetry.kotlin.sdk.resources.Resource
import io.opentelemetry.kotlin.sdk.testing.time.TestClock
import kotlin.test.Test

class SynchronousMetricStorageTest {
    private val testClock: TestClock = TestClock.create()
    private val aggregator: Aggregator<Long> =
        Aggregation.lastValue().createAggregator(DESCRIPTOR, ExemplarFilter.neverSample())
    private val spyAttributesProcessor = AttributesProcessorMock()
    private val reader = MetricReaderMock()
    private val collector: CollectionHandle = CollectionHandle.createSupplier().get()
    private val allCollectors: Set<CollectionHandle> = setOf(collector)

    @Test
    fun attributesProcessor_used() {
        val accumulator: SynchronousMetricStorage =
            DefaultSynchronousMetricStorage(METRIC_DESCRIPTOR, aggregator, spyAttributesProcessor)
        accumulator.bind(Attributes.empty())

        assertSoftly(spyAttributesProcessor.calls.last()) {
            incoming shouldBe Attributes.empty()
            context shouldBe Context.current()
        }
    }

    @Test
    fun attributesProcessor_applied() {
        val labels = Attributes.builder().put("K", "V").build()
        val attributesProcessor =
            AttributesProcessor.append(Attributes.builder().put("modifiedK", "modifiedV").build())
        val spyLabelsProcessor = AttributesProcessorMock(attributesProcessor)
        val accumulator: SynchronousMetricStorage =
            DefaultSynchronousMetricStorage(METRIC_DESCRIPTOR, aggregator, spyLabelsProcessor)
        val handle = accumulator.bind(labels)
        handle.recordDouble(1.0, labels, Context.root())
        val md =
            accumulator.collectAndReset(
                CollectionInfo.create(collector, allCollectors, reader),
                RESOURCE,
                INSTRUMENTATION_LIBRARY_INFO,
                0,
                testClock.now(),
                false
            )
        assertSoftly(md!!) {
            doubleGaugeData.shouldNotBeNull()
            doubleGaugeData.points.forEach { point ->
                assertSoftly(point.attributes.asMap()) {
                    shouldHaveSize(2)
                    shouldContain(AttributeKey.stringKey("modifiedK"), "modifiedV")
                    shouldContain(AttributeKey.stringKey("K"), "V")
                }
            }
        }
    }

    @Test
    fun sameAggregator_ForSameAttributes() {
        val accumulator: SynchronousMetricStorage =
            DefaultSynchronousMetricStorage(METRIC_DESCRIPTOR, aggregator, spyAttributesProcessor)
        val handle = accumulator.bind(Attributes.builder().put("K", "V").build())
        val duplicateHandle = accumulator.bind(Attributes.builder().put("K", "V").build())
        try {
            duplicateHandle shouldBe handle
            accumulator.collectAndReset(
                CollectionInfo.create(collector, allCollectors, reader),
                RESOURCE,
                INSTRUMENTATION_LIBRARY_INFO,
                0,
                testClock.now(),
                false
            )
            val anotherDuplicateAggregatorHandle =
                accumulator.bind(Attributes.builder().put("K", "V").build())
            try {
                anotherDuplicateAggregatorHandle shouldBe handle
            } finally {
                anotherDuplicateAggregatorHandle.release()
            }
        } finally {
            duplicateHandle.release()
            handle.release()
        }

        // If we try to collect once all bound references are gone AND no recordings have occurred,
        // we
        // should not see any labels (or metric).
        accumulator
            .collectAndReset(
                CollectionInfo.create(collector, allCollectors, reader),
                RESOURCE,
                INSTRUMENTATION_LIBRARY_INFO,
                0,
                testClock.now(),
                false
            )
            .shouldBeNull()
    }

    companion object {
        private val RESOURCE = Resource.empty()
        private val INSTRUMENTATION_LIBRARY_INFO = InstrumentationLibraryInfo.create("test", "1.0")
        private val DESCRIPTOR =
            InstrumentDescriptor.create(
                "name",
                "description",
                "unit",
                InstrumentType.COUNTER,
                InstrumentValueType.DOUBLE
            )
        private val METRIC_DESCRIPTOR = MetricDescriptor.create("name", "description", "unit")
    }
}
