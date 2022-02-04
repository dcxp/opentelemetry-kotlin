/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.kotest.matchers.shouldNotBe
import io.opentelemetry.kotlin.api.trace.SpanId
import io.opentelemetry.kotlin.api.trace.TraceId
import kotlin.test.Test

internal class RandomIdGeneratorTest {
    @Test
    fun defaults() {
        val generator = IdGenerator.random()

        // Can't assert values but can assert they're valid, try a lot as a sort of fuzz check.
        for (i in 0..99) {
            val traceId = generator.generateTraceId()
            traceId shouldNotBe TraceId.invalid
            val spanId = generator.generateSpanId()
            spanId shouldNotBe SpanId.invalid
        }
    }
}
