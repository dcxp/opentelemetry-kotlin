/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.context.propagation

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.context.ContextKey
import kotlin.test.Test

/** Unit tests for [DefaultContextPropagators]. */
internal class DefaultPropagatorsTest {

    @Test
    fun testInject() {
        val propagator1 = CustomTextMapPropagator("prop1")
        val propagator2 = CustomTextMapPropagator("prop2")
        val propagators =
            ContextPropagators.create(TextMapPropagator.composite(propagator1, propagator2))
        var context = Context.current()
        context = context.with(propagator1.key, "value1")
        context = context.with(propagator2.key, "value2")
        val map: MutableMap<String, String> = mutableMapOf()
        propagators.textMapPropagator.inject(context, map, MapSetter.INSTANCE)
        map[propagator1.keyName] shouldBe "value1"
        map[propagator2.keyName] shouldBe "value2"
    }

    @Test
    fun testExtract() {
        val propagator1 = CustomTextMapPropagator("prop1")
        val propagator2 = CustomTextMapPropagator("prop2")
        val propagator3 = CustomTextMapPropagator("prop3")
        val propagators =
            ContextPropagators.create(TextMapPropagator.composite(propagator1, propagator2))

        // Put values for propagators 1 and 2 only.
        val map: MutableMap<String, String> = mutableMapOf()
        map[propagator1.keyName] = "value1"
        map[propagator2.keyName] = "value2"
        val context =
            propagators.textMapPropagator.extract(Context.current(), map, MapGetter.INSTANCE)
        context[propagator1.key] shouldBe "value1"
        context[propagator2.key] shouldBe "value2"
        context[propagator3.key].shouldBeNull() // Handle missing value.
    }

    @Test
    fun testDuplicatedFields() {
        val propagator1 = CustomTextMapPropagator("prop1")
        val propagator2 = CustomTextMapPropagator("prop2")
        val propagator3 = CustomTextMapPropagator("prop1")
        val propagator4 = CustomTextMapPropagator("prop2")
        val propagators =
            ContextPropagators.create(
                TextMapPropagator.composite(propagator1, propagator2, propagator3, propagator4)
            )
        val fields = propagators.textMapPropagator.fields()
        fields shouldContainExactlyInAnyOrder listOf("prop1", "prop2")
    }

    @Test
    fun noopPropagator() {
        val propagators = ContextPropagators.noop()
        val context = Context.current()
        val map: MutableMap<String, String> = mutableMapOf()
        propagators.textMapPropagator.inject(context, map, MapSetter.INSTANCE)
        propagators.textMapPropagator.extract(context, map, MapGetter.INSTANCE) shouldBe context
    }

    private class CustomTextMapPropagator(val keyName: String) : TextMapPropagator {
        val key: ContextKey<String> = ContextKey.named(keyName)

        override fun fields(): Collection<String> {
            return listOf(keyName)
        }

        override fun <C> inject(context: Context, carrier: C, setter: TextMapSetter<C>) {
            context.tryGet(key) { setter[carrier, keyName] = it }
        }

        override fun <C> extract(context: Context, carrier: C, getter: TextMapGetter<C>): Context {
            var newcontext = context
            getter.tryGet(carrier, keyName) { newcontext = context.with(key, it) }
            return newcontext
        }
    }

    private class MapSetter private constructor() : TextMapSetter<MutableMap<String, String>> {
        override operator fun set(
            carrier: MutableMap<String, String>,
            key: String,
            value: String?
        ) {
            if (value == null) {
                carrier.remove(key)
            } else {
                carrier[key] = value
            }
        }

        companion object {
            val INSTANCE = MapSetter()
        }
    }

    private class MapGetter private constructor() : TextMapGetter<Map<String, String>> {
        override fun keys(carrier: Map<String, String>): Iterable<String> {
            return carrier.keys
        }

        override operator fun get(carrier: Map<String, String>, key: String): String? {
            return carrier[key]
        }

        companion object {
            val INSTANCE = MapGetter()
        }
    }
}
