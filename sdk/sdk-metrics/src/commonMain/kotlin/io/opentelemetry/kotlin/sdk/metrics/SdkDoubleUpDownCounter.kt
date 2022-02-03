/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.metrics.BoundDoubleUpDownCounter
import io.opentelemetry.kotlin.api.metrics.DoubleUpDownCounter
import io.opentelemetry.kotlin.api.metrics.DoubleUpDownCounterBuilder
import io.opentelemetry.kotlin.api.metrics.LongUpDownCounterBuilder
import io.opentelemetry.kotlin.api.metrics.ObservableDoubleMeasurement
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentType
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentValueType
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.state.BoundStorageHandle
import io.opentelemetry.kotlin.sdk.metrics.internal.state.MeterProviderSharedState
import io.opentelemetry.kotlin.sdk.metrics.internal.state.MeterSharedState
import io.opentelemetry.kotlin.sdk.metrics.internal.state.WriteableMetricStorage

internal class SdkDoubleUpDownCounter
private constructor(descriptor: InstrumentDescriptor, storage: WriteableMetricStorage) :
    AbstractInstrument(descriptor), DoubleUpDownCounter {
    private val storage: WriteableMetricStorage

    init {
        this.storage = storage
    }

    override fun add(increment: Double, attributes: Attributes, context: Context) {
        storage.recordDouble(increment, attributes, context)
    }

    override fun add(increment: Double, attributes: Attributes) {
        add(increment, attributes, Context.current())
    }

    override fun add(increment: Double) {
        add(increment, Attributes.empty())
    }

    override fun bind(attributes: Attributes): BoundDoubleUpDownCounter {
        return BoundInstrument(storage.bind(attributes), attributes)
    }

    internal class BoundInstrument(handle: BoundStorageHandle, attributes: Attributes) :
        BoundDoubleUpDownCounter {
        private val handle: BoundStorageHandle
        private val attributes: Attributes

        init {
            this.handle = handle
            this.attributes = attributes
        }

        override fun add(increment: Double, context: Context) {
            handle.recordDouble(increment, attributes, context)
        }

        override fun add(increment: Double) {
            add(increment, Context.current())
        }

        override fun unbind() {
            handle.release()
        }
    }

    internal class Builder(
        meterProviderSharedState: MeterProviderSharedState,
        sharedState: MeterSharedState,
        name: String,
        description: String,
        unit: String
    ) :
        io.opentelemetry.kotlin.sdk.metrics.AbstractInstrumentBuilder<Builder>(
            meterProviderSharedState,
            sharedState,
            name,
            description,
            unit
        ),
        DoubleUpDownCounterBuilder {
        override val `this`: Builder
            get() = this

        override fun build(): DoubleUpDownCounter {
            return buildSynchronousInstrument(
                InstrumentType.UP_DOWN_COUNTER,
                InstrumentValueType.DOUBLE
            ) { descriptor: InstrumentDescriptor, storage: WriteableMetricStorage ->
                SdkDoubleUpDownCounter(descriptor, storage)
            }
        }

        override fun ofLongs(): LongUpDownCounterBuilder {
            return swapBuilder(SdkLongUpDownCounter::Builder)
        }

        override fun buildWithCallback(callback: (ObservableDoubleMeasurement) -> Unit) {
            registerDoubleAsynchronousInstrument(InstrumentType.OBSERVABLE_UP_DOWN_SUM, callback)
        }
    }
}
