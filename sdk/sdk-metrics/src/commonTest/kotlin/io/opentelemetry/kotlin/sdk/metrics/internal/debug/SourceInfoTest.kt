/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.debug

import kotlin.test.Test

internal class SourceInfoTest {
    @Test
    fun noSourceInfo_includesEnableDebugMessage() {
        SourceInfo.noSourceInfo().multiLineDebugString().contains(DebugConfig.howToEnableMessage)
    }
    /*
    @Test
    fun doesNotGrabStackWhenDisabled() {
        DebugConfig.enableForTesting(false)
        assertThat(SourceInfo.fromCurrentStack()).isInstanceOf(NoSourceInfo::class.java)
    }

    @Test
    fun doesGrabStackWhenEnabled() {
        DebugConfig.enableForTesting(true)
        assertThat(SourceInfo.fromCurrentStack()).isInstanceOf(
            StackTraceSourceInfo::class.java
        )
    }*/
}
