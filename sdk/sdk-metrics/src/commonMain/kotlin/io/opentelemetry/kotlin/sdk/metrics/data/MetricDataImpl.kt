/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.data

import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.resources.Resource

data class MetricDataImpl(
    override val resource: Resource,
    override val instrumentationLibraryInfo: InstrumentationLibraryInfo,
    override val name: String,
    override val description: String,
    override val unit: String,
    override val type: MetricDataType,
    override val data: Data<*>
) : MetricData {
    companion object {
        fun create(
            resource: Resource,
            instrumentationLibraryInfo: InstrumentationLibraryInfo,
            name: String,
            description: String,
            unit: String,
            type: MetricDataType,
            data: Data<*>
        ): MetricDataImpl {
            return MetricDataImpl(
                resource,
                instrumentationLibraryInfo,
                name,
                description,
                unit,
                type,
                data
            )
        }
    }
}
