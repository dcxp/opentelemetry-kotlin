/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.context.propagation

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.opentelemetry.context.Context
import io.opentelemetry.context.ContextKey
import kotlin.test.Test

class NoopTextMapPropagatorTest {
    @Test
    fun noopFields() {
        TextMapPropagator.noop().fields().shouldBeEmpty()
    }

    @Test
    fun extract_contextUnchanged() {
        val input = Context.current()
        val result = TextMapPropagator.noop().extract(input, HashMap(), MapTextMapGetter.INSTANCE)
        result shouldBe input
    }

    @Test
    fun extract_nullContext() {
        TextMapPropagator.noop().extract(emptyMap(), MapTextMapGetter.INSTANCE) shouldBe
            Context.root()
    }

    @Test
    fun inject_nullContext() {
        val carrier: MutableMap<String, String> = mutableMapOf()
        TextMapPropagator.noop().inject(carrier) { obj, key, value ->
            if (value != null) {
                obj[key] = value
            } else {
                obj.remove(key)
            }
        }
        carrier.shouldBeEmpty()
    }

    internal enum class MapTextMapGetter : TextMapGetter<Map<out Any?, Any?>?> {
        INSTANCE;

        override fun keys(carrier: Map<out Any?, Any?>?): Iterable<String?> {
            return listOf()
        }

        override fun get(carrier: Map<out Any?, Any?>?, key: String): String? {
            return null
        }
    }

    companion object {
        private val KEY: ContextKey<String> = ContextKey.named("key")
    }
}
