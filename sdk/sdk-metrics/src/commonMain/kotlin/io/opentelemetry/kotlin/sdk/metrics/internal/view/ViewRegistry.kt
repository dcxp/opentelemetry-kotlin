/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.view

import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor
import io.opentelemetry.kotlin.sdk.metrics.view.InstrumentSelector
import io.opentelemetry.kotlin.sdk.metrics.view.MeterSelector
import io.opentelemetry.kotlin.sdk.metrics.view.View

/**
 * Central location for Views to be registered. Registration of a view is done via the [ ].
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
class ViewRegistry internal constructor(private val reverseRegistration: List<RegisteredView>) {

    /**
     * Returns the metric [View] for a given instrument.
     *
     * @param descriptor description of the instrument.
     * @return The list of [View]s for this instrument in registered order, or a default aggregation
     * view.
     */
    fun findViews(descriptor: InstrumentDescriptor, meter: InstrumentationLibraryInfo): List<View> {
        val result =
            reverseRegistration
                .filter { entry -> matchesSelector(entry.instrumentSelector, descriptor, meter) }
                .map { entry -> entry.view }
        return result.ifEmpty { listOf(DEFAULT_VIEW) }
    }

    companion object {
        val DEFAULT_VIEW = View.builder().build()

        /** Returns a builder of [ViewRegistry]. */
        fun builder(): io.opentelemetry.kotlin.sdk.metrics.internal.view.ViewRegistryBuilder {
            return io.opentelemetry.kotlin.sdk.metrics.internal.view.ViewRegistryBuilder()
        }

        // Matches an instrument selector against an instrument + meter.
        private fun matchesSelector(
            selector: InstrumentSelector,
            descriptor: InstrumentDescriptor,
            meter: InstrumentationLibraryInfo
        ): Boolean {
            return (selector.instrumentType === descriptor.type) &&
                selector.instrumentNameFilter(descriptor.name) &&
                matchesMeter(selector.meterSelector, meter)
        }

        // Matches a meter selector against a meter.
        private fun matchesMeter(
            selector: MeterSelector,
            meter: InstrumentationLibraryInfo
        ): Boolean {
            return (selector.nameFilter(meter.name) &&
                (meter.version == null || selector.versionFilter(meter.version!!)) &&
                (meter.schemaUrl == null || selector.schemaUrlFilter(meter.schemaUrl!!)))
        }
    }
}
