/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.debug

/**
 * An interface that can be used to record the (runtime) source of registered metrics in the sdk.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
interface SourceInfo {
    /**
     * Returns a debugging string to report where a given metric was registered.
     *
     * Example: `MyFile.java:15`
     */
    fun shortDebugString(): String

    /**
     * Returns a multi-line debugging string to report where a given metric was registered.
     *
     * Example:
     *
     * <pre> at full.package.name.method MyFile.java:15 at full.packae.name.otherMethod
     * MyOtherFile.java:10 </pre> *
     */
    fun multiLineDebugString(): String

    companion object {
        /** Returns a source info that asks the user to register information. */
        fun noSourceInfo(): SourceInfo {
            return NoSourceInfo.INSTANCE
        }

        /**
         * Constructs source information form the current stack.
         *
         * This will attempt to ignore SDK classes.
         */
        fun fromCurrentStack(): SourceInfo {
            return noSourceInfo()
        }
    }
}
