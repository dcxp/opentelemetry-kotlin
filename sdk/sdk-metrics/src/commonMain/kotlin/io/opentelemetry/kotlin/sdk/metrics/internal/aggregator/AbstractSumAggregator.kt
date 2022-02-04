/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor

abstract class AbstractSumAggregator<T>(instrumentDescriptor: InstrumentDescriptor) :
    io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.Aggregator<T> {
    val isMonotonic: Boolean

    init {
        isMonotonic =
            io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.MetricDataUtils
                .isMonotonicInstrument(instrumentDescriptor)
    }
}
