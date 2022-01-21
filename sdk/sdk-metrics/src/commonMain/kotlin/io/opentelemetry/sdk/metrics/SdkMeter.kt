/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.sdk.metrics

import io.opentelemetry.api.metrics.DoubleGaugeBuilder
import io.opentelemetry.api.metrics.DoubleHistogramBuilder
import io.opentelemetry.api.metrics.LongCounterBuilder
import io.opentelemetry.api.metrics.LongUpDownCounterBuilder
import io.opentelemetry.api.metrics.Meter
import io.opentelemetry.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.sdk.metrics.data.MetricData
import io.opentelemetry.sdk.metrics.internal.export.CollectionInfo
import io.opentelemetry.sdk.metrics.internal.state.MeterProviderSharedState
import io.opentelemetry.sdk.metrics.internal.state.MeterSharedState

/** [SdkMeter] is SDK implementation of [Meter]. */
internal class SdkMeter(
    meterProviderSharedState: MeterProviderSharedState,
    instrumentationLibraryInfo: InstrumentationLibraryInfo
) : Meter {
    private val meterProviderSharedState: MeterProviderSharedState
    private val meterSharedState: MeterSharedState

    init {
        this.meterProviderSharedState = meterProviderSharedState
        meterSharedState = MeterSharedState.create(instrumentationLibraryInfo)
    }

    // Only used in testing....
    val instrumentationLibraryInfo: InstrumentationLibraryInfo
        get() = meterSharedState.instrumentationLibraryInfo

    /** Collects all the metric recordings that changed since the previous call. */
    fun collectAll(
        collectionInfo: CollectionInfo,
        epochNanos: Long,
        suppressSynchronousCollection: Boolean
    ): Collection<MetricData> {
        return meterSharedState.collectAll(
            collectionInfo,
            meterProviderSharedState,
            epochNanos,
            suppressSynchronousCollection
        )
    }

    override fun counterBuilder(name: String): LongCounterBuilder {
        return SdkLongCounter.Builder(meterProviderSharedState, meterSharedState, name)
    }

    override fun upDownCounterBuilder(name: String): LongUpDownCounterBuilder {
        return SdkLongUpDownCounter.Builder(meterProviderSharedState, meterSharedState, name)
    }

    override fun histogramBuilder(name: String): DoubleHistogramBuilder {
        return SdkDoubleHistogram.Builder(meterProviderSharedState, meterSharedState, name)
    }

    override fun gaugeBuilder(name: String): DoubleGaugeBuilder {
        return SdkDoubleGaugeBuilder(meterProviderSharedState, meterSharedState, name)
    }
}
