/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.view

import io.opentelemetry.kotlin.sdk.metrics.view.InstrumentSelector
import io.opentelemetry.kotlin.sdk.metrics.view.View

/**
 * Builder for [ViewRegistry].
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
class ViewRegistryBuilder internal constructor() {
    private val orderedViews: MutableList<RegisteredView> = mutableListOf()

    /** Returns the [ViewRegistry]. */
    fun build(): ViewRegistry {
        return ViewRegistry(orderedViews.toList())
    }

    /**
     * Adds a new view to the registry.
     *
     * @param selector The instruments that should have their defaults altered.
     * @param view The [View] metric definition.
     * @return this
     */
    fun addView(selector: InstrumentSelector, view: View): ViewRegistryBuilder {
        orderedViews.add(RegisteredView.create(selector, view))
        return this
    }
}
