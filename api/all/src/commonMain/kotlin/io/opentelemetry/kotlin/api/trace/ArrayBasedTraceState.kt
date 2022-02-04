/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.trace

import io.opentelemetry.kotlin.api.internal.ReadOnlyArrayMap

internal abstract class ArrayBasedTraceState : TraceState {

    override operator fun get(key: String): String {
        val entries = entries
        var i = 0
        while (i < entries.size) {
            if (entries[i] == key) {
                return entries[i + 1]
            }
            i += 2
        }
        throw IllegalStateException("There is no key $key")
    }

    override fun size(): Int {
        return entries.size / 2
    }

    override val isEmpty: Boolean
        get() = entries.isEmpty()

    override fun forEach(consumer: (String, String) -> Unit) {
        val entries = entries
        var i = 0
        while (i < entries.size) {
            consumer(entries[i], entries[i + 1])
            i += 2
        }
    }

    override fun asMap(): Map<String, String> {
        return ReadOnlyArrayMap.wrap(entries)
    }

    abstract val entries: List<String>
    override fun toBuilder(): TraceStateBuilder {
        return ArrayBasedTraceStateBuilder(this)
    }

    companion object {
        fun create(entries: List<String>): ArrayBasedTraceState {
            return Instance(entries)
        }

        private class Instance(override val entries: List<String>) : ArrayBasedTraceState() {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || this::class != other::class) return false

                other as Instance

                if (entries != other.entries) return false

                return true
            }

            override fun hashCode(): Int {
                return entries.hashCode()
            }

            override fun toString(): String {
                return "ArrayBasedTraceState(entries=$entries)"
            }
        }
    }
}
