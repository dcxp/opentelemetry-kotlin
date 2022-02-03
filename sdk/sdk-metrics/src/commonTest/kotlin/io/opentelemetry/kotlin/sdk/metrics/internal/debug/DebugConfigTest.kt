/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.debug

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import kotlin.test.Test

internal class DebugConfigTest {
    @Test
    fun enableForTests() {
        DebugConfig.enableForTesting(true)
        DebugConfig.isMetricsDebugEnabled.shouldBeTrue()
        DebugConfig.enableForTesting(false)
        DebugConfig.isMetricsDebugEnabled.shouldBeFalse()
    }

    @Test
    fun hasActionableMessage() {
        // Ensure error message includes system property.
        DebugConfig.howToEnableMessage.contains("-D")
    }
}
