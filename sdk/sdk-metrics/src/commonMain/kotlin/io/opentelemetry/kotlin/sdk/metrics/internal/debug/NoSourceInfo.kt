/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.debug

internal enum class NoSourceInfo : SourceInfo {
    INSTANCE;

    override fun shortDebugString(): String {
        return "unknown source"
    }

    override fun multiLineDebugString(): String {
        return """	at unknown source
		${DebugConfig.howToEnableMessage}"""
    }
}
