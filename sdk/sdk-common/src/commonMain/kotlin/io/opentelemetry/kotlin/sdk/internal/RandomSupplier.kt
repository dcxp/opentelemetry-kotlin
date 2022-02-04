/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.internal

import kotlin.random.Random

/**
 * Provides random number generater constructor utilities.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
object RandomSupplier {
    /** Returns the platform default for random number generation. */
    fun platformDefault(): () -> Random {
        // note: check borrowed from OkHttp's check for Android.
        return { Random.Default }
    }
}
