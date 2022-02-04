/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.Aggregator

/** Utilities to help deal w/ `Map<Attributes, Accumulation>` in metric storage. */
internal object MetricStorageUtils {
    /** The max number of metric accumulations for a particular [MetricStorage]. */
    const val MAX_ACCUMULATIONS = 2000

    /**
     * Merges accumulations from `toMerge` into `result`. Keys from `result` which don't appear in
     * `toMerge` are removed.
     *
     * Note: This mutates the result map.
     */
    fun <T> mergeInPlace(
        result: MutableMap<Attributes, T>,
        toMerge: Map<Attributes, T>,
        aggregator: Aggregator<T>
    ) {
        blend(result, toMerge, aggregator::merge)
    }

    /**
     * Diffs accumulations from `toMerge` into `result`. Keys from `result` which don't appear in
     * `toMerge` are removed.
     *
     * If no prior value is found, then the value from `toDiff` is used.
     *
     * Note: This mutates the result map.
     */
    fun <T> diffInPlace(
        result: MutableMap<Attributes, T>,
        toDiff: Map<Attributes, T>,
        aggregator: Aggregator<T>
    ) {
        blend(result, toDiff, aggregator::diff)
    }

    private fun <T> blend(
        result: MutableMap<Attributes, T>,
        toMerge: Map<Attributes, T>,
        blendFunction: (T, T) -> T
    ) {
        result.entries.removeAll { entity -> !toMerge.containsKey(entity.key) }
        toMerge.forEach { entity ->
            result[entity.key].let { originalValue ->
                val newValue =
                    if (originalValue == null) {
                        entity.value
                    } else {
                        blendFunction(originalValue, entity.value)
                    }
                newValue?.let { result[entity.key] = it }
            }
        }
    }
}
