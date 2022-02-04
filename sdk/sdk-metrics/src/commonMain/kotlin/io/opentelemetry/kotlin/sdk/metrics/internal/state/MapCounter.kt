/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.collections.immutable.persistentMapOf

/**
 * Simple-as-possible backing structure for exponential histogram buckets. Can be used as a baseline
 * against other data structures.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time
 */
class MapCounter : ExponentialCounter {
    private val backing = atomic(persistentMapOf<Int, Long>())
    override var indexStart: Int = NULL_INDEX
        private set
    override var indexEnd: Int = NULL_INDEX
        private set

    constructor()
    /**
     * Create an independent copy of another ExponentialCounter.
     *
     * @param otherCounter another exponential counter to make a deep copy of.
     */
    constructor(otherCounter: ExponentialCounter) {
        indexStart = otherCounter.indexStart
        indexEnd = otherCounter.indexEnd
        val pairs =
            (indexStart..indexEnd)
                .map { key ->
                    val value = otherCounter[key]
                    key to value
                }
                .filter { it.second != 0L }
                .toMap()
        backing.update { it.putAll(pairs) }
    }

    override fun increment(index: Int, delta: Long): Boolean {
        if (indexStart == NULL_INDEX) {
            indexStart = index
            indexEnd = index
            doIncrement(index, delta)
            return true
        }

        // Extend window if possible. if it would exceed maxSize, then return false.
        if (index > indexEnd) {
            if (index - indexStart + 1 > MAX_SIZE) {
                return false
            }
            indexEnd = index
        } else if (index < indexStart) {
            if (indexEnd - index + 1 > MAX_SIZE) {
                return false
            }
            indexStart = index
        }
        doIncrement(index, delta)
        return true
    }

    override operator fun get(index: Int): Long {
        if (index < indexStart || index > indexEnd) {
            throw IndexOutOfBoundsException("Index $index out of range.")
        }
        return backing.value[index] ?: return 0
    }

    override fun isEmpty(): Boolean = backing.value.isEmpty()

    private fun doIncrement(index: Int, delta: Long) {
        val prevValue: Long = backing.value.getOrElse(index) { 0L }
        if (prevValue + delta == 0L) {
            // in the case of a decrement result may be 0, so we remove the entry
            backing.update {
                val result = it.remove(index)
                if (isEmpty()) {
                    indexStart = NULL_INDEX
                    indexEnd = NULL_INDEX
                } else {
                    // find largest and smallest index to remap window
                    indexStart = result.keys.minOf { key -> key }
                    indexEnd = result.keys.maxOf { key -> key }
                }
                result
            }
        } else {
            backing.update { it.put(index, prevValue + delta) }
        }
    }

    override fun toString(): String {
        return backing.toString()
    }

    companion object {
        const val MAX_SIZE = 320
        private const val NULL_INDEX = Int.MIN_VALUE
    }
}
