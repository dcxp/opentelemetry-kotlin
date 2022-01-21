/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.context

import io.opentelemetry.Closeable

/**
 * An [AutoCloseable] that represents a mounted context for a block of code. A failure to call
 * [Scope.close] will generally break tracing or cause memory leaks. It is recommended that you use
 * this class with a `try-with-resources` block:
 *
 * <pre>`try (Scope ignored = span.makeCurrent()) { ... } `</pre> *
 */
interface Scope : Closeable {
    companion object {
        /**
         * Returns a [Scope] that does nothing. Represents attaching a [Context] when it is already
         * attached.
         *
         * fun noop(): Scope? { return NoopScope.INSTANCE }
         */
    }
}
