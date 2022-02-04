/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.export

import io.opentelemetry.kotlin.Supplier
import kotlinx.atomicfu.atomic

/**
 * A handle for a collection-pipeline of metrics.
 *
 * This class provides an efficient means of leasing and tracking exporters.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
class CollectionHandle
private constructor(
    /** The index of this handle. */
    private val index: Int
) {
    override fun hashCode(): Int {
        return index
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null) {
            return false
        }
        return if (other !is CollectionHandle) {
            false
        } else index == other.index
    }

    override fun toString(): String {
        return "CollectionHandle($index)"
    }

    /** An optimised bitset version of `Set<CollectionHandle>`. */
    private class CollectionHandleSet : MutableSet<CollectionHandle> by mutableSetOf() {
        /*private val storage = mutableSet<CollectionHandle>()
        override operator fun iterator(): MutableIterator<CollectionHandle> {
            return MyIterator()
        }

        override fun add(handle: CollectionHandle): Boolean {
            if (storage.get(handle.index)) {
                return false
            }
            storage.set(handle.index)
            return true
        }

        override operator fun contains(handle: Any): Boolean {
            return if (handle is CollectionHandle) {
                storage.get(handle.index)
            } else false
        }

        override fun containsAll(other: Collection<*>?): Boolean {
            if (other is CollectionHandleSet) {
                val result: java.util.BitSet = storage.clone() as java.util.BitSet
                val otherStorage: java.util.BitSet = (other as CollectionHandleSet).storage
                result.and(otherStorage)
                return result == otherStorage
            }
            return super.containsAll(other)
        }

        private inner class MyIterator : MutableIterator<CollectionHandle?> {
            private var currentIndex = 0
            override fun hasNext(): Boolean {
                return currentIndex != -1 && storage.nextSetBit(currentIndex) != -1
            }

            override fun next(): CollectionHandle {
                val result: Int = storage.nextSetBit(currentIndex)
                if (result != -1) {
                    // Start checking next bit next time.
                    currentIndex = result + 1
                    return CollectionHandle(result)
                }
                throw NoSuchElementException("Called `.next` on iterator with no remaining values.")
            }
        }

        override val size: Int
          get(){
            return storage.cardinality()
        }*/
    }

    companion object {
        /** Construct a new (efficient) mutable set for tracking collection handles. */
        fun mutableSet(): MutableSet<CollectionHandle> {
            return CollectionHandleSet()
        }

        /**
         * Construct a new (mutable) set consisting of the passed in collection handles.
         *
         * Used by tests.
         */
        fun of(vararg handles: CollectionHandle): Set<CollectionHandle> {
            val result = mutableSet()
            for (handle in handles) {
                result.add(handle)
            }
            return result
        }

        /**
         * Construct a new supplier of collection handles.
         *
         * Handles returned by this supplier should not be used with unique handles produced by any
         * other supplier.
         */
        fun createSupplier(): Supplier<CollectionHandle> {
            return object : Supplier<CollectionHandle> {
                private val nextIndex = atomic(1)
                override fun get(): CollectionHandle {
                    return CollectionHandle(nextIndex.getAndIncrement())
                }
            }
        }
    }
}
