/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

/**
 * A summary metric point.
 *
 * See:
 * https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/metrics/datamodel.md#summary
 *
 * *Note: This is called "DoubleSummary" to reflect which primitives are used to record it, however
 * "Summary" is the equivalent OTLP type.*
 *
 * Summary is considered a legacy metric type, and shouldn't be produced (by default) from
 * instruments.
 */
interface DoubleSummaryData : Data<DoubleSummaryPointData> {
    companion object {
        val EMPTY = create(emptyList<DoubleSummaryPointData>())
        fun create(points: Collection<DoubleSummaryPointData>): DoubleSummaryData {
            return Implementation(points)
        }
        data class Implementation(override val points: Collection<DoubleSummaryPointData>) :
            DoubleSummaryData
    }
}
