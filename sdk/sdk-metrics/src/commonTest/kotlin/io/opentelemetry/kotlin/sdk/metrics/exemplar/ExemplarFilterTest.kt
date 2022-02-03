/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.exemplar

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.trace.Span
import io.opentelemetry.kotlin.api.trace.SpanContext
import io.opentelemetry.kotlin.api.trace.TraceFlags
import io.opentelemetry.kotlin.api.trace.TraceState
import io.opentelemetry.kotlin.context.Context
import kotlin.test.Test

internal class ExemplarFilterTest {
    @Test
    fun never_NeverReturnsTrue() {

        ExemplarFilter.neverSample()
            .shouldSampleMeasurement(1, Attributes.empty(), Context.root())
            .shouldBeFalse()
    }

    @Test
    fun always_AlwaysReturnsTrue() {

        ExemplarFilter.alwaysSample()
            .shouldSampleMeasurement(1, Attributes.empty(), Context.root())
            .shouldBeTrue()
    }

    @Test
    fun withSampledTrace_ReturnsFalseOnNoContext() {

        ExemplarFilter.sampleWithTraces()
            .shouldSampleMeasurement(1, Attributes.empty(), Context.root())
            .shouldBeFalse()
    }

    @Test
    fun withSampledTrace_sampleWithTrace() {
        val context =
            Context.root()
                .with(
                    Span.wrap(
                        SpanContext.createFromRemoteParent(
                            TRACE_ID,
                            SPAN_ID,
                            TraceFlags.sampled,
                            TraceState.default
                        )
                    )
                )

        ExemplarFilter.sampleWithTraces()
            .shouldSampleMeasurement(1, Attributes.empty(), context)
            .shouldBeTrue()
    }

    @Test
    fun withSampledTrace_notSampleUnsampledTrace() {
        val context =
            Context.root()
                .with(
                    Span.wrap(
                        SpanContext.createFromRemoteParent(
                            TRACE_ID,
                            SPAN_ID,
                            TraceFlags.default,
                            TraceState.default
                        )
                    )
                )

        ExemplarFilter.sampleWithTraces()
            .shouldSampleMeasurement(1, Attributes.empty(), context)
            .shouldBeFalse()
    }

    companion object {
        private const val TRACE_ID = "ff000000000000000000000000000041"
        private const val SPAN_ID = "ff00000000000041"
    }
}
