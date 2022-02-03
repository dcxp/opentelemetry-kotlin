/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.metrics.BoundDoubleCounter
import io.opentelemetry.kotlin.api.metrics.DoubleCounter
import io.opentelemetry.kotlin.api.metrics.DoubleCounterBuilder
import io.opentelemetry.kotlin.api.metrics.LongCounterBuilder
import io.opentelemetry.kotlin.api.metrics.ObservableDoubleMeasurement
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentType
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentValueType
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.state.BoundStorageHandle
import io.opentelemetry.kotlin.sdk.metrics.internal.state.MeterProviderSharedState
import io.opentelemetry.kotlin.sdk.metrics.internal.state.MeterSharedState
import io.opentelemetry.kotlin.sdk.metrics.internal.state.WriteableMetricStorage

internal class SdkDoubleCounter
private constructor(descriptor: InstrumentDescriptor, storage: WriteableMetricStorage) :
    AbstractInstrument(descriptor), DoubleCounter {
    private val storage: WriteableMetricStorage

    init {
        this.storage = storage
    }

    override fun add(increment: Double, attributes: Attributes, context: Context) {
        val aggregatorHandle: BoundStorageHandle = storage.bind(attributes)
        try {
            require(increment >= 0) { "Counters can only increase" }
            aggregatorHandle.recordDouble(increment, attributes, context)
        } finally {
            aggregatorHandle.release()
        }
    }

    override fun add(increment: Double, attributes: Attributes) {
        add(increment, attributes, Context.current())
    }

    override fun add(increment: Double) {
        add(increment, Attributes.empty())
    }

    override fun bind(attributes: Attributes): BoundDoubleCounter {
        return BoundInstrument(storage.bind(attributes), attributes)
    }

    internal class BoundInstrument(handle: BoundStorageHandle, attributes: Attributes) :
        BoundDoubleCounter {
        private val handle: BoundStorageHandle
        private val attributes: Attributes

        init {
            this.handle = handle
            this.attributes = attributes
        }

        override fun add(increment: Double, context: Context) {
            require(increment >= 0) { "Counters can only increase" }
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
        AbstractInstrumentBuilder<Builder>(
            meterProviderSharedState,
            sharedState,
            name,
            description,
            unit
        ),
        DoubleCounterBuilder {

        override val `this`: Builder
            get() = this

        override fun build(): SdkDoubleCounter {
            return buildSynchronousInstrument(InstrumentType.COUNTER, InstrumentValueType.DOUBLE) {
                descriptor: InstrumentDescriptor,
                storage: WriteableMetricStorage ->
                SdkDoubleCounter(descriptor, storage)
            }
        }

        override fun ofLongs(): LongCounterBuilder {
            return swapBuilder(SdkLongCounter::Builder)
        }

        override fun buildWithCallback(callback: (ObservableDoubleMeasurement) -> Unit) {
            registerDoubleAsynchronousInstrument(InstrumentType.OBSERVABLE_SUM, callback)
        }
    }
}
