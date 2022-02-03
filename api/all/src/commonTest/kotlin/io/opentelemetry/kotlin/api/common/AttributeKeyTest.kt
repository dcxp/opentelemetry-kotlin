/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.common

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.opentelemetry.kotlin.api.EqualsTester
import kotlin.test.Test

internal class AttributeKeyTest {
    @Test
    fun equalsVerifier() {
        val tester = EqualsTester()
        tester.addEqualityGroup(AttributeKey.stringKey("test"), AttributeKey.stringKey("test"))
        tester.addEqualityGroup(AttributeKey.booleanKey("test"), AttributeKey.booleanKey("test"))
        tester.addEqualityGroup(AttributeKey.longKey("test"), AttributeKey.longKey("test"))
        tester.addEqualityGroup(AttributeKey.doubleKey("test"), AttributeKey.doubleKey("test"))
        tester.addEqualityGroup(
            AttributeKey.stringArrayKey("test"),
            AttributeKey.stringArrayKey("test")
        )
        tester.addEqualityGroup(
            AttributeKey.booleanArrayKey("test"),
            AttributeKey.booleanArrayKey("test")
        )
        tester.addEqualityGroup(
            AttributeKey.longArrayKey("test"),
            AttributeKey.longArrayKey("test")
        )
        tester.addEqualityGroup(
            AttributeKey.doubleArrayKey("test"),
            AttributeKey.doubleArrayKey("test")
        )
        shouldNotThrowAny { tester.testEquals() }
    }
}
