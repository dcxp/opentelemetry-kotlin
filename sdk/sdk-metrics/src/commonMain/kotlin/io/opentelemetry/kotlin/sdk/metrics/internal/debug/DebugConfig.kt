/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.debug

import kotlinx.atomicfu.atomic

/**
 * Determines if the SDK is in debugging mode (captures stack traces) or not.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
object DebugConfig {
    private const val ENABLE_METRICS_DEBUG_PROPERTY = "otel.experimental.sdk.metrics.debug"
    private val isMetricsDebugEnabled_intern = atomic(false)
    /**
     * Returns true if metrics debugging is enabled.
     *
     * This will grab stack traces on instrument/view registration.
     */
    var isMetricsDebugEnabled
        get() = isMetricsDebugEnabled_intern.value
        private set(value) {
            isMetricsDebugEnabled_intern.lazySet(value)
        }

    init {
        // Attempt to mirror the logic in DefaultConfigProperties here...
        isMetricsDebugEnabled = true
        /*("true".equals(
            java.lang.System.getProperty(ENABLE_METRICS_DEBUG_PROPERTY),
            ignoreCase = true
        ) ||
            "true".equals(
                System.getenv(
                    ENABLE_METRICS_DEBUG_PROPERTY.lowercase().replace('.', '_')
                ),
                ignoreCase = true
            ))*/
    }

    /** Returns the message we send for how to enable better metrics debugging. */
    val howToEnableMessage: String
        get() =
            ("To enable better debugging, run your JVM with -D" +
                ENABLE_METRICS_DEBUG_PROPERTY +
                "=true")

    /** A mechanism to enable debugging for testing without having to recompile. */
    // Visible for testing
    fun enableForTesting(value: Boolean) {
        isMetricsDebugEnabled = value
    }
}
