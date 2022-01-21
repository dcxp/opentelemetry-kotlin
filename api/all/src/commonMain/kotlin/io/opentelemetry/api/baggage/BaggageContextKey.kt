/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.baggage

import io.opentelemetry.context.ContextKey

/** Util class to hold on to the key for storing Baggage in the Context. */
internal object BaggageContextKey {
    val KEY: ContextKey<Baggage> = ContextKey.named("opentelemetry-baggage-key")
}
