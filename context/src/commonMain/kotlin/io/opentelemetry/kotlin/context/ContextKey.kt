/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.context

/**
 * Key for indexing values of type [T] stored in a [Context]. [ContextKey] are compared by
 * reference, so it is expected that only one [ContextKey] is created for a particular type of
 * context value.
 *
 * <pre>`public class ContextUser {
 *
 * private static final ContextKey<MyState> KEY = ContextKey.named("MyState");
 *
 * public Context startWork() { return Context.withValues(KEY, new MyState()); }
 *
 * public void continueWork(Context context) { MyState state = context.get(KEY); // Keys are
 * compared by reference only. assert state != Context.current().get(ContextKey.named("MyState"));
 * ... } }
 *
 * `</pre> *
 */
// ErrorProne false positive, this is used for its type constraint, not only as a bag of statics.
interface ContextKey<T> {
    companion object {
        /**
         * Returns a new [ContextKey] with the given debug name. The name does not impact behavior
         * and is only for debugging purposes. Multiple different keys with the same name will be
         * separate keys.
         */
        fun <T> named(name: String): ContextKey<T> {
            return DefaultContextKey(name)
        }
    }
}
