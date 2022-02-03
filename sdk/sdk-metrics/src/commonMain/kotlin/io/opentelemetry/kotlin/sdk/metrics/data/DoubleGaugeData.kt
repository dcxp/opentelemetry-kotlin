/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

/** [GaugeData] recorded uses `double`s. */
interface DoubleGaugeData : GaugeData<DoublePointData> {

    companion object {
        val EMPTY = create(emptyList())
        fun create(points: Collection<DoublePointData>): DoubleGaugeData {
            return Implementation(points)
        }
        data class Implementation(override val points: Collection<DoublePointData>) :
            DoubleGaugeData
    }
}
