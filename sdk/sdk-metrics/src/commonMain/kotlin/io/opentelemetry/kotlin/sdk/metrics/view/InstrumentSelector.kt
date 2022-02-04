/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.view

import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentType
import io.opentelemetry.kotlin.sdk.metrics.internal.view.StringPredicates

/**
 * Provides means for selecting one or more instruments. Used for configuring aggregations for the
 * specified instruments.
 */
interface InstrumentSelector {
    /**
     * Returns [InstrumentType] that should be selected. If null, then this specifier will not be
     * used.
     */
    val instrumentType: InstrumentType

    /** Returns the [Predicate] for filtering instruments by name. Matches everything by default. */
    val instrumentNameFilter: (String) -> Boolean

    /** Returns the selections criteria for [io.opentelemetry.kotlin.api.metrics.Meter]s. */
    val meterSelector: MeterSelector

    private data class InstrumentSelectorDto(
        val instrumentType: InstrumentType?,
        val instrumentNameFilter: (String) -> Boolean?,
        val meterSelector: MeterSelector?
    )

    /** Builder for [InstrumentSelector] instances. */
    data class Builder
    internal constructor(
        val instrumentType: InstrumentType? = null,
        val instrumentNameFilter: ((String) -> Boolean) = { true },
        val meterSelector: MeterSelector? = null
    ) {

        /** Sets a specifier for [InstrumentType]. */
        fun setInstrumentType(instrumentType: InstrumentType): Builder {
            return copy(instrumentType = instrumentType)
        }

        /**
         * Sets the [Pattern] for instrument names that will be selected.
         *
         * Note: The last provided of [.setInstrumentNameFilter], [ ][.setInstrumentNamePattern]
         * [.setInstrumentNameRegex] and [.setInstrumentName] is used.
         */
        fun setInstrumentNameFilter(instrumentNameFilter: ((String) -> Boolean)): Builder {
            return copy(instrumentNameFilter = instrumentNameFilter)
        }

        /**
         * Sets the [Pattern] for instrument names that will be selected.
         *
         * Note: The last provided of [.setInstrumentNameFilter], [ ][.setInstrumentNamePattern]
         * [.setInstrumentNameRegex] and [.setInstrumentName] is used.
         */
        fun setInstrumentNamePattern(instrumentNamePattern: Regex): Builder {
            return setInstrumentNameFilter(StringPredicates.regex(instrumentNamePattern))
        }

        /**
         * Sets the exact instrument name that will be selected.
         *
         * Note: The last provided of [.setInstrumentNameFilter], [ ][.setInstrumentNamePattern]
         * [.setInstrumentNameRegex] and [.setInstrumentName] is used.
         */
        fun setInstrumentName(instrumentName: String): Builder {
            return setInstrumentNameFilter(StringPredicates.exact(instrumentName))
        }

        /**
         * Sets a specifier for selecting Instruments by name.
         *
         * Note: The last provided of [.setInstrumentNameFilter], [ ][.setInstrumentNamePattern]
         * [.setInstrumentNameRegex] and [.setInstrumentName] is used.
         */
        fun setInstrumentNameRegex(regex: String): Builder {
            return setInstrumentNamePattern(Regex(regex))
        }

        /**
         * Sets the [MeterSelector] for which [io.opentelemetry.kotlin.api.metrics.Meter]s will be
         * included.
         */
        fun setMeterSelector(meterSelector: MeterSelector): Builder {
            return copy(meterSelector = meterSelector)
        }

        /** Returns an InstrumentSelector instance with the content of this builder. */
        fun build(): InstrumentSelector {
            check(instrumentType != null) { "The instrumentType has to be set" }
            return object : InstrumentSelector {
                override val instrumentType: InstrumentType
                    get() = this@Builder.instrumentType
                override val instrumentNameFilter: (String) -> Boolean
                    get() = this@Builder.instrumentNameFilter
                override val meterSelector: MeterSelector
                    get() = this@Builder.meterSelector!!
            }
        }
    }

    companion object {
        /**
         * Returns a new [Builder] for [InstrumentSelector].
         *
         * @return a new [Builder] for [InstrumentSelector].
         */
        fun builder(): Builder {
            return Builder()
                .setInstrumentNameFilter(StringPredicates.ALL)
                .setMeterSelector(MeterSelector.builder().build())
        }
    }
}
