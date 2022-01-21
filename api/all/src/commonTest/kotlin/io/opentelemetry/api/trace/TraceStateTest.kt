/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.api.trace

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.opentelemetry.api.EqualsTester
import kotlin.test.Test

class TraceStateTest {
    private val FIRST_KEY: String
        get() {
            return "key_1"
        }
    private val SECOND_KEY: String
        get() {
            return "key_2"
        }
    private val FIRST_VALUE: String
        get() {
            return "value.1"
        }
    private val SECOND_VALUE: String
        get() {
            return "value.2"
        }

    private val firstTraceState: TraceState
        get() {
            return TraceState.builder().put(FIRST_KEY, FIRST_VALUE).build()
        }
    private val secondTraceState: TraceState
        get() {
            return TraceState.builder().put(SECOND_KEY, SECOND_VALUE).build()
        }
    private val multiValueTraceState: TraceState
        get() {
            return TraceState.builder()
                .put(FIRST_KEY, FIRST_VALUE)
                .put(SECOND_KEY, SECOND_VALUE)
                .build()
        }

    @Test
    fun get() {
        firstTraceState[FIRST_KEY] shouldBe FIRST_VALUE
        secondTraceState[SECOND_KEY] shouldBe SECOND_VALUE
        multiValueTraceState[FIRST_KEY] shouldBe FIRST_VALUE
        multiValueTraceState[SECOND_KEY] shouldBe SECOND_VALUE
        shouldThrowAny { firstTraceState["dog"] }
    }

    @Test
    fun sizeAndEmpty() {
        TraceState.default.size() shouldBeExactly 0
        TraceState.default.isEmpty.shouldBeTrue()

        firstTraceState.size() shouldBeExactly 1
        firstTraceState.isEmpty.shouldBeFalse()

        multiValueTraceState.size() shouldBeExactly 2
        multiValueTraceState.isEmpty.shouldBeFalse()
    }

    @Test
    fun forEach() {
        val entries: MutableMap<String, String> = mutableMapOf()
        firstTraceState.forEach(entries::put)
        entries shouldContainExactly mapOf(FIRST_KEY to FIRST_VALUE)

        entries.clear()
        secondTraceState.forEach(entries::put)
        entries shouldContainExactly mapOf(SECOND_KEY to SECOND_VALUE)

        entries.clear()
        multiValueTraceState.forEach(entries::put)
        // Reverse order of input.
        entries shouldContainExactly mapOf(SECOND_KEY to SECOND_VALUE, FIRST_KEY to FIRST_VALUE)
    }

    @Test
    fun asMap() {
        firstTraceState.asMap() shouldContainExactly mapOf(FIRST_KEY to FIRST_VALUE)

        secondTraceState.asMap() shouldContainExactly mapOf(SECOND_KEY to SECOND_VALUE)

        // Reverse order of input.
        multiValueTraceState.asMap() shouldContainExactly
            mapOf(SECOND_KEY to SECOND_VALUE, FIRST_KEY to FIRST_VALUE)
    }

    @Test
    fun disallowsEmptyKey() {
        TraceState.builder().put("", FIRST_VALUE).build() shouldBe TraceState.default
    }

    @Test
    fun invalidFirstKeyCharacter() {
        TraceState.builder().put("${'$'}_key", FIRST_VALUE).build() shouldBe TraceState.default
    }

    @Test
    fun firstKeyCharacterDigitIsAllowed() {
        // note: a digit is only allowed if the key is in the tenant format (with an '@')
        val result = TraceState.builder().put("1@tenant", FIRST_VALUE).build()
        result["1@tenant"] shouldBe FIRST_VALUE
    }

    @Test
    fun testValidLongTenantId() {
        val result = TraceState.builder().put("12345678901234567890@nr", FIRST_VALUE).build()
        result["12345678901234567890@nr"] shouldBe FIRST_VALUE
    }

    @Test
    fun invalidKeyCharacters() {
        TraceState.builder().put("kEy_1", FIRST_VALUE).build() shouldBe TraceState.default
    }

    @Test
    fun testValidAtSignVendorNamePrefix() {
        val result = TraceState.builder().put("1@nr", FIRST_VALUE).build()
        result["1@nr"] shouldBe FIRST_VALUE
    }

    @Test
    fun testVendorIdLongerThan13Characters() {
        TraceState.builder().put("1@nrabcdefghijkl", FIRST_VALUE).build() shouldBe
            TraceState.default
    }

    @Test
    fun testVendorIdLongerThan13Characters_longTenantId() {
        TraceState.builder()
            .put("12345678901234567890@nrabcdefghijkl", FIRST_VALUE)
            .build() shouldBe TraceState.default
    }

    @Test
    fun tenantIdLongerThan240Characters() {
        val tenantId = (0..240).map { 'a' }.toCharArray().concatToString()
        TraceState.builder().put("$tenantId@nrabcdefghijkl", FIRST_VALUE).build() shouldBe
            TraceState.default
    }

