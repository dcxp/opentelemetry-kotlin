/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor

/**
 * There are multiple metrics defined with the same name/identity.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
class DuplicateMetricStorageException(
    val existing: MetricDescriptor,
    val conflict: MetricDescriptor,
    message: String
) :
    IllegalArgumentException(
        "$message Found previous metric: $existing, cannot register metric: $conflict"
    ) {

    companion object {
        private const val serialVersionUID = 1547329629200005982L
    }
}
