/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.metrics.BoundDoubleHistogram
import io.opentelemetry.kotlin.api.metrics.DoubleHistogram
import io.opentelemetry.kotlin.api.metrics.DoubleHistogramBuilder
import io.opentelemetry.kotlin.api.metrics.LongHistogramBuilder
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentType
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentValueType
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.state.BoundStorageHandle
import io.opentelemetry.kotlin.sdk.metrics.internal.state.MeterProviderSharedState
import io.opentelemetry.kotlin.sdk.metrics.internal.state.MeterSharedState
import io.opentelemetry.kotlin.sdk.metrics.internal.state.WriteableMetricStorage

internal class SdkDoubleHistogram
private constructor(descriptor: InstrumentDescriptor, storage: WriteableMetricStorage) :
    AbstractInstrument(descriptor), DoubleHistogram {
    private val storage: WriteableMetricStorage

    init {
        this.storage = storage
    }

    override fun record(value: Double, attributes: Attributes, context: Context) {
        storage.recordDouble(value, attributes, context)
    }

    override fun record(value: Double, attributes: Attributes) {
        record(value, attributes, Context.current())
    }

    override fun record(value: Double) {
        record(value, Attributes.empty())
    }

    override fun bind(attributes: Attributes): BoundDoubleHistogram {
        return BoundInstrument(storage.bind(attributes), attributes)
    }

    internal class BoundInstrument(handle: BoundStorageHandle, attributes: Attributes) :
        BoundDoubleHistogram {
        private val aggregatorHandle: BoundStorageHandle
        private val attributes: Attributes

        init {
            aggregatorHandle = handle
            this.attributes = attributes
        }

        override fun record(value: Double, context: Context) {
            aggregatorHandle.recordDouble(value, attributes, context)
        }

        override fun record(value: Double) {
            record(value, Context.current())
        }

        override fun unbind() {
            aggregatorHandle.release()
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
        DoubleHistogramBuilder {
        constructor(
            meterProviderSharedState: MeterProviderSharedState,
            meterSharedState: MeterSharedState,
            name: String
        ) : this(meterProviderSharedState, meterSharedState, name, "", "1")

        override val `this`: Builder
            get() = this

        override fun build(): SdkDoubleHistogram {
            return buildSynchronousInstrument(
                InstrumentType.HISTOGRAM,
                InstrumentValueType.DOUBLE
            ) { descriptor: InstrumentDescriptor, storage: WriteableMetricStorage ->
                SdkDoubleHistogram(descriptor, storage)
            }
        }

        override fun ofLongs(): LongHistogramBuilder {
            return swapBuilder {
                meterProviderSharedState: MeterProviderSharedState,
                sharedState: MeterSharedState,
                name: String,
                description: String,
                unit: String ->
                SdkLongHistogram.Builder(
                    meterProviderSharedState,
                    sharedState,
                    name,
                    description,
                    unit
                )
            }
        }
    }
}
