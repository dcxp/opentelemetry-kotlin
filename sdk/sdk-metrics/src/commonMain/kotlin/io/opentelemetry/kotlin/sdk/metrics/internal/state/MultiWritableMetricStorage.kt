/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.opentelemetry.kotlin.api.common.Attributes

internal class MultiWritableMetricStorage(
    private val underlyingMetrics: List<WriteableMetricStorage>
) : WriteableMetricStorage {

    override fun bind(attributes: Attributes): BoundStorageHandle {
        val handles = underlyingMetrics.map { metric -> metric.bind(attributes) }
        return MultiBoundStorageHandle(handles)
    }
}
