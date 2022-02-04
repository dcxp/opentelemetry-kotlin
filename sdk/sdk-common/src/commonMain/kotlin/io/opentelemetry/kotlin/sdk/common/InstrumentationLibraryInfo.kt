/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.common

/**
 * Holds information about the instrumentation library specified when creating an instance of [ ]
 * using the Tracer Provider.
 */
interface InstrumentationLibraryInfo { // Package protected ctor to avoid others to extend this
    // class. {
    /**
     * Returns the name of the instrumentation library.
     *
     * @return the name of the instrumentation library.
     */
    val name: String

    /**
     * Returns the version of the instrumentation library, or `null` if not available.
     *
     * @return the version of the instrumentation library, or `null` if not available.
     */
    val version: String?

    /**
     * Returns the URL of the schema used by this instrumentation library, or `null` if not
     * available.
     *
     * @return the URL of the schema used by this instrumentation library, or `null` if not
     * available.
     */
    val schemaUrl: String?

    companion object {
        private val EMPTY = create("", null)

        /**
         * Creates a new instance of [InstrumentationLibraryInfo].
         *
         * @param name name of the instrumentation library (e.g.,
         * "io.opentelemetry.kotlin.contrib.mongodb"), must not be null
         * @param version version of the instrumentation library (e.g., "1.0.0"), might be null
         * @return the new instance
         */
        fun create(name: String, version: String?): InstrumentationLibraryInfo {
            return Implementation(name, version, null)
        }

        /**
         * Creates a new instance of [InstrumentationLibraryInfo].
         *
         * @param name name of the instrumentation library (e.g.,
         * "io.opentelemetry.kotlin.contrib.mongodb"), must not be null
         * @param version version of the instrumentation library (e.g., "1.0.0"), might be null
         * @param schemaUrl the URL of the OpenTelemetry schema being used by this instrumentation
         * library.
         * @return the new instance
         * @since 1.4.0
         */
        fun create(name: String, version: String?, schemaUrl: String?): InstrumentationLibraryInfo {
            return Implementation(name, version, schemaUrl)
        }

        /**
         * Returns an "empty" `InstrumentationLibraryInfo`.
         *
         * @return an "empty" `InstrumentationLibraryInfo`.
         */
        fun empty(): InstrumentationLibraryInfo {
            return EMPTY
        }

        private data class Implementation(
            override val name: String,
            override val version: String?,
            override val schemaUrl: String?
        ) : InstrumentationLibraryInfo
    }
}
