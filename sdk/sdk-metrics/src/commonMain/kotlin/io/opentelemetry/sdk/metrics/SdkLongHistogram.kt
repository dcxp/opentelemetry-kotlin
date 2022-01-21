/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.sdk.metrics

import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.metrics.BoundLongHistogram
import io.opentelemetry.api.metrics.DoubleHistogramBuilder
import io.opentelemetry.api.metrics.LongHistogram
import io.opentelemetry.api.metrics.LongHistogramBuilder
import io.opentelemetry.context.Context
import io.opentelemetry.sdk.metrics.common.InstrumentType
import io.opentelemetry.sdk.metrics.common.InstrumentValueType
import io.opentelemetry.sdk.metrics.internal.descriptor.InstrumentDescriptor
import io.opentelemetry.sdk.metrics.internal.state.BoundStorageHandle
import io.opentelemetry.sdk.metrics.internal.state.MeterProviderSharedState
import io.opentelemetry.sdk.metrics.internal.state.MeterSharedState
import io.opentelemetry.sdk.metrics.internal.state.WriteableMetricStorage

internal class SdkLongHistogram
private constructor(descriptor: InstrumentDescriptor, storage: WriteableMetricStorage) :
    AbstractInstrument(descriptor), LongHistogram {
    private val storage: WriteableMetricStorage

    init {
        this.storage = storage
    }

    override fun record(value: Long, attributes: Attributes, context: Context) {
        storage.recordLong(value, attributes, context)
    }

    override fun record(value: Long, attributes: Attributes) {
        record(value, attributes, Context.current())
    }

    override fun record(value: Long) {
        record(value, Attributes.empty())
    }

    override fun bind(attributes: Attributes): BoundLongHistogram {
        return BoundInstrument(storage.bind(attributes), attributes)
    }

    internal class BoundInstrument(handle: BoundStorageHandle, attributes: Attributes) :
        BoundLongHistogram {
        private val handle: BoundStorageHandle
        private val attributes: Attributes

        init {
            this.handle = handle
            this.attributes = attributes
        }

        override fun record(value: Long, context: Context) {
            handle.recordLong(value, attributes, context)
        }

        override fun record(value: Long) {
            record(value, Context.current())
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
        io.opentelemetry.sdk.metrics.AbstractInstrumentBuilder<Builder>(
            meterProviderSharedState,
            sharedState,
            name,
            description,
            unit
        ),
        LongHistogramBuilder {
        override val `this`: Builder
            protected get() = this

        override fun build(): SdkLongHistogram {
            return buildSynchronousInstrument(InstrumentType.HISTOGRAM, InstrumentValueType.LONG) {
                descriptor: InstrumentDescriptor,
                storage: WriteableMetricStorage ->
                SdkLongHistogram(descriptor, storage)
            }
        }

        override fun ofDoubles(): DoubleHistogramBuilder {
            return swapBuilder(SdkDoubleHistogram::Builder)
        }
    }
}
