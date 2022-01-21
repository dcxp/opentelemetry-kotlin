/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.trace

import io.opentelemetry.context.ContextKey

/** Util class to hold on to the key for storing a Span in the Context. */
internal object SpanContextKey {
    val KEY: ContextKey<Span> = ContextKey.named("opentelemetry-trace-span-key")
}
