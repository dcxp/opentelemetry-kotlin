/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
// Includes work from:
/*
 * Copyright 2020 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.opentelemetry.kotlin.context

/**
 * The storage for storing and retrieving the current [Context].
 *
 * If you want to implement your own storage or add some hooks when a [Context] is attached and
 * restored, you should use [ContextStorageProvider]. Here's an example that sets MDC before
 * [Context] is attached:
 *
 * <pre>`> public class MyStorage implements ContextStorageProvider { > > @Override > public
 * ContextStorage get() { > ContextStorage threadLocalStorage = ContextStorage.defaultStorage(); >
 * return new RequestContextStorage() { > @Override > public Scope T attach(Context toAttach) { >
 * Context current = current(); > setMdc(toAttach); > Scope scope =
 * threadLocalStorage.attach(toAttach); > return () -> { > clearMdc(); > setMdc(current); >
 * scope.close(); > } > } > > @Override > public Context current() { > return
 * threadLocalStorage.current(); > } > } > } > } `</pre> *
 */
interface ContextStorage {
    /**
     * Sets the specified [Context] as the current [Context] and returns a [Scope] representing the
     * scope of execution. [Scope.close] must be called when the current [Context] should be
     * restored to what it was before attaching `toAttach`.
     */
    fun attach(toAttach: Context): Scope

    /**
     * Returns the current [Context]. If no [Context] has been attached yet, this will return
     * `null`.
     */
    fun current(): Context

    /**
     * Returns the root [Context] which all other [Context] are derived from.
     *
     * The default implementation returns the root `ArrayBasedContext`, but subclasses can override
     * this method to return a root instance of a different [Context] implementation.
     */
    fun root(): Context {
        return ArrayBasedContext.root()
    }

    companion object {
        /**
         * Returns the [ContextStorage] being used by this application. This is only for use when
         * integrating with other context propagation mechanisms and not meant for direct use. To
         * attach or detach a [Context] in an application, use [Context.makeCurrent] and [ ]
         * [Scope.close].
         */
        fun get(): ContextStorage {
            return LazyStorage.get()
        }

        /** Returns the default [ContextStorage] which stores [Context] using a threadlocal. */
        fun defaultStorage(): ContextStorage {
            return ArrayBasedContextStorage()
        }
    }
}
