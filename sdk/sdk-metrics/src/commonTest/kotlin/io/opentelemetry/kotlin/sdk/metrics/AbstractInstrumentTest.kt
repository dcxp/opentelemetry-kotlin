/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentType
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentValueType
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor
import kotlin.test.Test

/** Unit tests for [AbstractInstrument]. */
internal class AbstractInstrumentTest {
    @Test
    fun getValues() {
        val testInstrument = TestInstrument(INSTRUMENT_DESCRIPTOR)
        testInstrument.getDescriptor() shouldBe INSTRUMENT_DESCRIPTOR
    }

    private class TestInstrument(descriptor: InstrumentDescriptor?) :
        AbstractInstrument(descriptor!!)

    companion object {
        private val INSTRUMENT_DESCRIPTOR =
            InstrumentDescriptor.create(
                "name",
                "description",
                "1",
                InstrumentType.COUNTER,
                InstrumentValueType.LONG
            )
    }
}
