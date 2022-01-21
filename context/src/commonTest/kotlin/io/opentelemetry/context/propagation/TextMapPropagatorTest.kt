/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.context.propagation

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class TextMapPropagatorTest {
    @Test
    fun emptyCompositeShouldBeNoop() {
        TextMapPropagator.composite() shouldBe TextMapPropagator.noop()
    }

    @Test
    fun singleCompositeShouldNotBeWrapped() {
        val propagator: TextMapPropagator = PropagatorFactory.createDummyTextMapPropagator()
        TextMapPropagator.composite(propagator) shouldBe propagator
    }

    @Test
    fun multiCompositeShouldNotBeWrapped() {
        val propagator1: TextMapPropagator = PropagatorFactory.createDummyTextMapPropagator()
        val propagator2: TextMapPropagator = PropagatorFactory.createDummyTextMapPropagator()
        val result = TextMapPropagator.composite(propagator1, propagator2)
        result shouldNotBe propagator1
        result shouldNotBe propagator2
    }
}
