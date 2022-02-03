/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.exemplar

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.metrics.data.ExemplarData

/** Implementation of a reservoir that keeps no exemplars. */
internal class NoExemplarReservoir private constructor() :
    io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarReservoir {
    override fun offerMeasurement(value: Long, attributes: Attributes, context: Context) {
        // Stores nothing
    }

    override fun offerMeasurement(value: Double, attributes: Attributes, context: Context) {
        // Stores nothing.
    }

    override fun collectAndReset(pointAttributes: Attributes): List<ExemplarData> {
        return emptyList()
    }

    companion object {
        val INSTANCE: io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarReservoir =
            NoExemplarReservoir()
    }
}
