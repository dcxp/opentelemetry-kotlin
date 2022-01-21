package io.opentelemetry.api

/**
 * Creates a tuple of type [Map.Entry] from this and [that].
 *
 * This can be useful for creating [Map] literals with less noise, for example:
 * @sample samples.collections.Maps.Instantiation.mapFromPairs
 */
infix fun <A, B> A.entryTo(that: B): Map.Entry<A, B> {
    val pair: Pair<A, B> = this to that
    return mapOf(pair).entries.single()
}
