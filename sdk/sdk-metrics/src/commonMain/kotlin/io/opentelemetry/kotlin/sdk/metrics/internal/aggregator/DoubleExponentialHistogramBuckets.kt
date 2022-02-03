/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.aggregator

import io.opentelemetry.kotlin.sdk.metrics.Math
import io.opentelemetry.kotlin.sdk.metrics.data.ExponentialHistogramBuckets
import io.opentelemetry.kotlin.sdk.metrics.internal.state.ExponentialCounter
import io.opentelemetry.kotlin.sdk.metrics.internal.state.MapCounter
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min

/**
 * This class handles the operations for recording, scaling, and exposing data related to the
 * exponential histogram.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
internal class DoubleExponentialHistogramBuckets : ExponentialHistogramBuckets {
    private var counts: ExponentialCounter
    private var bucketMapper: BucketMapper
    var scale: Int
        private set

    constructor() {
        counts = MapCounter()
        bucketMapper = LogarithmMapper(MAX_SCALE)
        scale = MAX_SCALE
    }

    // For copying
    constructor(buckets: DoubleExponentialHistogramBuckets) {
        counts = MapCounter(buckets.counts) // copy counts
        bucketMapper = LogarithmMapper(buckets.scale)
        scale = buckets.scale
    }

    fun record(value: Double): Boolean {
        check(value != 0.0) {
            // Guarded by caller. If passed 0 it would be a bug in the SDK.
            "Illegal attempted recording of zero at bucket level."
        }
        val index: Int = bucketMapper.valueToIndex(abs(value))
        return counts.increment(index, 1)
    }

    override val offset: Int
        get() = counts.indexStart

    override val bucketCounts: List<Long>
        get() {
            if (counts.isEmpty()) {
                return emptyList()
            }
            val length: Int = counts.indexEnd - counts.indexStart + 1
            return (0L until length).toList()
        }
    override val totalCount: Long
        get() {
            var totalCount: Long = 0
            for (i in counts.indexStart..counts.indexEnd) {
                totalCount += counts[i]
            }
            return totalCount
        }

    fun downscale(by: Int) {
        if (by == 0) {
            return
        } else if (by < 0) {
            // This should never happen without an SDK bug
            throw IllegalStateException("Cannot downscale by negative amount. Was given $by.")
        }
        if (!counts.isEmpty()) {
            val newCounts: ExponentialCounter = MapCounter()
            for (i in counts.indexStart..counts.indexEnd) {
                val count: Long = counts[i]
                if (count > 0) {
                    if (!newCounts.increment(i shr by, count)) {
                        // Theoretically won't happen unless there's an overflow on index
                        throw IllegalStateException("Failed to create new downscaled buckets.")
                    }
                }
            }
            counts = newCounts
        }
        scale -= by
        bucketMapper = LogarithmMapper(scale)
    }

    /**
     * This method merges this instance with another set of buckets. It alters the underlying bucket
     * counts and scale of this instance only, so it is to be used with caution. For immutability,
     * use the static merge() method.
     *
     * The bucket counts of this instance will be added to or subtracted from depending on the
     * additive parameter.
     *
     * This algorithm for merging is adapted from NrSketch.
     *
     * @param other the histogram that will be merged into this one
     * @param additive whether the bucket counts will be added or subtracted (diff vs merge).
     */
    private fun mergeWith(other: DoubleExponentialHistogramBuckets, additive: Boolean) {
        if (other.counts.isEmpty()) {
            return
        }

        // Find the common scale, and the extended window required to merge the two bucket sets
        val commonScale: Int = min(scale, other.scale)

        // Deltas are changes in scale
        var deltaThis = scale - commonScale
        var deltaOther = other.scale - commonScale
        val newWindowStart: Long
        val newWindowEnd: Long
        if (counts.isEmpty()) {
            newWindowStart = (other.offset shr deltaOther).toLong()
            newWindowEnd = (other.counts.indexEnd shr deltaOther).toLong()
        } else {
            newWindowStart = min(offset shr deltaThis, other.offset shr deltaOther).toLong()
            newWindowEnd =
                max(counts.indexEnd shr deltaThis, other.counts.indexEnd shr deltaOther).toLong()
        }

        // downscale to fit new window
        deltaThis += getScaleReduction(newWindowStart, newWindowEnd)
        downscale(deltaThis)

        // since we changed scale of this, we need to know the new difference between the two scales
        deltaOther = other.scale - scale

        // do actual merging of other into this. Will decrement or increment depending on sign.
        val sign = if (additive) 1 else -1
        for (i in other.offset..other.counts.indexEnd) {
            if (!counts.increment(i shr deltaOther, sign * other.counts.get(i))) {
                // This should never occur if scales and windows are calculated without bugs
                throw IllegalStateException("Failed to merge exponential histogram buckets.")
            }
        }
    }

    /**
     * Returns the minimum scale reduction required to record the given value in these buckets, by
     * calculating the new required window to allow the new value to be recorded. To be used with
     * downScale().
     *
     * @param value The proposed value to be recorded.
     * @return The required scale reduction in order to fit the value in these buckets.
     */
    fun getScaleReduction(value: Double): Int {
        val index: Long = bucketMapper.valueToIndex(abs(value)).toLong()
        val newStart: Long = min(index.toUInt(), counts.indexStart.toUInt()).toLong()
        val newEnd: Long = max(index.toUInt(), counts.indexEnd.toUInt()).toLong()
        return getScaleReduction(newStart, newEnd)
    }

    fun getScaleReduction(newStart: Long, newEnd: Long): Int {
        var newStart = newStart
        var newEnd = newEnd
        var scaleReduction = 0
        while (newEnd - newStart + 1 > MAX_BUCKETS) {
            newStart = newStart shr 1
            newEnd = newEnd shr 1
            scaleReduction++
        }
        return scaleReduction
    }

    override fun equals(other: Any?): Boolean {
        if (other !is DoubleExponentialHistogramBuckets) {
            return false
        }
        val other = other
        // Don't need to compare getTotalCount() because equivalent bucket counts
        // imply equivalent overall count.
        return bucketCounts == other.bucketCounts && offset == other.offset && scale == other.scale
    }

    override fun hashCode(): Int {
        var hash = 1
        hash *= 1000003
        hash = hash xor offset
        hash *= 1000003
        hash = hash xor bucketCounts.hashCode()
        hash *= 1000003
        hash = hash xor scale
        // Don't need to hash getTotalCount() because equivalent bucket
        // counts imply equivalent overall count.
        return hash
    }

    override fun toString(): String {
        return ("DoubleExponentialHistogramBuckets{" +
            "scale: " +
            scale +
            ", offset: " +
            offset +
            ", counts: " +
            counts +
            " }")
    }

    private class LogarithmMapper(scale: Int) : BucketMapper {
        private val scaleFactor: Double

        init {
            scaleFactor = Math.scalb(1.0 / ln(2.0), scale)
        }

        override fun valueToIndex(value: Double): Int {
            return floor(ln(value) * scaleFactor).toInt()
        }
    }

    companion object {
        const val MAX_SCALE = 20
        private val MAX_BUCKETS: Int = MapCounter.MAX_SIZE

        /**
         * Return buckets a subtracted by buckets b. May perform downscaling if required.
         *
         * @param a the minuend of the subtraction.
         * @param b the subtrahend of the subtraction.
         * @return buckets a subtracted by buckets b.
         */
        fun diff(
            a: DoubleExponentialHistogramBuckets,
            b: DoubleExponentialHistogramBuckets
        ): DoubleExponentialHistogramBuckets {
            val copy = DoubleExponentialHistogramBuckets(a)
            copy.mergeWith(b, /* additive= */ false)
            return copy
        }

        /**
         * Immutable method for merging. This method copies the first set of buckets, performs the
         * merge on the copy, and returns the copy.
         *
         * @param a first buckets
         * @param b second buckets
         * @return A new set of buckets, the result
         */
        fun merge(
            a: DoubleExponentialHistogramBuckets,
            b: DoubleExponentialHistogramBuckets
        ): DoubleExponentialHistogramBuckets {
            if (b.counts.isEmpty()) {
                return DoubleExponentialHistogramBuckets(a)
            } else if (a.counts.isEmpty()) {
                return DoubleExponentialHistogramBuckets(b)
            }
            val copy = DoubleExponentialHistogramBuckets(a)
            copy.mergeWith(b, /* additive= */ true)
            return copy
        }
    }
}
