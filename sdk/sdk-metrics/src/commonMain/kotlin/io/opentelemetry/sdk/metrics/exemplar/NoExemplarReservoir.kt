/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.sdk.metrics.exemplar

import io.opentelemetry.api.common.Attributes
import io.opentelemetry.context.Context
import io.opentelemetry.sdk.metrics.data.ExemplarData

/** Implementation of a reservoir that keeps no exemplars. */
internal class NoExemplarReservoir private constructor() :
    io.opentelemetry.sdk.metrics.exemplar.ExemplarReservoir {
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
        val INSTANCE: io.opentelemetry.sdk.metrics.exemplar.ExemplarReservoir =
            NoExemplarReservoir()
    }
}
