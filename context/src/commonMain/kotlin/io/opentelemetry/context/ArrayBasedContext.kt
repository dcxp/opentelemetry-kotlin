/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
// Includes work from:
/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.opentelemetry.context

internal class ArrayBasedContext private constructor(private val entries: Array<Any?>) : Context {
    @Suppress("UNCHECKED_CAST")
    override operator fun <V> get(key: ContextKey<V>): V? {
        var i = 0
        while (i < entries.size) {
            if (entries[i] === key) {
                return entries[i + 1] as V
            }
            i += 2
        }
        return null
    }

    override fun <V> with(key: ContextKey<V>, value: V): Context {
        var i = 0
        while (i < entries.size) {
            if (entries[i] === key) {
                if (entries[i + 1] === value) {
                    return this
                }
                val newEntries: Array<Any?> = entries.copyOf()
                newEntries[i + 1] = value
                return ArrayBasedContext(newEntries)
            }
            i += 2
        }
        val newEntries: Array<Any?> = entries.copyOf(entries.size + 2)
        newEntries[newEntries.size - 2] = key
        newEntries[newEntries.size - 1] = value
        return ArrayBasedContext(newEntries)
    }

    override fun toString(): String {
        val sb = StringBuilder("{")
        var i = 0
        while (i < entries.size) {
            sb.append(entries[i]).append('=').append(entries[i + 1]).append(", ")
            i += 2
        }
        // get rid of that last pesky comma
        if (sb.length > 1) {
            sb.setLength(sb.length - 2)
        }
        sb.append('}')
        return sb.toString()
    }

    companion object {
        private val ROOT: Context = ArrayBasedContext(arrayOfNulls(0))

        // Used by auto-instrumentation agent. Check with auto-instrumentation before making changes
        // to
        // this method.
        //
        // In particular, do not change this return type to DefaultContext because
        // auto-instrumentation
        // hijacks this method and returns a bridged implementation of Context.
        //
        // Ideally auto-instrumentation would hijack the public Context.root() instead of this
        // method, but auto-instrumentation also needs to inject its own implementation of Context
        // into the class loader at the same time, which causes a problem because injecting a class
        // into
        // the class loader automatically resolves its super classes (interfaces), which in this
        // case is
        // Context, which would be the same class (interface) being instrumented at that time,
        // which would lead to the JVM throwing a LinkageError "attempted duplicate interface
        // definition"
        fun root(): Context {
            return ROOT
        }
    }
}
