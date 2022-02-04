/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.export

import io.opentelemetry.kotlin.sdk.metrics.data.MetricData

/**
 * `MetricProducer` is the interface that is used to make metric data available to the OpenTelemetry
 * exporters. Implementations should be stateful, in that each call to [ ][.collectAllMetrics] will
 * return any metric generated since the last call was made.
 *
 * Implementations must be thread-safe.
 */
interface MetricProducer {
    /**
     * Returns a collection of produced [MetricData]s to be exported. This will only be those
     * metrics that have been produced since the last time this method was called.
     *
     * @return a collection of produced [MetricData]s to be exported.
     */
    fun collectAllMetrics(): Collection<MetricData>
}
