/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.data

import io.opentelemetry.kotlin.api.trace.StatusCode

/**
 * Defines the status of a [Span] by providing a standard [StatusCode] in conjunction with an
 * optional descriptive message. Instances of `Status` are created by starting with the template for
 * the appropriate [StatusCode] and supplementing it with additional information:
 * `Status.NOT_FOUND.withDescription("Could not find 'important_file.txt'");`
 */
internal object ImmutableStatusData {
    /**
     * The operation has been validated by an Application developers or Operator to have completed
     * successfully.
     */
    val OK: StatusData = createInternal(StatusCode.OK, "")

    /** The default status. */
    val UNSET: StatusData = createInternal(StatusCode.UNSET, "")

    /** The operation contains an error. */
    val ERROR: StatusData = createInternal(StatusCode.ERROR, "")

    /**
     * Creates a derived instance of `Status` with the given description.
     *
     * @param description the new description of the `Status`.
     * @return The newly created `Status` with the given description.
     */
    fun create(statusCode: StatusCode, description: String = ""): StatusData {
        if (description.isEmpty()) {
            return when (statusCode) {
                StatusCode.UNSET -> StatusData.unset()
                StatusCode.OK -> StatusData.ok()
                StatusCode.ERROR -> StatusData.error()
            }
        }
        return createInternal(statusCode, description)
    }

    private fun createInternal(statusCode: StatusCode, description: String = ""): StatusData {
        return Implementation(statusCode, description)
    }

    private class Implementation(
        override val statusCode: StatusCode,
        override val description: String
    ) : StatusData {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Implementation

            if (statusCode != other.statusCode) return false
            if (description != other.description) return false

            return true
        }

        override fun hashCode(): Int {
            var result = statusCode.hashCode()
            result = 31 * result + description.hashCode()
            return result
        }
    }
}
