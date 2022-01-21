/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.baggage

import io.opentelemetry.context.Context
import io.opentelemetry.context.ImplicitContextKeyed

/**
 * A map from [String] to [BaggageEntry] that can be used to label anything that is associated with
 * a specific operation.
 *
 * For example, `Baggage`s can be used to label stats, log messages, or debugging information.
 *
 * Implementations of this interface *must* be immutable and have well-defined value-based
 * equals/hashCode implementations. If an implementation does not strictly conform to these
 * requirements, behavior of the OpenTelemetry APIs and default SDK cannot be guaranteed.
 *
 * For this reason, it is strongly suggested that you use the implementation that is provided here
 * via the factory methods and the [BaggageBuilder].
 */
interface Baggage : ImplicitContextKeyed {
    override fun storeInContext(context: Context): Context {
        return context.with(BaggageContextKey.KEY, this)
    }

    /** Returns the number of entries in this [Baggage]. */
    val size: Int

    /** Returns whether this [Baggage] is empty, containing no entries. */
    fun isEmpty(): Boolean {
        return size == 0
    }

    /** Iterates over all the entries in this [Baggage]. */
    fun forEach(consumer: (String, BaggageEntry) -> Unit)

    fun forEach(consumer: (Map.Entry<String, BaggageEntry>) -> Unit)

    /** Returns a read-only view of this [Baggage] as a [Map]. */
    fun asMap(): Map<String, BaggageEntry>

    /**
     * Returns the `String` value associated with the given key, without metadata.
     *
     * @param entryKey entry key to return the value for.
     * @return the value associated with the given key, or `null` if no `Entry` with the given
     * `entryKey` is in this `Baggage`.
     */
    fun getEntryValue(entryKey: String): String?

    /**
     * Create a Builder pre-initialized with the contents of this Baggage. The returned Builder will
     * be set to not use an implicit parent, so any parent assignment must be done manually.
     */
    fun toBuilder(): BaggageBuilder

    companion object {
        /** Baggage with no entries. */
        fun empty(): Baggage {
            return ImmutableBaggage.empty()
        }

        /** Creates a new [BaggageBuilder] for creating Baggage. */
        fun builder(): BaggageBuilder {
            return ImmutableBaggage.builder()
        }

        /**
         * Returns Baggage from the current [Context], falling back to empty Baggage if none is in
         * the current Context.
         */
        fun current(): Baggage {
            return fromContext(Context.current())
        }

        /**
         * Returns the [Baggage] from the specified [Context], falling back to a empty [ ] if there
         * is no baggage in the context.
         */
        fun fromContext(context: Context): Baggage {
            val baggage = context.get<Baggage>(BaggageContextKey.KEY)
            return baggage ?: empty()
        }

        /**
         * Returns the [Baggage] from the specified [Context], or `null` if there is no baggage in
         * the context.
         */
        fun fromContextOrNull(context: Context): Baggage? {
            return context.get<Baggage>(BaggageContextKey.KEY)
        }
    }
}
