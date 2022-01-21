/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.internal

/**
 * An immutable set of key-value pairs.
 *
 * Key-value pairs are dropped for `null` or empty keys.
 *
 * Note: for subclasses of this, null keys will be removed, but if your key has another concept of
 * being "empty", you'll need to remove them before calling the constructor, assuming you don't want
 * the "empty" keys to be kept in your collection.
 *
 * @param <V> The type of the values contained in this. </V>
 */
@Suppress("UNCHECKED_CAST")
abstract class ImmutableKeyValuePairs<K, V>
/**
 * Stores the raw object data directly. Does not do any de-duping or sorting. If you use this
 * constructor, you *must* guarantee that the data has been de-duped and sorted by key before it is
 * passed here.
 */
protected constructor(private val data: Array<Any>) {
    /**
     * Sorts and dedupes the key/value pairs in `data`. `null` values will be removed. Keys will be
     * compared with the given [Comparator].
     */
    protected constructor(
        data: Array<Any?>,
        keyComparator: Comparator<*>
    ) : this(sortAndFilter(data, keyComparator))

    // TODO: Improve this to avoid one allocation, for the moment only some Builders and the asMap
    //  calls this.
    protected fun data(): List<Any> {
        return data.toList()
    }

    val size: Int
        get() = data.size / 2

    open fun isEmpty(): Boolean {
        return data.isEmpty()
    }

    fun asMap(): Map<K, V> {
        return ReadOnlyArrayMap.wrap(data())
    }

    /** Returns the value for the given `key`, or `null` if the key is not present. */
    operator fun get(key: K): V? {
        if (key == null) {
            return null
        }
        var i = 0
        while (i < data.size) {
            if (key == data[i]) {
                return data[i + 1] as V
            }
            i += 2
        }
        return null
    }

    /** Iterates over all the key-value pairs of labels contained by this instance. */
    fun forEach(consumer: (K, V) -> Unit) {
        var i = 0
        while (i < data.size) {
            consumer(data[i] as K, data[i + 1] as V)
            i += 2
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ImmutableKeyValuePairs<*, *>

        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = 1
        result *= 1000003
        result = result xor data.contentHashCode()
        return result
    }

    override fun toString(): String {
        val sb = StringBuilder("{")
        var i = 0
        while (i < data.size) {

            // Quote string values
            val value = data[i + 1]
            val valueStr = if (value is String) '"'.toString() + value + '"' else value.toString()
            sb.append(data[i]).append("=").append(valueStr).append(", ")
            i += 2
        }
        // get rid of that last pesky comma
        if (sb.length > 1) {
            sb.setLength(sb.length - 2)
        }
        sb.append("}")
        return sb.toString()
    }

    companion object {

        /**
         * Sorts and dedupes the key/value pairs in `data`. `null` values will be removed. Keys will
         * be compared with the given [Comparator].
         */
        fun sortAndFilter(data: Array<Any?>, keyComparator: Comparator<*>): Array<Any> {
            require(data.size % 2 == 0) {
                "You must provide an even number of key/value pair arguments."
            }
            mergeSort(data, keyComparator)
            return dedupe(data, keyComparator)
        }

        // note: merge sort implementation cribbed from this wikipedia article:
        // https://en.wikipedia.org/wiki/Merge_sort (this is the top-down variant)
        fun mergeSort(data: Array<Any?>, keyComparator: Comparator<*>) {
            val workArray = data.copyOf()
            splitAndMerge(
                workArray,
                0,
                data.size,
                data,
                keyComparator
            ) // sort data from workArray[] into sourceArray[]
        }

        /**
         * Sort the given run of array targetArray[] using array workArray[] as a source. beginIndex
         * is inclusive; endIndex is exclusive (targetArray[endIndex] is not in the set).
         */
        fun splitAndMerge(
            workArray: Array<Any?>,
            beginIndex: Int,
            endIndex: Int,
            targetArray: Array<Any?>,
            keyComparator: Comparator<*>
        ) {
            if (endIndex - beginIndex <= 2) { // if single element in the run, it's sorted
                return
            }
            // split the run longer than 1 item into halves
            val midpoint =
                (endIndex + beginIndex) / 4 * 2 // note: due to it's being key/value pairs
            // recursively sort both runs from array targetArray[] into workArray[]
            splitAndMerge(targetArray, beginIndex, midpoint, workArray, keyComparator)
            splitAndMerge(targetArray, midpoint, endIndex, workArray, keyComparator)
            // merge the resulting runs from array workArray[] into targetArray[]
            merge(workArray, beginIndex, midpoint, endIndex, targetArray, keyComparator)
        }

        /**
         * Left source half is sourceArray[ beginIndex:middleIndex-1]. Right source half is
         * sourceArray[ middleIndex:endIndex-1]. Result is targetArray[ beginIndex:endIndex-1].
         */
        fun <K> merge(
            sourceArray: Array<Any?>,
            beginIndex: Int,
            middleIndex: Int,
            endIndex: Int,
            targetArray: Array<Any?>,
            keyComparator: Comparator<K>
        ) {
            var leftKeyIndex = beginIndex
            var rightKeyIndex = middleIndex

            // While there are elements in the left or right runs, fill in the target array from
            // left to
            // right
            var k = beginIndex
            while (k < endIndex) {

                // If left run head exists and is <= existing right run head.
                if (leftKeyIndex < middleIndex - 1 &&
                        (rightKeyIndex >= endIndex - 1 ||
                            compareToNullSafe(
                                sourceArray[leftKeyIndex] as K,
                                sourceArray[rightKeyIndex] as K,
                                keyComparator
                            ) <= 0)
                ) {
                    targetArray[k] = sourceArray[leftKeyIndex]
                    targetArray[k + 1] = sourceArray[leftKeyIndex + 1]
                    leftKeyIndex += 2
                } else {
                    targetArray[k] = sourceArray[rightKeyIndex]
                    targetArray[k + 1] = sourceArray[rightKeyIndex + 1]
                    rightKeyIndex += 2
                }
                k += 2
            }
        }

        fun <K> compareToNullSafe(key: K?, pivotKey: K?, keyComparator: Comparator<K>): Int {
            if (key == null) {
                return if (pivotKey == null) 0 else -1
            }
            return if (pivotKey == null) {
                1
            } else keyComparator.compare(key, pivotKey)
        }

        fun <K> dedupe(data: Array<Any?>, keyComparator: Comparator<K>): Array<Any> {
            var previousKey: Any? = null
            var size = 0

            // Implement the "last one in wins" behavior.
            var i = 0
            while (i < data.size) {
                val key = data[i]
                val value = data[i + 1]
                // Skip entries with key null.
                if (key == null) {
                    i += 2
                    continue
                }
                // If the previously added key is equal with the current key, we overwrite what we
                // have.
                if (previousKey != null && keyComparator.compare(key as K, previousKey as K) == 0) {
                    size -= 2
                }
                // Skip entries with null value, we do it here because we want them to overwrite and
                // remove
                // entries with same key that we already added.
                if (value == null) {
                    i += 2
                    continue
                }
                previousKey = key
                data[size++] = key
                data[size++] = value
                i += 2
            }
            return data.copyOf(size).filterNotNull().toTypedArray()
        }
    }
}
