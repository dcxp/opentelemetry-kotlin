/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.descriptor

import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentType
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentValueType
import io.opentelemetry.kotlin.sdk.metrics.view.View
import kotlin.test.Test

internal class MetricDescriptorTest {
    @Test
    fun metricDescriptor_preservesInstrumentDescriptor() {
        val view = View.builder().build()
        val instrument =
            InstrumentDescriptor.create(
                "name",
                "description",
                "unit",
                InstrumentType.COUNTER,
                InstrumentValueType.DOUBLE
            )
        val simple = MetricDescriptor.create(view, instrument)
        simple.name shouldBe "name"
        simple.description shouldBe "description"
        simple.unit shouldBe "unit"
        simple.sourceView shouldBe view
        simple.sourceInstrument shouldBe instrument
    }

    @Test
    fun metricDescriptor_overridesFromView() {
        val view = View.builder().setName("new_name").setDescription("new_description").build()
        val instrument =
            InstrumentDescriptor.create(
                "name",
                "description",
                "unit",
                InstrumentType.HISTOGRAM,
                InstrumentValueType.DOUBLE
            )
        val simple = MetricDescriptor.create(view, instrument)
        simple.name shouldBe "new_name"
        simple.description shouldBe "new_description"
        simple.unit shouldBe "unit"
        simple.sourceInstrument shouldBe instrument
        simple.sourceView shouldBe view
    }
}
