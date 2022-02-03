/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.metrics.internal.export.CollectionHandle
import kotlin.test.Test

internal class DeltaAccumulationTest {
    private val handle1: CollectionHandle
    private val handle2: CollectionHandle
    private val all: Set<CollectionHandle>

    init {
        val supplier = CollectionHandle.createSupplier()
        handle1 = supplier.get()
        handle2 = supplier.get()
        all = setOf(handle1, handle2)
    }

    @Test
    fun wasReadBy_works() {
        val measurement: MutableMap<Attributes, Long> = HashMap()
        measurement[Attributes.empty()] = 1L
        val accumulation = DeltaAccumulation(measurement)
        accumulation.wasReadBy(handle1).shouldBeFalse()
        accumulation.wasReadBy(handle2).shouldBeFalse()
        accumulation.wasReadByAll(all).shouldBeFalse()

        // Read and check.
        accumulation.read(handle1) shouldBe measurement
        accumulation.wasReadBy(handle1).shouldBeTrue()
        accumulation.wasReadBy(handle2).shouldBeFalse()
        accumulation.wasReadByAll(all).shouldBeFalse()

        // Read and check.
        accumulation.read(handle2) shouldBe measurement
        accumulation.wasReadBy(handle1).shouldBeTrue()
        accumulation.wasReadBy(handle2).shouldBeTrue()
        accumulation.wasReadByAll(all).shouldBeTrue()
    }
}
