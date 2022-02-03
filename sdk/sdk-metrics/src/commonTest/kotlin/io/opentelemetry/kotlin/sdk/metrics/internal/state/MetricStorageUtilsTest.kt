/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.kotest.matchers.maps.shouldContainExactly
import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.Aggregator
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.AggregatorHandle
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.resources.Resource
import kotlin.test.Test

internal class MetricStorageUtilsTest {
    private val result: MutableMap<Attributes, String?> =
        mutableMapOf(a to "A", b to "B", d to null)
    private val toMerge: Map<Attributes, String?> = mutableMapOf(b to "B'", c to "C", e to null)

    @Test
    fun mergeInPlace() {
        val agg = buildConcatAggregator()
        MetricStorageUtils.mergeInPlace(result, toMerge, agg)
        result.shouldContainExactly(mapOf(b to "merge(B,B')", c to "C"))
    }

    @Test
    fun diffInPlace() {
        val agg = buildConcatAggregator()
        MetricStorageUtils.diffInPlace(result, toMerge, agg)
        result.shouldContainExactly(mapOf(b to "diff(B,B')", c to "C"))
    }

    companion object {
        private val a = Attributes.of(AttributeKey.stringKey("a"), "a")
        private val b = Attributes.of(AttributeKey.stringKey("b"), "b")
        private val c = Attributes.of(AttributeKey.stringKey("c"), "c")
        private val d = Attributes.of(AttributeKey.stringKey("d"), "d")
        private val e = Attributes.of(AttributeKey.stringKey("e"), "e")
        private fun buildConcatAggregator(): Aggregator<String?> {
            val agg =
                object : Aggregator<String?> {
                    override fun createHandle(): AggregatorHandle<String?> {
                        TODO("Not yet implemented")
                    }

                    override fun merge(previousCumulative: String?, delta: String?): String? {
                        return "merge($previousCumulative,$delta)"
                    }

                    override fun diff(previousCumulative: String?, delta: String?): String? {
                        return "diff($previousCumulative,$delta)"
                    }

                    override fun toMetricData(
                        resource: Resource,
                        instrumentationLibrary: InstrumentationLibraryInfo,
                        metricDescriptor: MetricDescriptor,
                        accumulationByLabels: Map<Attributes, String?>,
                        temporality: AggregationTemporality,
                        startEpochNanos: Long,
                        lastCollectionEpoch: Long,
                        epochNanos: Long
                    ): MetricData {
                        TODO("Not yet implemented")
                    }
                }
            return agg
        }
    }
}
