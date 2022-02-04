/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor

/**
 * Utilities for logging metric diagnostic issues.
 *
 * This is a publicly accessible class purely for testing.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
object DebugUtils {
    fun duplicateMetricErrorMessage(ex: DuplicateMetricStorageException): String {
        return duplicateMetricErrorMessage(ex.existing, ex.conflict)
    }

    /**
     * Creates a detailed error message comparing two MetricDescriptors.
     *
     * This should identify all issues between the descriptor and log information on where they are
     * defined. Users should be able to find/fix issues based on this error.
     *
     * Visible for testing.
     *
     * @param existing The already registered metric stream.
     * @param conflict The about-to-be registered metric stream.
     * @return A multi-line debugging string.
     */
    fun duplicateMetricErrorMessage(
        existing: MetricDescriptor,
        conflict: MetricDescriptor
    ): String {
        val result = StringBuilder("Found duplicate metric definition: ")
        result.append(existing.name).append("\n")
        // Now we write out where the existing metric descriptor is coming from, either a raw
        // instrument
        // or a view on a raw instrument.
        if (conflict.name != conflict.sourceInstrument.name) {
            // Record the source view.
            result.append("\tVIEW defined\n")
            conflict.sourceView?.let { v -> result.append(v.sourceInfo.multiLineDebugString()) }
            result
                .append("\tFROM instrument ")
                .append(conflict.sourceInstrument.name)
                .append("\n")
                .append(conflict.sourceInstrument.sourceInfo.multiLineDebugString())
        } else {
            result.append(conflict.sourceInstrument.sourceInfo.multiLineDebugString()).append("\n")
        }
        // Add information on what's at conflict.
        result.append("Causes\n")
        if (existing.description != conflict.description) {
            result
                .append("- Description [")
                .append(conflict.description)
                .append("] does not match [")
                .append(existing.description)
                .append("]\n")
        }
        if (existing.unit != conflict.unit) {
            result
                .append("- Unit [")
                .append(conflict.unit)
                .append("] does not match [")
                .append(existing.unit)
                .append("]\n")
        }

        // Next we write out where the existing metric deescriptor came from, either a raw
        // instrument
        // or a view on a raw instrument.
        if (existing.name == existing.sourceInstrument.name) {
            result
                .append(
                    "Original instrument registered with same name but different description or unit.\n"
                )
                .append(existing.sourceInstrument.sourceInfo.multiLineDebugString())
                .append("\n")
        } else {
            // Log that the view changed the name.
            result.append("Conflicting view registered.\n")
            existing.sourceView?.let { view ->
                result.append(view.sourceInfo.multiLineDebugString())
            }
            result
                .append("FROM instrument ")
                .append(existing.sourceInstrument.name)
                .append("\n")
                .append(existing.sourceInstrument.sourceInfo.multiLineDebugString())
                .append("\n")
        }
        return result.toString()
    }
}
