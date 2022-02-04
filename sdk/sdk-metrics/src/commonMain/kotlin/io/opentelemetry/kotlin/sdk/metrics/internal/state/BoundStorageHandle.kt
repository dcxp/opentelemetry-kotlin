/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context

/**
 * A bound handle for recording measurements against a particular set of attributes.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
interface BoundStorageHandle {
    /** Records a measurement. */
    fun recordLong(value: Long, attributes: Attributes, context: Context)

    /** Records a measurement. */
    fun recordDouble(value: Double, attributes: Attributes, context: Context)

    /** Release this handle back to the storage. */
    fun release()
}
