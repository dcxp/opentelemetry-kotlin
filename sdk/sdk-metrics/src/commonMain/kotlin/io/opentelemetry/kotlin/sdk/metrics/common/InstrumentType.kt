/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.common

/** All instrument types available in the metric package. */
enum class InstrumentType {
    COUNTER,
    UP_DOWN_COUNTER,
    HISTOGRAM,
    OBSERVABLE_SUM,
    OBSERVABLE_UP_DOWN_SUM,
    OBSERVABLE_GAUGE
}
