/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.common

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.booleanArrayKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.booleanKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.doubleArrayKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.doubleKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.longArrayKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.longKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.stringArrayKey
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.stringKey
import io.opentelemetry.kotlin.api.entryTo
import kotlin.test.Test

/** Unit tests for [Attributes]s. */
class AttributesTest {
    @Test
    fun forEach() {
        val entriesSeen: MutableMap<AttributeKey<*>, Any> = LinkedHashMap()
        val attributes = Attributes.of(stringKey("key1"), "value1", longKey("key2"), 333L)
        attributes.forEach { key, value -> entriesSeen[key] = value }
        entriesSeen shouldContainExactly
            mapOf(stringKey("key1") to "value1", longKey("key2") to 333L)
    }

    @Test
    fun forEach_singleAttribute() {
        val entriesSeen: MutableMap<AttributeKey<*>, Any> = HashMap()
        val attributes = Attributes.of(stringKey("key"), "value")
        attributes.forEach { key, value -> entriesSeen[key] = value }
        entriesSeen shouldContainExactly mapOf(stringKey("key") to "value")
    }

    @Test
    fun putAll() {
        val attributes = Attributes.of(stringKey("key1"), "value1", longKey("key2"), 333L)
        Attributes.builder().put(booleanKey("key3"), true).putAll(attributes).build() shouldBe
            Attributes.of(
                stringKey("key1"),
                "value1",
                longKey("key2"),
                333L,
                booleanKey("key3"),
                true
            )
    }

    @Test
    fun asMap() {
        val attributes = Attributes.of(stringKey("key1"), "value1", longKey("key2"), 333L)
        val map = attributes.asMap()
        map shouldContainExactly mapOf(stringKey("key1") to "value1", longKey("key2") to 333L)
        map[stringKey("key1")] shouldBe "value1"
        map[longKey("key2")] shouldBe 333L
        map.keys shouldContainExactlyInAnyOrder listOf(stringKey("key1"), longKey("key2"))
        map.values shouldContainExactlyInAnyOrder listOf("value1", 333L)

        map.entries.contains(stringKey("key1") entryTo "value1").shouldBeTrue()
        map.entries.contains(stringKey("key1") entryTo "value2").shouldBeFalse()
        map.isEmpty().shouldBeFalse()
        map.containsKey(stringKey("key1")).shouldBeTrue()
        map.containsKey(longKey("key2")).shouldBeTrue()
        map.containsKey(stringKey("key3")).shouldBeFalse()
        map.containsValue("value1").shouldBeTrue()
        map.containsValue(333L).shouldBeTrue()
        map.containsValue("cat").shouldBeFalse()
        map.keys.contains(stringKey("key1")).shouldBeTrue()
        map.keys.contains(stringKey("key3")).shouldBeFalse()
        map.keys.containsAll(listOf(stringKey("key1"), longKey("key2"))).shouldBeTrue()
        map.keys.containsAll(listOf(stringKey("key1"), longKey("key3"))).shouldBeFalse()
        map.keys.containsAll(emptyList<Any>()).shouldBeTrue()
        map.keys.size shouldBe 2
        map.keys.toTypedArray() shouldContainExactlyInAnyOrder
            arrayOf(stringKey("key1"), longKey("key2"))
        val keys = map.keys.toTypedArray()
        keys shouldContainExactlyInAnyOrder arrayOf(stringKey("key1"), longKey("key2"))
        keys.isEmpty() // Didn't use input array.
        map.keys.containsAll(listOf(stringKey("key1"))).shouldBeTrue()
        map.keys.containsAll(listOf(stringKey("key1"), stringKey("key3"))).shouldBeFalse()
        map.keys.isEmpty().shouldBeFalse()
        map.values.contains("value1").shouldBeTrue()
        map.values.contains("value3").shouldBeFalse()
        map.toString() shouldBe "ReadOnlyArrayMap{key1=value1,key2=333}"
        val emptyMap = Attributes.builder().build().asMap()
        emptyMap.isEmpty().shouldBeTrue()
        shouldThrow<NoSuchElementException> { emptyMap.entries.iterator().next() }
    }

    @Test
    fun forEach_empty() {
        var sawSomething = false
        val emptyAttributes = Attributes.empty()
        emptyAttributes.forEach { _, _ -> sawSomething = true }
        sawSomething.shouldBeFalse()
    }

