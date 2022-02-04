/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.opentelemetry.kotlin.sdk.common.Clock
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarFilter
import io.opentelemetry.kotlin.sdk.metrics.internal.view.ViewRegistry
import io.opentelemetry.kotlin.sdk.resources.Resource

/**
 * State for a `MeterProvider`.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
interface MeterProviderSharedState {
    /** Returns the clock used for measurements. */
    val clock: Clock

    /** Returns the [Resource] to attach telemetry to. */
    val resource: Resource

    /** Returns the [ViewRegistry] for custom aggregation and metric definitions. */
    val viewRegistry: ViewRegistry

    /**
     * Returns the timestamp when this `MeterProvider` was started, in nanoseconds since Unix epoch
     * time.
     */
    val startEpochNanos: Long

    /** Returns the [ExemplarFilter] for remembering synchronous measurements. */
    val exemplarFilter: ExemplarFilter

    companion object {
        fun create(
            clock: Clock,
            resource: Resource,
            viewRegistry: ViewRegistry,
            exemplarFilter: ExemplarFilter
        ): MeterProviderSharedState {
            return Instance(clock, resource, viewRegistry, clock.now(), exemplarFilter)
        }

        private data class Instance(
            override val clock: Clock,
            override val resource: Resource,
            override val viewRegistry: ViewRegistry,
            override val startEpochNanos: Long,
            override val exemplarFilter: ExemplarFilter
        ) : MeterProviderSharedState
    }
}
