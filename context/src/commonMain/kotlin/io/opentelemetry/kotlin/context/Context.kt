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
package io.opentelemetry.kotlin.context

/**
 * A context propagation mechanism which can carry scoped-values across API boundaries and between
 * threads.
 *
 * A Context object can be [set][.makeCurrent] to the [ContextStorage], which effectively forms a
 * **scope** for the context. The scope is bound to the current thread. Within a scope, its Context
 * is accessible even across API boundaries, through [.current]. The scope is later exited by
 * [Scope.close] closing} the scope.
 *
 * Context objects are immutable and inherit state from their parent. To add or overwrite the
 * current state a new context object must be created and then attached, replacing the previously
 * bound context. For example:
 *
 * <pre>`Context withCredential = Context.current().with(CRED_KEY, cred); withCredential.wrap(new
 * Runnable() { public void run() { readUserRecords(userId, CRED_KEY.get()); } }).run(); `</pre> *
 *
 * Notes and cautions on use:
 *
 * * Every [.makeCurrent] must be followed by a [Scope.close]. Breaking these rules may lead to
 * memory leaks and incorrect scoping.
 * * While Context objects are immutable they do not place such a restriction on the state they
 * store.
 * * Context is not intended for passing optional parameters to an API and developers should take
 * care to avoid excessive dependence on context when designing an API.
 * * Attaching Context from a different ancestor will cause information in the current Context to be
 * lost. This should generally be avoided.
 *
 * Context propagation is not trivial, and when done incorrectly can lead to broken traces or even
 * mixed traces. We provide a debug mechanism for context propagation, which can be enabled by
 * setting `-Dio.opentelemetry.kotlin.context.enableStrictContext=true` in your JVM args. This will
 * enable a strict checker that makes sure that [Scope]s are closed on the correct thread and that
 * they are not garbage collected before being closed. This is done with some relatively expensive
 * stack trace walking. It is highly recommended to enable this in unit tests and staging
 * environments, and you may consider enabling it in production if you have the CPU budget or have
 * very strict requirements on context being propagated correctly (i.e., because you use context in
 * a multi-tenant system). For kotlin coroutine users, this will also detect invalid usage of [
 * ][.makeCurrent] from coroutines and suspending functions. This detection relies on internal APIs
 * of kotlin coroutines and may not function across all versions - let us know if you find a version
 * of kotlin coroutines where this mechanism does not function.
 *
 * @see StrictContextStorage
 */
interface Context {
    /**
     * Returns the value stored in this [Context] for the given [ContextKey], or `null` if there is
     * no value for the key in this context.
     */
    operator fun <V> get(key: ContextKey<V>): V?

    fun <V> tryGet(key: ContextKey<V>, callback: (V) -> Unit) {
        val value = this[key]
        if (value != null) {
            callback(value)
        }
    }

    fun <V> getOrElse(key: ContextKey<V>, otherValue: V): V {
        var internalValue = otherValue
        tryGet(key) { internalValue = it }
        return internalValue
    }

    fun <V> getOrElse(key: ContextKey<V>, otherValueFactory: () -> V): V {
        var internalValue: V? = null
        tryGet(key) { internalValue = it }
        if (internalValue == null) {
            internalValue = otherValueFactory()
        }
        return internalValue!!
    }

    /**
     * Returns a new context with the given key value set.
     *
     * <pre>`Context withCredential = Context.current().with(CRED_KEY, cred);
     * withCredential.wrap(new Runnable() { public void run() { readUserRecords(userId,
     * CRED_KEY.get()); } }).run(); `</pre> *
     *
     * Note that multiple calls to [.with] can be chained together.
     *
     * <pre>`context.with(K1, V1).with(K2, V2); `</pre> *
     *
     * Nonetheless, [Context] should not be treated like a general purpose map with a large number
     * of keys and values — combine multiple related items together into a single key instead of
     * separating them. But if the items are unrelated, have separate keys for them.
     */
    fun <V> with(key: ContextKey<V>, value: V): Context

    /** Returns a new [Context] with the given [ImplicitContextKeyed] set. */
    fun with(value: ImplicitContextKeyed): Context {
        return value.storeInContext(this)
    }

    /**
     * Makes this the [current context][Context.current] and returns a [Scope] which corresponds to
     * the scope of execution this context is current for. [Context.current] will return this
     * [Context] until [Scope.close] is called. [Scope.close] must be called to properly restore the
     * previous context from before this scope of execution or context will not work correctly. It
     * is recommended to use try-with-resources to call [ ][Scope.close] automatically.
     *
     * The default implementation of this method will store the [Context] in a [ ]. Kotlin coroutine
     * users SHOULD NOT use this method as the [ThreadLocal] will not be properly synced across
     * coroutine suspension and resumption. Instead, use `withContext(context.asContextElement())`
     * provided by the `opentelemetry-extension-kotlin` library.
     *
     * <pre>`Context prevCtx = Context.current(); try (Scope ignored = ctx.makeCurrent()) { assert
     * Context.current() == ctx; ... } assert Context.current() == prevCtx; `</pre> *
     */
    fun makeCurrent(): Scope {
        return ContextStorage.get().attach(this)
    }

    companion object {
        /** Return the context associated with the current [Scope]. */
        fun current(): Context {
            return try {
                ContextStorage.get().current()
            } catch (_: Exception) {
                root()
            }
        }

        /**
         * Returns the root [Context] which all other [Context] are derived from.
         *
         * It should generally not be required to use the root [Context] directly - instead, use
         * [Context.current] to operate on the current [Context]. Only use this method if you are
         * absolutely sure you need to disregard the current [Context]
         * - this almost always is only a workaround hiding an underlying context propagation issue.
         */
        fun root(): Context {
            return ContextStorage.get().root()
        }
    }
}