    @Test
    fun orderIndependentEquality() {
        val one = Attributes.of(stringKey("key1"), "value1", stringKey("key2"), "value2")
        val two = Attributes.of(stringKey("key2"), "value2", stringKey("key1"), "value1")
        one shouldBe two
        val three =
            Attributes.of(
                stringKey("key1"),
                "value1",
                stringKey("key2"),
                "value2",
                stringKey(""),
                "empty",
                stringKey("key3"),
                "value3",
                stringKey("key4"),
                "value4"
            )
        val four =
            Attributes.of(
                stringKey("key2"),
                "value2",
                stringKey("key1"),
                "value1",
                stringKey("key4"),
                "value4",
                stringKey("key3"),
                "value3"
            )
        three shouldBe four
    }

    @Test
    fun deduplication() {
        val one = Attributes.of(stringKey("key1"), "valueX", stringKey("key1"), "value1")
        val two = Attributes.of(stringKey("key1"), "value1")
        one shouldBe two
    }

    @Test
    fun deduplication_oddNumberElements() {
        val one =
            Attributes.builder()
                .put(stringKey("key2"), "valueX")
                .put(stringKey("key2"), "value2")
                .put(stringKey("key1"), "value1")
                .build()
        val two =
            Attributes.builder()
                .put(stringKey("key2"), "value2")
                .put(stringKey("key1"), "value1")
                .build()
        one shouldBe two
    }

    @Test
    fun emptyKey() {
        Attributes.of(stringKey(""), "empty") shouldBe Attributes.empty()

        Attributes.of(stringKey("one"), "one", stringKey(""), "null") shouldBe
            Attributes.of(stringKey("one"), "one")
    }

    @Test
    fun builder() {
        val attributes =
            Attributes.builder()
                .put("string", "value1")
                .put("long", 100)
                .put(longKey("long2"), 10)
                .put("double", 33.44)
                .put("boolean", "duplicateShouldBeRemoved")
                .put("boolean", false)
                .build()
        val wantAttributes =
            Attributes.of(
                stringKey("string"),
                "value1",
                longKey("long"),
                100L,
                longKey("long2"),
                10L,
                doubleKey("double"),
                33.44,
                booleanKey("boolean"),
                false
            )
        attributes shouldBe wantAttributes
        val newAttributes = attributes.toBuilder()
        newAttributes.put("newKey", "newValue")
        newAttributes.build() shouldBe
            Attributes.of(
                stringKey("string"),
                "value1",
                longKey("long"),
                100L,
                longKey("long2"),
                10L,
                doubleKey("double"),
                33.44,
                booleanKey("boolean"),
                false,
                stringKey("newKey"),
                "newValue"
            )
        // Original not mutated.
        attributes shouldBe wantAttributes
    }

    @Test
    fun builder_arrayTypes() {
        val attributes =
            Attributes.builder()
                .put("string", "value1", "value2")
                .put("long", 100L, 200L)
                .put("double", 33.44, -44.33)
                .put("boolean", "duplicateShouldBeRemoved")
                .put(stringKey("boolean"), "true")
                .put("boolean", false, true)
                .build()
        attributes shouldBe
            Attributes.of(
                stringArrayKey("string"),
                listOf("value1", "value2"),
                longArrayKey("long"),
                listOf(100L, 200L),
                doubleArrayKey("double"),
                listOf(33.44, -44.33),
                booleanArrayKey("boolean"),
                listOf(false, true)
            )
    }

    @Test
    fun get_Null() {
        Attributes.empty()[stringKey("foo")].shouldBeNull()
        Attributes.of(stringKey("key"), "value")[stringKey("foo")].shouldBeNull()
    }

    @Test
    fun get() {
        Attributes.of(stringKey("key"), "value")[stringKey("key")] shouldBe "value"
        Attributes.of(stringKey("key"), "value")[booleanKey("value")].shouldBeNull()
        val threeElements =
            Attributes.of(
                stringKey("string"),
                "value",
                booleanKey("boolean"),
                true,
                longKey("long"),
                1L
            )
        threeElements[booleanKey("boolean")] shouldBe true
        threeElements[stringKey("string")] shouldBe "value"
        threeElements[longKey("long")] shouldBe 1L
        val twoElements = Attributes.of(stringKey("string"), "value", booleanKey("boolean"), true)
        twoElements[booleanKey("boolean")] shouldBe true
        twoElements[stringKey("string")] shouldBe "value"
        val fourElements =
            Attributes.of(
                stringKey("string"),
                "value",
                booleanKey("boolean"),
                true,
                longKey("long"),
                1L,
                stringArrayKey("array"),
                listOf("one", "two", "three")
            )
        fourElements[stringArrayKey("array")] shouldBe listOf("one", "two", "three")
        threeElements[booleanKey("boolean")] shouldBe true
        threeElements[stringKey("string")] shouldBe "value"
        threeElements[longKey("long")] shouldBe 1L
    }

