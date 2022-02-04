/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context

/** Storage handle that aggregates across several instances. */
internal class MultiBoundStorageHandle(handles: List<BoundStorageHandle>) : BoundStorageHandle {
    private val underlyingHandles: List<BoundStorageHandle>

    init {
        underlyingHandles = handles
    }

    override fun recordLong(value: Long, attributes: Attributes, context: Context) {
        for (handle in underlyingHandles) {
            handle.recordLong(value, attributes, context)
        }
    }

    override fun recordDouble(value: Double, attributes: Attributes, context: Context) {
        for (handle in underlyingHandles) {
            handle.recordDouble(value, attributes, context)
        }
    }

    override fun release() {
        for (handle in underlyingHandles) {
            handle.release()
        }
    }
}
