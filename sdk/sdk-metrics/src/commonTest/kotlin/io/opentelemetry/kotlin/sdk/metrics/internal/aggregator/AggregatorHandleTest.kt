/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.metrics.data.DoubleExemplarData
import io.opentelemetry.kotlin.sdk.metrics.data.ExemplarData
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarReservoir
import io.opentelemetry.kotlin.sdk.metrics.mock.ExemplarReservoirMock
import kotlinx.atomicfu.atomic
import kotlin.test.Test

class AggregatorHandleTest {
    var reservoir = ExemplarReservoirMock()
    @Test
    fun acquireMapped() {
        val testAggregator = TestAggregatorHandle(reservoir)
        testAggregator.acquire().shouldBeTrue()
        testAggregator.release()
        testAggregator.acquire().shouldBeTrue()
        testAggregator.acquire().shouldBeTrue()
        testAggregator.release()
        testAggregator.acquire().shouldBeTrue()
        testAggregator.release()
        testAggregator.release()
    }

    @Test
    fun tryUnmap_AcquiredHandler() {
        val testAggregator = TestAggregatorHandle(reservoir)
        testAggregator.acquire().shouldBeTrue()
        testAggregator.tryUnmap().shouldBeFalse()
        testAggregator.release()
        // The aggregator is by default acquired, so need an extra release.
        testAggregator.tryUnmap().shouldBeFalse()
        testAggregator.release()
        testAggregator.tryUnmap().shouldBeTrue()
    }

    @Test
    fun tryUnmap_AcquiredHandler_MultipleTimes() {
        val testAggregator = TestAggregatorHandle(reservoir)
        testAggregator.acquire().shouldBeTrue()
        testAggregator.acquire().shouldBeTrue()
        testAggregator.acquire().shouldBeTrue()
        testAggregator.tryUnmap().shouldBeFalse()
        testAggregator.release()
        testAggregator.acquire().shouldBeTrue()
        testAggregator.tryUnmap().shouldBeFalse()
        testAggregator.release()
        testAggregator.tryUnmap().shouldBeFalse()
        testAggregator.release()
        testAggregator.tryUnmap().shouldBeFalse()
        testAggregator.release()
        // The aggregator is by default acquired, so need an extra release.
        testAggregator.tryUnmap().shouldBeFalse()
        testAggregator.release()
        testAggregator.tryUnmap().shouldBeTrue()
    }

    @Test
    fun bind_ThenUnmap_ThenTryToBind() {
        val testAggregator = TestAggregatorHandle(reservoir)
        testAggregator.release()
        testAggregator.tryUnmap().shouldBeTrue()
        testAggregator.acquire().shouldBeFalse()
        testAggregator.release()
    }

    @Test
    fun testRecordings() {
        val testAggregator = TestAggregatorHandle(reservoir)
        testAggregator.recordLong(22)
        testAggregator.recordedLong.value shouldBe 22
        testAggregator.recordedDouble.value shouldBe 0
        testAggregator.accumulateThenReset(Attributes.empty())
        testAggregator.recordedLong.value shouldBe 0
        testAggregator.recordedDouble.value shouldBe 0
        testAggregator.recordDouble(33.55)
        testAggregator.recordedLong.value shouldBe 0
        testAggregator.recordedDouble.value shouldBe 33.55
        testAggregator.accumulateThenReset(Attributes.empty())
        testAggregator.recordedLong.value shouldBe 0
        testAggregator.recordedDouble.value shouldBe 0
    }

    @Test
    fun testOfferMeasurementLongToExemplar() {
        val testAggregator = TestAggregatorHandle(reservoir)
        val attributes = Attributes.builder().put("test", "value").build()
        val context = Context.root()
        testAggregator.recordLong(1L, attributes, context)
        assertSoftly(reservoir) {
            value shouldBe 1.0
            attributes shouldBe attributes
            context shouldBe context
        }
    }

    @Test
    fun testOfferMeasurementDoubleToExemplar() {
        val testAggregator = TestAggregatorHandle(reservoir)
        val attributes = Attributes.builder().put("test", "value").build()
        val context = Context.root()
        testAggregator.recordDouble(1.0, attributes, context)
        assertSoftly(reservoir) {
            value shouldBe 1.0
            attributes shouldBe attributes
            context shouldBe context
        }
    }

    @Test
    fun testGenerateExemplarsOnCollect() {
        val attributes = Attributes.builder().put("test", "value").build()
        val result: ExemplarData =
            DoubleExemplarData.create(attributes, 2L, "spanid", "traceid", 1.0)
        reservoir = ExemplarReservoirMock(result)
        val testAggregator = TestAggregatorHandle(reservoir)

        // We need to first record a value so that collect and reset does something.
        testAggregator.recordDouble(1.0, Attributes.empty(), Context.root())
        testAggregator.accumulateThenReset(attributes)
        testAggregator.recordedExemplars.value.shouldContainInOrder(result)
    }

    private class TestAggregatorHandle constructor(reservoir: ExemplarReservoir) :
        AggregatorHandle<Unit>(reservoir) {
        val recordedLong = atomic(0L)
        val recordedDouble = atomic(0.0)
        val recordedExemplars = atomic(listOf<ExemplarData>())

        override fun doAccumulateThenReset(exemplars: List<ExemplarData>) {
            recordedLong.lazySet(0)
            recordedDouble.lazySet(0.0)
            recordedExemplars.lazySet(exemplars)
        }

        override fun doRecordLong(value: Long) {
            recordedLong.lazySet(value)
        }

        override fun doRecordDouble(value: Double) {
            recordedDouble.lazySet(value)
        }
    }
}
