/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.view

import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarFilter
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.Aggregator
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor

/** Configuration representing no aggregation. */
internal class NoAggregation private constructor() : Aggregation() {
    override fun <T> createAggregator(
        instrumentDescriptor: InstrumentDescriptor,
        exemplarFilter: ExemplarFilter
    ): Aggregator<T> {
        return Aggregator.empty() as Aggregator<T>
    }

    override fun toString(): String {
        return "NoAggregation"
    }

    companion object {
        val INSTANCE: Aggregation = NoAggregation()
    }
}
