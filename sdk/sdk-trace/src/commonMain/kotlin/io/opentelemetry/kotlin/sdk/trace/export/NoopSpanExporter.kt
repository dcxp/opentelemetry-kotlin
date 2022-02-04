/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.export

import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.trace.data.SpanData

internal class NoopSpanExporter : SpanExporter {
    override fun export(spans: Collection<SpanData>): CompletableResultCode {
        return CompletableResultCode.ofSuccess()
    }

    override fun flush(): CompletableResultCode {
        return CompletableResultCode.ofSuccess()
    }

    override fun shutdown(): CompletableResultCode {
        return CompletableResultCode.ofSuccess()
    }

    companion object {
        private val INSTANCE: SpanExporter = NoopSpanExporter()
        val instance: SpanExporter
            get() = INSTANCE
    }
}
