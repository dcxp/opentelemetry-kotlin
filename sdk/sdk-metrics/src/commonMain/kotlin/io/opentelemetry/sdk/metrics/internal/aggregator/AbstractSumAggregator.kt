/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.sdk.metrics.internal.aggregator

import io.opentelemetry.sdk.metrics.internal.descriptor.InstrumentDescriptor

abstract class AbstractSumAggregator<T>(instrumentDescriptor: InstrumentDescriptor) :
    io.opentelemetry.sdk.metrics.internal.aggregator.Aggregator<T> {
    val isMonotonic: Boolean

    init {
        isMonotonic =
            io.opentelemetry.sdk.metrics.internal.aggregator.MetricDataUtils.isMonotonicInstrument(
                instrumentDescriptor
            )
    }
}
