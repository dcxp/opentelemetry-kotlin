/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.context

/**
 * A value that can be stored inside [Context]. Types will generally use this interface to allow
 * storing themselves in [Context] without exposing a [ContextKey].
 */
interface ImplicitContextKeyed {
    /**
     * Adds this [ImplicitContextKeyed] value to the [current context][Context.current] and makes
     * the new [Context] the current context. [Scope.close] must be called to properly restore the
     * previous context from before this scope of execution or context will not work correctly. It
     * is recommended to use try-with-resources to call [Scope.close] automatically.
     *
     * This method is equivalent to `Context.current().with(value).makeCurrent()`.
     *
     * The default implementation of this method will store the [ImplicitContextKeyed] in a
     * [ThreadLocal]. Kotlin coroutine users SHOULD NOT use this method as the [ ] will not be
     * properly synced across coroutine suspension and resumption. Instead, use
     * `withContext(value.asContextElement())` provided by the `opentelemetry-extension-kotlin`
     * library.
     */
    fun makeCurrent(): Scope {
        return Context.current().with(this).makeCurrent()
    }

    /**
     * Returns a new [Context] created by setting `this` into the provided [ ]. It is generally
     * recommended to call [Context.with] instead of this method. The following are equivalent.
     *
     * * `context.with(myContextValue)`
     * * `myContextValue.storeInContext(context)`
     */
    fun storeInContext(context: Context): Context
}
