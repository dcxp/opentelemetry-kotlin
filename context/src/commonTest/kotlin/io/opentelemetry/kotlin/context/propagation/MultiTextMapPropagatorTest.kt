/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.context.propagation

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.ints.shouldNotBeExactly
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.context.ContextKey
import kotlin.test.Test

internal class MultiTextMapPropagatorTest {

    @Test
    fun fields() {
        val propagator1 = PropagatorFactory.createTextMapPropagatorWithFields("foo", "bar")
        val propagator2 = PropagatorFactory.createTextMapPropagatorWithFields("hello", "world")
        val prop: TextMapPropagator = MultiTextMapPropagator(propagator1, propagator2)
        val fields = prop.fields()
        fields shouldContainExactlyInAnyOrder listOf("foo", "bar", "hello", "world")
    }

    @Test
    fun fields_duplicates() {
        val propagator1 = PropagatorFactory.createTextMapPropagatorWithFields("foo", "bar", "foo")
        val propagator2 =
            PropagatorFactory.createTextMapPropagatorWithFields("hello", "world", "world", "bar")
        val prop: TextMapPropagator = MultiTextMapPropagator(propagator1, propagator2)
        val fields = prop.fields()
        fields shouldContainExactlyInAnyOrder listOf("foo", "bar", "hello", "world")
    }

    @Test
    fun inject_allDelegated() {
        val carrier: MutableMap<String, String> = HashMap()
        val context = Context.root()
        val setter: (MutableMap<String, String>, String, String?) -> Unit =
                { mutableMap: MutableMap<String, String>, s: String, s1: String? ->
            if (s1 == null) {
                mutableMap.remove(s)
            } else {
                mutableMap.put(s, s1)
            }
        }
        val propagator1 = PropagatorFactory.createObservableTextMapPropagator()
        val propagator2 = PropagatorFactory.createObservableTextMapPropagator()
        val propagator3 = PropagatorFactory.createObservableTextMapPropagator()

        val prop: TextMapPropagator = MultiTextMapPropagator(propagator1, propagator2, propagator3)
        prop.inject(context, carrier, setter)
        propagator1.injectCount shouldNotBeExactly 0
        propagator2.injectCount shouldNotBeExactly 0
        propagator3.injectCount shouldNotBeExactly 0
    }

    @Test
    fun extract_noPropagators() {
        val carrier: Map<String, String> = HashMap()
        val context = Context.root()
        val prop: TextMapPropagator = MultiTextMapPropagator()
        val resContext = prop.extract(context, carrier, getter)
        resContext shouldBe context
    }
    /*
        @Test
        fun extract_found_all() {
            val carrier: Map<String, String> = HashMap()
            val prop: TextMapPropagator = MultiTextMapPropagator(propagator1, propagator2, propagator3)
            val context1: Context = mock(Context::class.java)
            val context2: Context = mock(Context::class.java)
            val context3: Context = mock(Context::class.java)
            val expectedContext: Context = mock(Context::class.java)
            `when`(propagator1!!.extract(context1, carrier, getter)).thenReturn(context2)
            `when`(propagator2!!.extract(context2, carrier, getter)).thenReturn(context3)
            `when`(propagator3!!.extract(context3, carrier, getter)).thenReturn(expectedContext)
            assertThat(prop.extract(context1, carrier, getter)).isEqualTo(expectedContext)
        }
    */
    @Test
    fun extract_notFound() {
        val carrier: MutableMap<String, String> = HashMap()
        val context = Context.root()
        val propagator1 = PropagatorFactory.createDummyTextMapPropagator()
        val propagator2 = PropagatorFactory.createDummyTextMapPropagator()
        val prop: TextMapPropagator = MultiTextMapPropagator(propagator1, propagator2)
        val result = prop.extract(context, carrier, getter)
        result shouldBe context
    }

    @Test
    fun extract_nullContext() {
        MultiTextMapPropagator(
                PropagatorFactory.createDummyTextMapPropagator(),
                PropagatorFactory.createDummyTextMapPropagator()
            )
            .extract(emptyMap(), getter) shouldBe Context.root()
    }

    @Test
    fun inject_nullContext() {
        val carrier: MutableMap<String, String> = LinkedHashMap()
        MultiTextMapPropagator(
                PropagatorFactory.createDummyTextMapPropagator(),
                PropagatorFactory.createDummyTextMapPropagator()
            )
            .inject(carrier) { obj, key, value ->
                if (value == null) {
                    obj.remove(key)
                } else {
                    obj[key] = value
                }
            }
        carrier.shouldBeEmpty()
    }

    companion object {
        private val KEY: ContextKey<String> = ContextKey.named("key")
        private val getter: TextMapGetter<Map<String, String>> =
            object : TextMapGetter<Map<String, String>> {

                override fun keys(carrier: Map<String, String>): Iterable<String?> {
                    return carrier.keys
                }

                override fun get(carrier: Map<String, String>, key: String): String? {
                    return carrier[key]
                }
            }
    }
}
