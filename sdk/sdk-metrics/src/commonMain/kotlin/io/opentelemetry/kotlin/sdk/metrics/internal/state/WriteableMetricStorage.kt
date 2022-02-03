/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData

/**
 * Stores [MetricData] and allows synchronous writes of measurements.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
interface WriteableMetricStorage {
    /** Bind an efficient storage handle for a set of attributes. */
    fun bind(attributes: Attributes): BoundStorageHandle

    /** Records a measurement. */
    fun recordLong(value: Long, attributes: Attributes, context: Context) {
        val handle: BoundStorageHandle = bind(attributes)
        try {
            handle.recordLong(value, attributes, context)
        } finally {
            handle.release()
        }
    }

    /** Records a measurement. */
    fun recordDouble(value: Double, attributes: Attributes, context: Context) {
        val handle: BoundStorageHandle = bind(attributes)
        try {
            handle.recordDouble(value, attributes, context)
        } finally {
            handle.release()
        }
    }
}
