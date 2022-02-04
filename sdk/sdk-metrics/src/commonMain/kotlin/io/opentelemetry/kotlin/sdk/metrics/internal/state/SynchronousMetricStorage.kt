/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarFilter
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.Aggregator
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.metrics.view.View

/**
 * Stores aggregated [MetricData] for synchronous instruments.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
interface SynchronousMetricStorage : MetricStorage, WriteableMetricStorage {
    companion object {
        /** Returns metric storage which doesn't store or generate any metrics. */
        fun empty(): SynchronousMetricStorage {
            return EmptyMetricStorage.INSTANCE
        }

        /**
         * Constructs metric storage for a given synchronous instrument and view.
         *
         * @return The storage, or `null` if the instrument should not be recorded.
         */
        fun <T> create(
            view: View,
            instrumentDescriptor: InstrumentDescriptor,
            exemplarFilter: ExemplarFilter
        ): SynchronousMetricStorage {
            val metricDescriptor: MetricDescriptor =
                MetricDescriptor.create(view, instrumentDescriptor)
            val aggregator: Aggregator<T> =
                view.aggregation.createAggregator(instrumentDescriptor, exemplarFilter)
            // We won't be storing this metric.
            return if (Aggregator.empty() === aggregator) {
                empty()
            } else
                DefaultSynchronousMetricStorage<T>(
                    metricDescriptor,
                    aggregator,
                    view.attributesProcessor
                )
        }
    }
}
