/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.context

internal class DefaultContextKey<T>(private val name: String) : ContextKey<T> {
    override fun toString(): String {
        return name
    }
}
