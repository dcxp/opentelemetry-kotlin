/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.view

import io.opentelemetry.kotlin.sdk.metrics.view.InstrumentSelector
import io.opentelemetry.kotlin.sdk.metrics.view.View

/**
 * Internal representation of a [View] and [InstrumentSelector].
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
interface RegisteredView {
    /** Instrument fitler for applying this view. */
    val instrumentSelector: InstrumentSelector

    /** The view to apply. */
    val view: View

    companion object {
        fun create(selector: InstrumentSelector, view: View): RegisteredView {
            return Implementation(selector, view)
        }
    }
    class Implementation(
        override val instrumentSelector: InstrumentSelector,
        override val view: View
    ) : RegisteredView
}
