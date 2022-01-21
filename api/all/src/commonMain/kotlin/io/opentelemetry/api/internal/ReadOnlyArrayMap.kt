/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
// Includes work from:
/*
 * Copyright 2013-2020 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.opentelemetry.api.internal

/** A read-only view of an array of key-value pairs. */
class ReadOnlyArrayMap<K, V> private constructor(private val array: List<Any?>) : Map<K, V> {
    override val size: Int = array.size / 2

    override fun isEmpty(): Boolean {
        return array.isEmpty()
    }

    override val keys: Set<K> = KeyView(this)

    override val values: Collection<V> = ValueView(this)

    override val entries: Set<Map.Entry<K, V>> = PairView(this)

    abstract class BaseView<T>(private val target: ReadOnlyArrayMap<*, *>) : Set<T> {
        override val size: Int = target.size

        override fun isEmpty(): Boolean = target.isEmpty()

        override fun containsAll(elements: Collection<T>): Boolean {
            for (element in elements) {
                if (!contains(element)) {
                    return false
                }
            }
            return true
        }
    }

    class KeyView<K>(val target: ReadOnlyArrayMap<K, *>) : BaseView<K>(target) {
        override fun contains(element: K): Boolean {
            return target.containsKey(element)
        }

        override fun iterator(): Iterator<K> {
            return IteratorImpl(target)
        }

        private class IteratorImpl<K>(val target: ReadOnlyArrayMap<K, *>) : Iterator<K> {
            var poss = 0

            override fun hasNext(): Boolean {
                return poss < target.array.size
            }

            override fun next(): K {
                val key = target.key(poss)
                poss += 2
                return key
            }
        }
    }

    class ValueView<V>(val target: ReadOnlyArrayMap<*, V>) : BaseView<V>(target) {
        override fun contains(element: V): Boolean {
            return target.containsValue(element)
        }

        override fun iterator(): Iterator<V> {
            return IteratorImpl(target)
        }

        private class IteratorImpl<V>(val target: ReadOnlyArrayMap<*, V>) : Iterator<V> {
            var poss = 1

            override fun hasNext(): Boolean {
                return poss < target.array.size
            }

            override fun next(): V {
                val value = target.value(poss)
                poss += 2
                return value
            }
        }
    }

    class PairView<K, V>(val target: ReadOnlyArrayMap<K, V>) : BaseView<Map.Entry<K, V>>(target) {

        override fun contains(element: Map.Entry<K, V>): Boolean {
            val pos = target.arrayIndexOfKey(element.key)
            if (pos == -1) {
                return false
            }
            return this.target.array[pos + 1] == element.value
        }

        override fun iterator(): Iterator<Map.Entry<K, V>> {
            return IteratorImpl(target)
        }

        private class IteratorImpl<K, V>(val target: ReadOnlyArrayMap<K, V>) :
            Iterator<Map.Entry<K, V>> {
            var poss = 0

            override fun hasNext(): Boolean {
                return poss < target.array.size
            }

            override fun next(): Map.Entry<K, V> {
                val entry = EntryImpl(target.key(poss), target.value(poss + 1))
                poss += 2
                return entry
            }

            private class EntryImpl<K, V>(override val key: K, override val value: V) :
                Map.Entry<K, V>
        }
    }

    override fun containsKey(key: K): Boolean {
        return arrayIndexOfKey(key) != -1
    }

    override fun containsValue(value: V): Boolean {
        var i = 0
        while (i < array.size) {
            if (value(i + 1) == value) {
                return true
            }
            i += 2
        }
        return false
    }

    override operator fun get(key: K): V? {
        val i = arrayIndexOfKey(key)
        return if (i != -1) value(i + 1) else null
    }

    fun arrayIndexOfKey(o: K): Int {
        val result = -1
        var i = 0
        while (i < array.size) {
            if (o == key(i)) {
                return i
            }
            i += 2
        }
        return result
    }

    @Suppress("UNCHECKED_CAST")
    fun key(i: Int): K {
        return array[i] as K
    }

    @Suppress("UNCHECKED_CAST")
    fun value(i: Int): V {
        return array[i] as V
    }

    override fun toString(): String {
        val result = StringBuilder()
        result.append("ReadOnlyArrayMap{")
        var i = 0
        while (i < array.size) {
            result.append(key(i)).append('=').append(value(i + 1))
            result.append(',')
            i += 2
        }
        result.setLength(result.length - 1)
        return result.append("}").toString()
    }

    companion object {
        /** Returns a read-only view of the given `array`. */
        fun <K, V> wrap(array: List<Any?>): Map<K, V> {
            return if (array.isEmpty()) {
                emptyMap()
            } else ReadOnlyArrayMap(array)
        }
    }
}
