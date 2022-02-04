/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

/** [GaugeData] recorded uses `long`s. */
interface LongGaugeData : GaugeData<LongPointData> {

    companion object {
        val EMPTY = create(emptyList<LongPointData>())
        fun create(points: Collection<LongPointData>): LongGaugeData {
            return Implementation(points)
        }
        data class Implementation(override val points: Collection<LongPointData>) : LongGaugeData
    }
}