    @Test
    fun testNonVendorFormatFirstKeyCharacter() {
        TraceState.builder().put("1acdfrgs", FIRST_VALUE).build() shouldBe TraceState.default
    }

    @Test
    fun testMultipleAtSignNotAllowed() {
        TraceState.builder().put("1@n@r@", FIRST_VALUE).build() shouldBe TraceState.default
    }

    @Test
    fun invalidKeySize() {
        val longKey = (0..257).map { 'a' }.toCharArray().concatToString()
        TraceState.builder().put(longKey, FIRST_VALUE).build() shouldBe TraceState.default
    }

    @Test
    fun allAllowedKeyCharacters() {
        val allowedKey =
            listOf(('a'..'z'), ('0'..'1'), listOf('_', '-', '*', '/'))
                .flatten()
                .toCharArray()
                .concatToString()

        val result = TraceState.builder().put(allowedKey, FIRST_VALUE).build()
        result[allowedKey] shouldBe FIRST_VALUE
    }

    @Test
    fun invalidValueSize() {
        val longValue = (0..257).map { 'a' }.toCharArray().concatToString()
        TraceState.builder().put(FIRST_KEY, longValue).build() shouldBe TraceState.default
    }

    @Test
    fun allAllowedValueCharacters() {
        val allowedValue =
            (' '..'~').filter { c -> c != ',' && c != '=' }.toCharArray().concatToString()
        val result = TraceState.builder().put(FIRST_KEY, allowedValue).build()
        result[FIRST_KEY] shouldBe allowedValue
    }

    @Test
    fun invalidValues() {
        TraceState.builder().put("foo", "bar,").build() shouldBe TraceState.default
        TraceState.builder().put("foo", "bar ").build() shouldBe TraceState.default
        TraceState.builder().put("foo", "bar=").build() shouldBe TraceState.default
        TraceState.builder().put("foo", "bar\u0019").build() shouldBe TraceState.default
        TraceState.builder().put("foo", "bar\u007F").build() shouldBe TraceState.default
    }

    @Test
    fun addEntry() {
        firstTraceState.toBuilder().put(SECOND_KEY, SECOND_VALUE).build() shouldBe
            multiValueTraceState
    }

    @Test
    fun updateEntry() {
        firstTraceState.toBuilder().put(FIRST_KEY, SECOND_VALUE).build().get(FIRST_KEY) shouldBe
            SECOND_VALUE
        val updatedMultiValueTraceState =
            multiValueTraceState.toBuilder().put(FIRST_KEY, SECOND_VALUE).build()

        updatedMultiValueTraceState[FIRST_KEY] shouldBe SECOND_VALUE
        updatedMultiValueTraceState[SECOND_KEY] shouldBe SECOND_VALUE
    }

    @Test
    fun addAndUpdateEntry() {
        val state =
            firstTraceState
                .toBuilder()
                .put(FIRST_KEY, SECOND_VALUE) // update the existing entry
                .put(SECOND_KEY, FIRST_VALUE) // add a new entry
                .build() as
                ArrayBasedTraceState
        state.entries shouldContainInOrder listOf(SECOND_KEY, FIRST_VALUE, FIRST_KEY, SECOND_VALUE)
    }

    @Test
    fun addSameKey() {
        val state =
            TraceState.builder()
                .put(FIRST_KEY, SECOND_VALUE) // update the existing entry
                .put(FIRST_KEY, FIRST_VALUE) // add a new entry
                .build() as
                ArrayBasedTraceState
        state.entries shouldContainInOrder listOf(FIRST_KEY, FIRST_VALUE)
    }

    @Test
    fun remove() {
        multiValueTraceState.toBuilder().remove(SECOND_KEY).build() shouldBe firstTraceState
    }

    @Test
    fun addAndRemoveEntry() {
        TraceState.builder()
            .put(FIRST_KEY, SECOND_VALUE) // update the existing entry
            .remove(FIRST_KEY) // add a new entry
            .build() shouldBe TraceState.default
    }

    @Test
    fun traceState_EqualsAndHashCode() {
        val tester = EqualsTester()
        tester.addEqualityGroup(
            TraceState.default,
            TraceState.default,
            TraceState.default.toBuilder().build(),
            TraceState.builder().build()
        )
        tester.addEqualityGroup(
            firstTraceState,
            TraceState.builder().put(FIRST_KEY, FIRST_VALUE).build()
        )
        tester.addEqualityGroup(
            secondTraceState,
            TraceState.builder().put(SECOND_KEY, SECOND_VALUE).build()
        )
        shouldNotThrowAny { tester.testEquals() }
    }

    @Test
    fun traceState_ToString() {
        TraceState.default.toString() shouldBe "ArrayBasedTraceState(entries=[])"
    }
}
