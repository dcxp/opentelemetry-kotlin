/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.view

import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentType
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarFilter
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.Aggregator
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor

/** Aggregation that selects the specified default based on instrument. */
internal class DefaultAggregation private constructor() : Aggregation() {
    override fun <T> createAggregator(
        instrumentDescriptor: InstrumentDescriptor,
        exemplarFilter: ExemplarFilter
    ): Aggregator<T> {
        return resolve(instrumentDescriptor).createAggregator(instrumentDescriptor, exemplarFilter)
    }

    override fun toString(): String {
        return "DefaultAggregation"
    }

    companion object {
        val INSTANCE: Aggregation = DefaultAggregation()
        /*private val logger: ThrottlingLogger =
        ThrottlingLogger(
            java.util.logging.Logger.getLogger(DefaultAggregation::class.java.getName())
        )*/

        private fun resolve(instrument: InstrumentDescriptor): Aggregation {
            when (instrument.type) {
                InstrumentType.COUNTER,
                InstrumentType.UP_DOWN_COUNTER,
                InstrumentType.OBSERVABLE_SUM,
                InstrumentType.OBSERVABLE_UP_DOWN_SUM -> return SumAggregation.DEFAULT
                InstrumentType.HISTOGRAM -> return ExplicitBucketHistogramAggregation.DEFAULT
                InstrumentType.OBSERVABLE_GAUGE -> return LastValueAggregation.INSTANCE
            }
            /*logger.log(
                java.util.logging.Level.WARNING,
                "Unable to find default aggregation for instrument: $instrument"
            )*/
            return NoAggregation.INSTANCE
        }
    }
}
