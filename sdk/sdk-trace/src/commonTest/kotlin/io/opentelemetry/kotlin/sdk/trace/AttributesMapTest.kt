/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.kotest.matchers.maps.shouldContainExactly
import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.longKey
import kotlin.test.Test

class AttributesMapTest {
    @Test
    fun asMap() {
        val attributesMap = AttributesMap(2, Int.MAX_VALUE)
        attributesMap.put(longKey("one"), 1L)
        attributesMap.put(longKey("two"), 2L)
        attributesMap.asMap() shouldContainExactly mapOf(longKey("one") to 1L, longKey("two") to 2L)
    }
}
