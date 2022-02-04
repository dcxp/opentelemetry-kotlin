/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.opentelemetry.kotlin.api.metrics.DoubleGaugeBuilder
import io.opentelemetry.kotlin.api.metrics.LongGaugeBuilder
import io.opentelemetry.kotlin.api.metrics.ObservableLongMeasurement
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentType
import io.opentelemetry.kotlin.sdk.metrics.internal.state.MeterProviderSharedState
import io.opentelemetry.kotlin.sdk.metrics.internal.state.MeterSharedState

internal class SdkLongGaugeBuilder(
    meterProviderSharedState: MeterProviderSharedState,
    sharedState: MeterSharedState,
    name: String,
    description: String,
    unit: String
) :
    AbstractInstrumentBuilder<SdkLongGaugeBuilder>(
        meterProviderSharedState,
        sharedState,
        name,
        description,
        unit
    ),
    LongGaugeBuilder {
    override val `this`: SdkLongGaugeBuilder
        protected get() = this

    override fun ofDoubles(): DoubleGaugeBuilder {
        return swapBuilder(::SdkDoubleGaugeBuilder)
    }

    override fun buildWithCallback(callback: (ObservableLongMeasurement) -> Unit) {
        registerLongAsynchronousInstrument(InstrumentType.OBSERVABLE_GAUGE, callback)
    }
}
