/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.view

/**
 * Re-usable string predicates.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
object StringPredicates {
    /** A string predicate that matches all strings. */
    val ALL: (String) -> Boolean = { _: String -> true }

    /** A string predicate that does exact string matching. */
    fun exact(match: String): (String) -> Boolean {
        return { anObject: Any? -> match == anObject }
    }

    /** A string predicate that matches against a regular expression. */
    fun regex(pattern: Regex): (String) -> Boolean {
        return { input: String -> pattern.matches(input) }
    }
}
