/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.descriptor

import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentType
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentValueType
import io.opentelemetry.kotlin.sdk.metrics.view.View

/**
 * Describes a metric that will be output.
 *
 * Provides equality/identity semantics for detecting duplicate metrics of incompatible.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
interface MetricDescriptor {
    val name: String
    val description: String
    val unit: String

    /** The view that lead to the creation of this metric, if applicable. */
    val sourceView: View?

    /** The instrument which lead to the creation of this metric. */
    val sourceInstrument: InstrumentDescriptor

    /** Returns true if another metric descriptor is compatible with this one. */
    fun isCompatibleWith(other: MetricDescriptor): Boolean {
        return (name == other.name && description == other.description && unit == other.unit)
    }

    companion object {
        /**
         * Constructs a metric descriptor with no source instrument/view.
         *
         * Used for testing + empty-storage only.
         */
        fun create(name: String, description: String, unit: String): MetricDescriptor {
            return Implementation(
                name,
                description,
                unit,
                null,
                io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor
                    .Companion.create(
                    name,
                    description,
                    unit,
                    InstrumentType.OBSERVABLE_GAUGE,
                    InstrumentValueType.DOUBLE
                )
            )
        }

        /** Constructs a metric descriptor for a given View + instrument. */
        fun create(view: View, instrument: InstrumentDescriptor): MetricDescriptor {
            val name = if (view.name == null) instrument.name else view.name!!
            val description =
                if (view.description == null) instrument.description else view.description!!
            return Implementation(name, description, instrument.unit, view, instrument)
        }

        data class Implementation(
            override val name: String,
            override val description: String,
            override val unit: String,
            override val sourceView: View?,
            override val sourceInstrument: InstrumentDescriptor
        ) : MetricDescriptor
    }
}
