/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.exemplar

import io.opentelemetry.kotlin.Supplier
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.Clock
import kotlinx.atomicfu.atomic
import kotlin.random.Random

/**
 * A Reservoir sampler with fixed size that stores the given number of exemplars.
 *
 * This implementation uses a un-unweighted/naive algorithm for sampler where the probability of
 * sampling decrease as the number of observations continue. The collectAndReset method resets the
 * count of observations, making the probability of sampling effectively 1.0.
 *
 * Additionally this implementation ONLY exports double valued exemplars.
 */
internal class FixedSizeExemplarReservoir(
    clock: Clock,
    size: Int,
    private val randomSupplier: Supplier<Random>
) : AbstractFixedSizeExemplarReservoir(clock, size) {
    private val numMeasurements = atomic(0)

    override fun reservoirIndexFor(value: Double, attributes: Attributes, context: Context): Int {
        val count: Int = numMeasurements.incrementAndGet()
        val index: Int = randomSupplier.get().nextInt(if (count > 0) count else 1)
        return if (index < maxSize()) {
            index
        } else -1
    }

    override fun reset() {
        // Reset the count so exemplars are likely to be filled.
        numMeasurements.lazySet(0)
    }
}
