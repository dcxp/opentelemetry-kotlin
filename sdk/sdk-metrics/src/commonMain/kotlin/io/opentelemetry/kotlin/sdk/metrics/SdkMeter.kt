/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.opentelemetry.kotlin.api.metrics.DoubleGaugeBuilder
import io.opentelemetry.kotlin.api.metrics.DoubleHistogramBuilder
import io.opentelemetry.kotlin.api.metrics.LongCounterBuilder
import io.opentelemetry.kotlin.api.metrics.LongUpDownCounterBuilder
import io.opentelemetry.kotlin.api.metrics.Meter
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.data.MetricData
import io.opentelemetry.kotlin.sdk.metrics.internal.export.CollectionInfo
import io.opentelemetry.kotlin.sdk.metrics.internal.state.MeterProviderSharedState
import io.opentelemetry.kotlin.sdk.metrics.internal.state.MeterSharedState

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
