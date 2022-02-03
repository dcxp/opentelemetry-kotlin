/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.exemplar

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.metrics.mock.ExemplarReservoirMock
import kotlin.test.Test

internal class FilteredExemplarReservoirTest {
    var reservoir = ExemplarReservoirMock()

    var filter: ExemplarFilter =
        object : ExemplarFilter {
            override fun shouldSampleMeasurement(
                value: Long,
                attributes: Attributes,
                context: Context
            ): Boolean {
                return false
            }

            override fun shouldSampleMeasurement(
                value: Double,
                attributes: Attributes,
                context: Context
            ): Boolean {
                return false
            }
        }
    @Test
    fun testFilter_preventsSamplingDoubles() {
        val filter =
            object : ExemplarFilter {
                override fun shouldSampleMeasurement(
                    value: Long,
                    attributes: Attributes,
                    context: Context
                ): Boolean {
                    return false
                }

                override fun shouldSampleMeasurement(
                    value: Double,
                    attributes: Attributes,
                    context: Context
                ): Boolean {
                    return false
                }
            }
        val filtered: ExemplarReservoir = FilteredExemplarReservoir(filter, reservoir)
        filtered.offerMeasurement(1.0, Attributes.empty(), Context.root())
    }

    @Test
    fun testFilter_allowsSamplingDoubles() {
        filter =
            object : ExemplarFilter {
                override fun shouldSampleMeasurement(
                    value: Long,
                    attributes: Attributes,
                    context: Context
                ): Boolean {
                    return false
                }

                override fun shouldSampleMeasurement(
                    value: Double,
                    attributes: Attributes,
                    context: Context
                ): Boolean {
                    return true
                }
            }
        val filtered: ExemplarReservoir = FilteredExemplarReservoir(filter, reservoir)
        filtered.offerMeasurement(1.0, Attributes.empty(), Context.root())
        assertSoftly(reservoir) {
            value shouldBe 1.0
            attributes shouldBe Attributes.empty()
            context shouldBe Context.root()
        }
    }

    @Test
    fun testFilter_preventsSamplingLongs() {
        val filtered: ExemplarReservoir = FilteredExemplarReservoir(filter, reservoir)
        filtered.offerMeasurement(1L, Attributes.empty(), Context.root())
    }

    @Test
    fun testFilter_allowsSamplingLongs() {
        filter =
            object : ExemplarFilter {
                override fun shouldSampleMeasurement(
                    value: Long,
                    attributes: Attributes,
                    context: Context
                ): Boolean {
                    return true
                }

                override fun shouldSampleMeasurement(
                    value: Double,
                    attributes: Attributes,
                    context: Context
                ): Boolean {
                    return false
                }
            }
        val filtered: ExemplarReservoir = FilteredExemplarReservoir(filter, reservoir)
        filtered.offerMeasurement(1L, Attributes.empty(), Context.root())
        assertSoftly(reservoir) {
            value shouldBe 1.0
            attributes shouldBe Attributes.empty()
            context shouldBe Context.root()
        }
    }

    @Test
    fun reservoir_collectsUnderlying() {
        val filtered: ExemplarReservoir = FilteredExemplarReservoir(filter, reservoir)
        filtered.collectAndReset(Attributes.empty()).shouldBeEmpty()
    }
}
