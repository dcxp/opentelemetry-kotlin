/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.opentelemetry.kotlin.api.trace.SpanBuilder
import io.opentelemetry.kotlin.api.trace.Tracer
import io.opentelemetry.kotlin.api.trace.TracerProvider
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo

/** [SdkTracer] is SDK implementation of [Tracer]. */
internal class SdkTracer(
    sharedState: TracerSharedState,
    instrumentationLibraryInfo: InstrumentationLibraryInfo
) : Tracer {
    private val sharedState: TracerSharedState
    private val instrumentationLibraryInfo: InstrumentationLibraryInfo

    init {
        this.sharedState = sharedState
        this.instrumentationLibraryInfo = instrumentationLibraryInfo
    }

    override fun spanBuilder(spanName: String): SpanBuilder {
        var spanName = spanName
        if (spanName.trim { it <= ' ' }.isEmpty()) {
            spanName = FALLBACK_SPAN_NAME
        }
        if (sharedState.hasBeenShutdown()) {
            val tracer = TracerProvider.noop()[instrumentationLibraryInfo.name]
            return tracer.spanBuilder(spanName)
        }
        return SdkSpanBuilder(
            spanName,
            instrumentationLibraryInfo,
            sharedState,
            sharedState.spanLimits
        )
    }

    /**
     * Returns the instrumentation library specified when creating the tracer.
     *
     * @return an instance of [InstrumentationLibraryInfo]
     */
    fun getInstrumentationLibraryInfo(): InstrumentationLibraryInfo {
        return instrumentationLibraryInfo
    }

    companion object {
        const val FALLBACK_SPAN_NAME = "<unspecified span name>"
    }
}