    @Test
    fun toBuilder() {
        val filled = Attributes.builder().put("cat", "meow").put("dog", "bark").build()
        val fromEmpty = Attributes.empty().toBuilder().put("cat", "meow").put("dog", "bark").build()
        fromEmpty shouldBe filled
        // Original not mutated.
        Attributes.empty().isEmpty().shouldBeTrue()
        val partial = Attributes.builder().put("cat", "meow").build()
        val fromPartial = partial.toBuilder().put("dog", "bark").build()
        fromPartial shouldBe filled
        // Original not mutated.
        partial shouldBe Attributes.builder().put("cat", "meow").build()
    }

    @Test
    fun nullsAreNoOps() {
        val builder = Attributes.builder()
        builder.put(stringKey("attrValue"), "attrValue")
        builder.put("string", "string")
        builder.put("long", 10)
        builder.put("double", 1.0)
        builder.put("bool", true)
        builder.put("arrayString", *arrayOf("string"))
        builder.put("arrayLong", *longArrayOf(10L))
        builder.put("arrayDouble", *doubleArrayOf(1.0))
        builder.put("arrayBool", *booleanArrayOf(true))
        builder.build().size shouldBe 9
        val attributes = builder.build()
        attributes.size shouldBe 9
        attributes[stringKey("string")] shouldBe "string"
        attributes[stringArrayKey("arrayString")] shouldBe listOf("string")
        attributes[longArrayKey("arrayLong")] shouldBe listOf(10L)
        attributes[doubleArrayKey("arrayDouble")] shouldBe listOf(1.0)
        attributes[booleanArrayKey("arrayBool")] shouldBe listOf(true)
    }

    @Test
    fun attributesToString() {
        val attributes =
            Attributes.builder()
                .put("otel.status_code", "OK")
                .put("http.response_size", 100)
                .put("process.cpu_consumed", 33.44)
                .put("error", true)
                .put("success", "true")
                .build()
        attributes.toString() shouldBe
            "{error=true, http.response_size=100, ".plus(
                "otel.status_code=\"OK\", process.cpu_consumed=33.44, success=\"true\"}"
            )
    }

    @Test
    fun onlySameTypeCanRetrieveValue() {
        val attributes = Attributes.of(stringKey("animal"), "cat")
        attributes[stringKey("animal")] shouldBe "cat"
        attributes[longKey("animal")].shouldBeNull()
    }

    @Test
    fun remove() {
        val builder = Attributes.builder()
        builder.remove(stringKey("")) shouldBe builder
        var attributes = Attributes.builder().remove(stringKey("key1")).build()
        attributes shouldBe Attributes.builder().build()
        attributes =
            Attributes.builder()
                .put("key1", "value1")
                .build()
                .toBuilder()
                .remove(stringKey("key1"))
                .remove(stringKey("key1"))
                .build()
        attributes shouldBe Attributes.builder().build()
        attributes =
            Attributes.builder()
                .put("key1", "value1")
                .put("key1", "value2")
                .put("key2", "value2")
                .put("key3", "value3")
                .remove(stringKey("key1"))
                .build()
        attributes shouldBe Attributes.builder().put("key2", "value2").put("key3", "value3").build()
        attributes =
            Attributes.builder()
                .put("key1", "value1")
                .put("key1", true)
                .remove(stringKey("key1"))
                .remove(stringKey("key1"))
                .build()
        attributes shouldBe Attributes.builder().put("key1", true).build()
    }

    @Test
    fun removeIf() {
        val builder = Attributes.builder()
        builder.removeIf { _ -> true } shouldBe builder

        var attributes = Attributes.builder().removeIf { key -> key.key == "key1" }.build()
        attributes shouldBe Attributes.builder().build()
        attributes =
            Attributes.builder()
                .put("key1", "value1")
                .build()
                .toBuilder()
                .removeIf { key -> key.key.equals("key1") }
                .removeIf { key -> key.key.equals("key1") }
                .build()
        attributes shouldBe Attributes.builder().build()
        attributes =
            Attributes.builder()
                .put("key1", "value1")
                .put("key1", "value2")
                .put("key2", "value2")
                .put("key3", "value3")
                .removeIf { key -> key.key.equals("key1") }
                .build()
        attributes shouldBe Attributes.builder().put("key2", "value2").put("key3", "value3").build()
        attributes =
            Attributes.builder()
                .put("key1", "value1A")
                .put("key1", true)
                .removeIf { key -> key.key.equals("key1") && key.type.equals(AttributeType.STRING) }
                .build()
        attributes shouldBe Attributes.builder().put("key1", true).build()
        attributes =
            Attributes.builder()
                .put("key1", "value1")
                .put("key2", "value2")
                .put("foo", "bar")
                .removeIf { key -> key.key.matches("key.*".toRegex()) }
                .build()
        attributes shouldBe Attributes.builder().put("foo", "bar").build()
    }
}
