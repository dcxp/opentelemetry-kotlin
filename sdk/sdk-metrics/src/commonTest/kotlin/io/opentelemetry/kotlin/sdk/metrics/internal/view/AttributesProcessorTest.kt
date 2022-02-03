/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.view

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldHaveSize
import io.opentelemetry.kotlin.api.baggage.Baggage
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.stringKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import kotlin.test.Test

/** Tests for the [AttributesProcessors] DSL-ish library. */
class AttributesProcessorTest {
    @Test
    fun filterKeyName_removesKeys() {
        val processor = AttributesProcessor.filterByKeyName { name -> "test" == name }
        assertSoftly(
            processor
                .process(
                    Attributes.builder().put("remove", "me").put("test", "keep").build(),
                    Context.root()
                )
                .asMap()
        ) {
            shouldHaveSize(1)
            shouldContain(stringKey("test"), "keep")
        }
    }

    @Test
    fun append_works() {
        val processor = AttributesProcessor.append(Attributes.builder().put("append", "me").build())
        assertSoftly(processor.process(Attributes.empty(), Context.root()).asMap()) {
            shouldHaveSize(1)
            shouldContain(stringKey("append"), "me")
        }
    }

    @Test
    fun append_doesNotOverrideExistingKeys() {
        val processor = AttributesProcessor.append(Attributes.builder().put("test", "drop").build())

        assertSoftly(
            processor
                .process(Attributes.builder().put("test", "keep").build(), Context.root())
                .asMap()
        ) {
            shouldHaveSize(1)
            shouldContain(stringKey("test"), "keep")
        }
    }

    @Test
    fun appendBaggage_works() {
        val processor = AttributesProcessor.appendBaggageByKeyName { ignored -> true }
        val baggage = Baggage.builder().put("baggage", "value").build()
        val context = Context.root().with(baggage)
        assertSoftly(
            processor.process(Attributes.builder().put("test", "keep").build(), context).asMap()
        ) {
            shouldHaveSize(2)
            shouldContain(stringKey("test"), "keep")
            shouldContain(stringKey("baggage"), "value")
        }
    }

    @Test
    fun appendBaggage_doesNotOverrideExistingKeys() {
        val processor = AttributesProcessor.appendBaggageByKeyName { ignored -> true }
        val baggage = Baggage.builder().put("test", "drop").build()
        val context = Context.root().with(baggage)
        assertSoftly(
            processor.process(Attributes.builder().put("test", "keep").build(), context).asMap()
        ) {
            shouldHaveSize(1)
            shouldContain(stringKey("test"), "keep")
        }
    }

    @Test
    fun appendBaggageByKeyName_works() {
        val processor = AttributesProcessor.appendBaggageByKeyName { name -> "keep" == name }
        val baggage = Baggage.builder().put("baggage", "value").put("keep", "baggage").build()
        val context = Context.root().with(baggage)
        assertSoftly(
            processor.process(Attributes.builder().put("test", "keep").build(), context).asMap()
        ) {
            shouldHaveSize(2)
            shouldContain(stringKey("test"), "keep")
            shouldContain(stringKey("keep"), "baggage")
        }
    }

    @Test
    fun proccessors_joinByThen() {
        // Baggage should be added, then all keys filtered.
        val processor =
            AttributesProcessor.appendBaggageByKeyName { ignored -> true }
                .then(AttributesProcessor.filterByKeyName { name -> "baggage" == name })
        val baggage = Baggage.builder().put("baggage", "value").put("keep", "baggage").build()
        val context = Context.root().with(baggage)
        assertSoftly(
            processor.process(Attributes.builder().put("test", "keep").build(), context).asMap()
        ) {
            shouldHaveSize(1)
            shouldContain(stringKey("baggage"), "value")
        }
    }
}
