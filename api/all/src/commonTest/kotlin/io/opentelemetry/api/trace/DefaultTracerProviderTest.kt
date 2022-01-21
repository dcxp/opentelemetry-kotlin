/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.trace

import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import kotlin.test.Test

internal class DefaultTracerProviderTest {
    @Test
    fun returnsDefaultTracer() {
        TracerProvider.noop()["test"] shouldNotBeSameInstanceAs DefaultTracer::class
        TracerProvider.noop()["test", "1.0"] shouldNotBeSameInstanceAs DefaultTracer::class
    }
}
