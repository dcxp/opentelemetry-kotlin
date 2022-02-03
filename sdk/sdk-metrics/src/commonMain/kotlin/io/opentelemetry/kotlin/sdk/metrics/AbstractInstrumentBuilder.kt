/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.opentelemetry.kotlin.api.metrics.ObservableDoubleMeasurement
import io.opentelemetry.kotlin.api.metrics.ObservableLongMeasurement
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentType
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentValueType
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.state.MeterProviderSharedState
import io.opentelemetry.kotlin.sdk.metrics.internal.state.MeterSharedState
import io.opentelemetry.kotlin.sdk.metrics.internal.state.WriteableMetricStorage

/** Helper to make implementing builders easier. */
abstract class AbstractInstrumentBuilder<BuilderT : AbstractInstrumentBuilder<BuilderT>>
internal constructor(
    meterProviderSharedState: MeterProviderSharedState,
    meterSharedState: MeterSharedState,
    private val instrumentName: String,
    private var description: String,
    private var unit: String
) {
    private val meterProviderSharedState: MeterProviderSharedState
    private val meterSharedState: MeterSharedState

    init {
        this.meterProviderSharedState = meterProviderSharedState
        this.meterSharedState = meterSharedState
    }

    protected abstract val `this`: BuilderT

    fun setUnit(unit: String): BuilderT {
        this.unit = unit
        return `this`
    }

    fun setDescription(description: String): BuilderT {
        this.description = description
        return `this`
    }

    private fun makeDescriptor(
        type: InstrumentType,
        valueType: InstrumentValueType
    ): InstrumentDescriptor {
        return InstrumentDescriptor.create(instrumentName, description, unit, type, valueType)
    }

    protected fun <T> swapBuilder(swapper: SwapBuilder<T>): T {
        return swapper.newBuilder(
            meterProviderSharedState,
            meterSharedState,
            instrumentName,
            description,
            unit
        )
    }

    fun <I : AbstractInstrument> buildSynchronousInstrument(
        type: InstrumentType,
        valueType: InstrumentValueType,
        instrumentFactory: (InstrumentDescriptor, WriteableMetricStorage) -> I
    ): I {
        val descriptor: InstrumentDescriptor = makeDescriptor(type, valueType)
        val storage: WriteableMetricStorage =
            meterSharedState.registerSynchronousMetricStorage(descriptor, meterProviderSharedState)
        return instrumentFactory(descriptor, storage)
    }

    fun registerDoubleAsynchronousInstrument(
        type: InstrumentType,
        updater: (ObservableDoubleMeasurement) -> Unit
    ) {
        val descriptor: InstrumentDescriptor = makeDescriptor(type, InstrumentValueType.DOUBLE)
        meterSharedState.registerDoubleAsynchronousInstrument(
            descriptor,
            meterProviderSharedState,
            updater
        )
    }

    fun registerLongAsynchronousInstrument(
        type: InstrumentType,
        updater: (ObservableLongMeasurement) -> Unit
    ) {
        val descriptor: InstrumentDescriptor = makeDescriptor(type, InstrumentValueType.LONG)
        meterSharedState.registerLongAsynchronousInstrument(
            descriptor,
            meterProviderSharedState,
            updater
        )
    }

    protected fun interface SwapBuilder<T> {
        fun newBuilder(
            meterProviderSharedState: MeterProviderSharedState,
            meterSharedState: MeterSharedState,
            name: String,
            description: String,
            unit: String
        ): T
    }
}
