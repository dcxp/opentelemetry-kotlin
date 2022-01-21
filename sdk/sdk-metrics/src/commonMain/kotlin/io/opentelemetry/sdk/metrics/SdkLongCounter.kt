/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.sdk.metrics

import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.metrics.BoundLongCounter
import io.opentelemetry.api.metrics.DoubleCounterBuilder
import io.opentelemetry.api.metrics.LongCounter
import io.opentelemetry.api.metrics.LongCounterBuilder
import io.opentelemetry.api.metrics.ObservableLongMeasurement
import io.opentelemetry.context.Context
import io.opentelemetry.sdk.metrics.common.InstrumentType
import io.opentelemetry.sdk.metrics.common.InstrumentValueType
import io.opentelemetry.sdk.metrics.internal.descriptor.InstrumentDescriptor
import io.opentelemetry.sdk.metrics.internal.state.BoundStorageHandle
import io.opentelemetry.sdk.metrics.internal.state.MeterProviderSharedState
import io.opentelemetry.sdk.metrics.internal.state.MeterSharedState
import io.opentelemetry.sdk.metrics.internal.state.WriteableMetricStorage

internal class SdkLongCounter
private constructor(descriptor: InstrumentDescriptor, storage: WriteableMetricStorage) :
    AbstractInstrument(descriptor), LongCounter {
    private val storage: WriteableMetricStorage

    init {
        this.storage = storage
    }

    override fun add(increment: Long, attributes: Attributes, context: Context) {
        require(increment >= 0) { "Counters can only increase" }
        storage.recordLong(increment, attributes, context)
    }

    override fun add(increment: Long, attributes: Attributes) {
        add(increment, attributes, Context.current())
    }

    override fun add(increment: Long) {
        add(increment, Attributes.empty())
    }

    override fun bind(attributes: Attributes): BoundLongCounter {
        return BoundInstrument(storage.bind(attributes), attributes)
    }

    internal class BoundInstrument(handle: BoundStorageHandle, attributes: Attributes) :
        BoundLongCounter {
        private val handle: BoundStorageHandle
        private val attributes: Attributes

        init {
            this.handle = handle
            this.attributes = attributes
        }

        override fun add(increment: Long, context: Context) {
            require(increment >= 0) { "Counters can only increase" }
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
        AbstractInstrumentBuilder<Builder>(
            meterProviderSharedState,
            sharedState,
            name,
            description,
            unit
        ),
        LongCounterBuilder {
        constructor(
            meterProviderSharedState: MeterProviderSharedState,
            meterSharedState: MeterSharedState,
            name: String
        ) : this(meterProviderSharedState, meterSharedState, name, "", "1")

        override val `this`: Builder
            get() = this

        override fun build(): SdkLongCounter {
            return buildSynchronousInstrument(InstrumentType.COUNTER, InstrumentValueType.LONG) {
                descriptor: InstrumentDescriptor,
                storage: WriteableMetricStorage ->
                SdkLongCounter(descriptor, storage)
            }
        }

        override fun ofDoubles(): DoubleCounterBuilder {
            return swapBuilder {
                meterProviderSharedState: MeterProviderSharedState,
                sharedState: MeterSharedState,
                name: String,
                description: String,
                unit: String ->
                SdkDoubleCounter.Builder(
                    meterProviderSharedState,
                    sharedState,
                    name,
                    description,
                    unit
                )
            }
        }

        override fun buildWithCallback(callback: (ObservableLongMeasurement) -> Unit) {
            registerLongAsynchronousInstrument(InstrumentType.OBSERVABLE_SUM, callback)
        }
    }
}
