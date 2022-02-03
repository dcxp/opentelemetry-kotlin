/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.metrics.BoundLongUpDownCounter
import io.opentelemetry.kotlin.api.metrics.DoubleUpDownCounterBuilder
import io.opentelemetry.kotlin.api.metrics.LongUpDownCounter
import io.opentelemetry.kotlin.api.metrics.LongUpDownCounterBuilder
import io.opentelemetry.kotlin.api.metrics.ObservableLongMeasurement
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentType
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentValueType
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.state.BoundStorageHandle
import io.opentelemetry.kotlin.sdk.metrics.internal.state.MeterProviderSharedState
import io.opentelemetry.kotlin.sdk.metrics.internal.state.MeterSharedState
import io.opentelemetry.kotlin.sdk.metrics.internal.state.WriteableMetricStorage

internal class SdkLongUpDownCounter
private constructor(descriptor: InstrumentDescriptor, storage: WriteableMetricStorage) :
    AbstractInstrument(descriptor), LongUpDownCounter {
    private val storage: WriteableMetricStorage

    init {
        this.storage = storage
    }

    override fun add(increment: Long, attributes: Attributes, context: Context) {
        storage.recordLong(increment, attributes, context)
    }

    override fun add(increment: Long, attributes: Attributes) {
        add(increment, attributes, Context.current())
    }

    override fun add(increment: Long) {
        add(increment, Attributes.empty())
    }

    override fun bind(attributes: Attributes): BoundLongUpDownCounter {
        return BoundInstrument(storage.bind(attributes), attributes)
    }

    internal class BoundInstrument(handle: BoundStorageHandle, attributes: Attributes) :
        BoundLongUpDownCounter {
        private val handle: BoundStorageHandle
        private val attributes: Attributes

        init {
            this.handle = handle
            this.attributes = attributes
        }

        override fun add(increment: Long, context: Context) {
            handle.recordLong(increment, attributes, context)
        }

        override fun add(increment: Long) {
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
        LongUpDownCounterBuilder {
        constructor(
            meterProviderSharedState: MeterProviderSharedState,
            meterSharedState: MeterSharedState,
            name: String
        ) : this(meterProviderSharedState, meterSharedState, name, "", "1")

        override val `this`: Builder
            get() = this

        override fun build(): LongUpDownCounter {
            return buildSynchronousInstrument(
                InstrumentType.UP_DOWN_COUNTER,
                InstrumentValueType.LONG
            ) { descriptor: InstrumentDescriptor, storage: WriteableMetricStorage ->
                SdkLongUpDownCounter(descriptor, storage)
            }
        }

        override fun ofDoubles(): DoubleUpDownCounterBuilder {
            return swapBuilder {
                meterProviderSharedState: MeterProviderSharedState,
                sharedState: MeterSharedState,
                name: String,
                description: String,
                unit: String ->
                SdkDoubleUpDownCounter.Builder(
                    meterProviderSharedState,
                    sharedState,
                    name,
                    description,
                    unit
                )
            }
        }

        override fun buildWithCallback(callback: (ObservableLongMeasurement) -> Unit) {
            registerLongAsynchronousInstrument(InstrumentType.OBSERVABLE_UP_DOWN_SUM, callback)
        }
    }
}
