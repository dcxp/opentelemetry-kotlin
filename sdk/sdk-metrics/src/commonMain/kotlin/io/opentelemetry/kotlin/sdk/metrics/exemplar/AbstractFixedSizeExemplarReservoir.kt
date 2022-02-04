/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.kotlin.sdk.metrics.exemplar

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.trace.Span
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.Clock
import io.opentelemetry.kotlin.sdk.metrics.data.DoubleExemplarData
import io.opentelemetry.kotlin.sdk.metrics.data.ExemplarData

/**
 * Base implementation for fixed-size reservoir sampling of Exemplars.
 *
 * <p>Additionally this implementation ONLY exports double valued exemplars.
 */
abstract class AbstractFixedSizeExemplarReservoir(val clock: Clock, size: Int) : ExemplarReservoir {
    private val storage: Array<ReservoirCell>

    init {
        this.storage = (0 until size).map { ReservoirCell() }.toTypedArray()
    }

    fun maxSize(): Int {
        return storage.size
    }

    /**
     * Determines the sample reservoir index for a given measurement.
     *
     * @return The index to sample into or -1 for no sampling.
     */
    abstract fun reservoirIndexFor(value: Double, attributes: Attributes, context: Context): Int

    /** Callback to reset any local state after a {@link #collectAndReset} call. */
    open fun reset() {}

    override fun offerMeasurement(value: Long, attributes: Attributes, context: Context) {
        offerMeasurement(value.toDouble(), attributes, context)
    }

    override fun offerMeasurement(value: Double, attributes: Attributes, context: Context) {
        val bucket = reservoirIndexFor(value, attributes, context)
        if (bucket != -1) {
            this.storage[bucket].offerMeasurement(value, attributes, context, clock)
        }
    }

    override fun collectAndReset(pointAttributes: Attributes): List<ExemplarData> {
        // Note: we are collecting exemplars from buckets piecemeal, but we
        // could still be sampling exemplars during this process.
        val results = this.storage.map { cell -> cell.getAndReset(pointAttributes) }.filterNotNull()
        reset()
        return results
    }

    /**
     * A Reservoir cell pre-allocated memories for Exemplar data.
     *
     * <p>We only allocate new objects during collection. This class should NOT cause allocations
     * during sampling or within the synchronous metric hot-path.
     *
     * <p>Allocations are acceptable in the {@link #getAndReset(Attributes)} method.
     */
    private class ReservoirCell {
        private var value: Double = 0.0
        private var attributes: Attributes? = null
        private var spanId: String? = null
        private var traceId: String? = null
        private var recordTime: Long = 0

        fun offerMeasurement(
            value: Double,
            attributes: Attributes,
            context: Context,
            clock: Clock
        ) {
            this.value = value
            this.attributes = attributes
            // Note: It may make sense in the future to attempt to pull this from an active span.
            this.recordTime = clock.now()
            updateFromContext(context)
        }

        private fun updateFromContext(context: Context) {
            val current = Span.fromContext(context)
            if (current.spanContext.isValid) {
                this.spanId = current.spanContext.spanId
                this.traceId = current.spanContext.traceId
            }
        }

        fun getAndReset(pointAttributes: Attributes): ExemplarData? {
            val attributes = this.attributes
            if (attributes != null) {
                val result =
                    DoubleExemplarData.create(
                        filtered(attributes, pointAttributes),
                        recordTime,
                        spanId,
                        traceId,
                        value
                    )
                this.attributes = null
                this.value = 0.0
                this.spanId = null
                this.traceId = null
                this.recordTime = 0
                return result
            }
            return null
        }
    }

    companion object {
        /** Returns filtered attributes for exemplars. */
        private fun filtered(original: Attributes, metricPoint: Attributes): Attributes {
            if (metricPoint.isEmpty()) {
                return original
            }
            val metricPointKeys = metricPoint.asMap()
            return original.toBuilder().removeIf(metricPointKeys::contains).build()
        }
    }
}
