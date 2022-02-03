/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

/** [SumData] recorded uses `long`s. */
interface LongSumData : SumData<LongPointData> {
    companion object {
        val EMPTY =
            create(
                /* isMonotonic= */ false,
                AggregationTemporality.CUMULATIVE,
                emptyList<LongPointData>()
            )

        fun create(
            isMonotonic: Boolean,
            temporality: AggregationTemporality,
            points: Collection<LongPointData>
        ): LongSumData {
            return Implementation(points, isMonotonic, temporality)
        }
        data class Implementation(
            override val points: Collection<LongPointData>,
            override val isMonotonic: Boolean,
            override val aggregationTemporality: AggregationTemporality
        ) : LongSumData
    }
}
