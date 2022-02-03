/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.export

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.test.Test

class TestCollectionHandle {
    @Test
    fun created_haveUniqueIdentity() {
        val supplier = CollectionHandle.createSupplier()
        val one: CollectionHandle = supplier.get()
        val two: CollectionHandle = supplier.get()
        one shouldBe one
        one shouldNotBe two
    }

    @Test
    fun mutableSet_allowsAddAndContains() {
        val supplier = CollectionHandle.createSupplier()
        val mutable = CollectionHandle.mutableSet()
        val one: CollectionHandle = supplier.get()
        mutable.shouldHaveSize(0)
        mutable.contains(one).shouldBeFalse()
        mutable.add(one)
        mutable.shouldHaveSize(1)
        mutable.contains(one).shouldBeTrue()
        val two: CollectionHandle = supplier.get()
        mutable.contains(two).shouldBeFalse()
        mutable.add(two)
        mutable.shouldHaveSize(2)
        mutable.contains(two).shouldBeTrue()
    }

    @Test
    fun mutableSet_allowsContainsAll() {
        val supplier = CollectionHandle.createSupplier()
        val one: CollectionHandle = supplier.get()
        val two: CollectionHandle = supplier.get()
        val three: CollectionHandle = supplier.get()
        val mutable = CollectionHandle.mutableSet()
        mutable.add(one)
        mutable.add(two)
        val mutableCopy = CollectionHandle.of(one, two)
        val mutablePlus = CollectionHandle.of(one, two, three)
        mutable.containsAll(mutableCopy).shouldBeTrue()
        mutable.containsAll(mutablePlus).shouldBeFalse()
        mutablePlus.containsAll(mutable).shouldBeTrue()
    }

    @Test
    fun mutableSet_iteratingWorks() {
        val supplier = CollectionHandle.createSupplier()
        val one: CollectionHandle = supplier.get()
        val two: CollectionHandle = supplier.get()
        val three: CollectionHandle = supplier.get()
        val set = CollectionHandle.of(one, two, three)
        set.shouldHaveSize(3)
        val iterator = set.iterator()
        iterator.hasNext().shouldBeTrue()
        iterator.next() shouldBe one
        iterator.hasNext().shouldBeTrue()
        iterator.next() shouldBe two
        iterator.hasNext().shouldBeTrue()
        iterator.next() shouldBe three
        iterator.hasNext().shouldBeFalse()
        // TODO: Verify next throws.
    }
}
