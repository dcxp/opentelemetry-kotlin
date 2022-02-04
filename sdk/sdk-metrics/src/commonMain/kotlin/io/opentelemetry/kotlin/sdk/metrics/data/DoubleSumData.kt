/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

/** [SumData] recorded uses `double`s. */
interface DoubleSumData : SumData<DoublePointData> {
    companion object {
        val EMPTY = create(false, AggregationTemporality.CUMULATIVE, emptyList())

        fun create(
            isMonotonic: Boolean,
            temporality: AggregationTemporality,
            points: Collection<DoublePointData>
        ): DoubleSumData {
            return Implementation(points, isMonotonic, temporality)
        }

        data class Implementation(
            override val points: Collection<DoublePointData>,
            override val isMonotonic: Boolean,
            override val aggregationTemporality: AggregationTemporality
        ) : DoubleSumData
    }
}
