/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldHaveSize
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentType
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentValueType
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarFilter
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.DoubleAccumulation
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.export.CollectionHandle
import io.opentelemetry.kotlin.sdk.metrics.view.Aggregation
import kotlin.test.Test

internal class DeltaMetricStorageTest {
    private val collector1: CollectionHandle
    private val collector2: CollectionHandle
    private val allCollectors: Set<CollectionHandle>
    private val storage: DeltaMetricStorage<DoubleAccumulation>
    init {
        val supplier = CollectionHandle.createSupplier()
        collector1 = supplier.get()
        collector2 = supplier.get()
        allCollectors = setOf(collector1, collector2)
        storage =
            DeltaMetricStorage(
                Aggregation.sum().createAggregator(DESCRIPTOR, ExemplarFilter.neverSample()),
                DESCRIPTOR
            )
    }

    @Test
    fun collectionDeltaForMultiReader() {
        val bound = storage.bind(Attributes.empty())
        bound.recordDouble(1.0, Attributes.empty(), Context.root())
        // First collector only sees first recording.
        assertSoftly(storage.collectFor(collector1, allCollectors, false)) {
            shouldHaveSize(1)
            assertSoftly(entries) {
                shouldContain(
                    Attributes.empty(),
                    DoubleAccumulation.Companion.create(1.0, listOf())
                )
            }
        }
        bound.recordDouble(2.0, Attributes.empty(), Context.root())
        // First collector only sees second recording.
        assertSoftly(storage.collectFor(collector1, allCollectors, false)) {
            shouldHaveSize(1)
            assertSoftly(entries) {
                shouldContain(
                    Attributes.empty(),
                    DoubleAccumulation.Companion.create(2.0, listOf())
                )
            }
        }

        // First collector no longer sees a recording.
        storage.collectFor(collector1, allCollectors, false).shouldHaveSize(0)

        assertSoftly(storage.collectFor(collector2, allCollectors, false)) {
            shouldHaveSize(1)
            assertSoftly(entries) {
                shouldContain(
                    Attributes.empty(),
                    DoubleAccumulation.Companion.create(3.0, listOf())
                )
            }
        }

        // Second collector no longer sees a recording.
        storage.collectFor(collector2, allCollectors, false).shouldHaveSize(0)
    }

    @Test
    fun avoidCollectionInRapidSuccession() {
        val bound = storage.bind(Attributes.empty())
        bound.recordDouble(1.0, Attributes.empty(), Context.root())
        // First collector only sees first recording.
        assertSoftly(storage.collectFor(collector1, allCollectors, false)) {
            shouldHaveSize(1)
            assertSoftly(entries) {
                shouldContain(
                    Attributes.empty(),
                    DoubleAccumulation.Companion.create(1.0, listOf())
                )
            }
        }
        // Add some data immediately after read, but pretent it hasn't been long.
        bound.recordDouble(2.0, Attributes.empty(), Context.root())
        // Collector1 doesn't see new data, because we don't recollect, but collector2 sees old
        // delta.
        storage.collectFor(collector1, allCollectors, true).shouldHaveSize(0)

        assertSoftly(storage.collectFor(collector2, allCollectors, true)) {
            shouldHaveSize(1)
            assertSoftly(entries) {
                shouldContain(
                    Attributes.empty(),
                    DoubleAccumulation.Companion.create(1.0, listOf())
                )
            }
        }
        // After enough time passes, collector1 sees new data
        assertSoftly(storage.collectFor(collector1, allCollectors, false)) {
            shouldHaveSize(1)
            assertSoftly(entries) {
                shouldContain(
                    Attributes.empty(),
                    DoubleAccumulation.Companion.create(2.0, listOf())
                )
            }
        }
    }

    companion object {
        private val DESCRIPTOR =
            InstrumentDescriptor.create(
                "name",
                "description",
                "unit",
                InstrumentType.COUNTER,
                InstrumentValueType.DOUBLE
            )
    }
}
