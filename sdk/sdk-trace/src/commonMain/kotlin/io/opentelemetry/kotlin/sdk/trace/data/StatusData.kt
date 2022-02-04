/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.data

import io.opentelemetry.kotlin.api.trace.StatusCode

/**
 * Defines the status of a [Span] by providing a standard [StatusCode] in conjunction with an
 * optional descriptive message.
 */
interface StatusData {
    /** Returns the status code. */
    val statusCode: StatusCode

    /**
     * Returns the description of this `Status` for human consumption.
     *
     * @return the description of this `Status`.
     */
    val description: String

    companion object {
        /**
         * Returns a [StatusData] indicating the operation has been validated by an application
         * developer or operator to have completed successfully.
         */
        fun ok(): StatusData {
            return ImmutableStatusData.OK
        }

        /** Returns the default [StatusData]. */
        fun unset(): StatusData {
            return ImmutableStatusData.UNSET
        }

        /** Returns a [StatusData] indicating an error occurred. */
        fun error(): StatusData {
            return ImmutableStatusData.ERROR
        }

        /**
         * Returns a [StatusData] with the given `code` and `description`. If `description` is
         * `null`, the returned [StatusData] does not have a description.
         */
        fun create(code: StatusCode, description: String = ""): StatusData {
            return ImmutableStatusData.create(code, description)
        }
    }
}
