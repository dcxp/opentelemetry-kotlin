/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.view

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.metrics.internal.view.AttributesProcessor
import io.opentelemetry.kotlin.sdk.metrics.internal.view.StringPredicates

/** Builder of metric [View]s. */
class ViewBuilder internal constructor() {
    private var name: String? = null

    private var description: String? = null

    private var aggregation: Aggregation = Aggregation.defaultAggregation()
    private var processor: AttributesProcessor = AttributesProcessor.noop()

    /**
     * sets the name of the resulting metric.
     *
     * @param name metric name or `null` if the underlying instrument name should be used.
     * @return this Builder.
     */
    fun setName(name: String): ViewBuilder {
        this.name = name
        return this
    }

    /**
     * sets the name of the resulting metric.
     *
     * @param description metric description or `null` if the underlying instrument description
     * should be used.
     * @return this Builder.
     */
    fun setDescription(description: String): ViewBuilder {
        this.description = description
        return this
    }

    /**
     * sets [Aggregation].
     *
     * @param aggregation aggregation to use.
     * @return this Builder.
     */
    fun setAggregation(aggregation: Aggregation): ViewBuilder {
        this.aggregation = aggregation
        return this
    }

    /**
     * Specify the attributes processor for this view.
     *
     * Note: This resets all attribute filters, baggage appending and other processing.
     *
     * Visible for testing.
     *
     * @param processor The pre-processor for measurement attributes.
     * @return this Builder.
     */
    fun setAttributesProcessor(processor: AttributesProcessor): ViewBuilder {
        this.processor = processor
        return this
    }

    /**
     * Filters measurement attributes using a given filter.
     *
     * Note: This runs after all other attribute processing added so far.
     *
     * @param keyFilter filter for key names to include.
     * @return this Builder.
     */
    fun filterAttributes(keyFilter: (String) -> Boolean): ViewBuilder {
        processor = processor.then(AttributesProcessor.filterByKeyName(keyFilter))
        return this
    }

    /**
     * Filters measurement attributes using a given regex.
     *
     * Note: This runs after all other attribute processing added so far.
     *
     * @param keyPattern the regular expression for selecting attributes by key name.
     * @return this Builder.
     */
    fun filterAttributes(keyPattern: Regex): ViewBuilder {
        processor =
            processor.then(AttributesProcessor.filterByKeyName(StringPredicates.regex(keyPattern)))
        return this
    }

    /**
     * Appends a static set of attributes to all measurements.
     *
     * Note: This runs after all other attribute processing added so far.
     *
     * @param extraAttributes The static attributes to append to measurements.
     * @return this Builder.
     */
    fun appendAttributes(extraAttributes: Attributes): ViewBuilder {
        processor = processor.then(AttributesProcessor.append(extraAttributes))
        return this
    }

    /**
     * Appends key-values from baggage to all measurements.
     *
     * Note: This runs after all other attribute processing added so far.
     *
     * @param keyFilter Only baggage key values pairs where the key matches this predicate will be
     * appended.
     * @return this Builder.
     */
    fun appendFilteredBaggageAttributes(keyFilter: (String) -> Boolean): ViewBuilder {
        processor = processor.then(AttributesProcessor.appendBaggageByKeyName(keyFilter))
        return this
    }

    /**
     * Appends key-values from baggage to all measurements.
     *
     * Note: This runs after all other attribute processing added so far.
     *
     * @param keyPattern Only baggage key values pairs where the key matches this regex will be
     * appended.
     * @return this Builder.
     */
    fun appendFilteredBaggageAttributesByPattern(keyPattern: Regex): ViewBuilder {
        processor =
            processor.then(
                AttributesProcessor.appendBaggageByKeyName(StringPredicates.regex(keyPattern))
            )
        return this
    }

    /**
     * Appends all key-values from baggage to all measurements.
     *
     * Note: This runs after all other attribute processing added so far.
     *
     * @return this Builder.
     */
    fun appendAllBaggageAttributes(): ViewBuilder {
        return appendFilteredBaggageAttributes(StringPredicates.ALL)
    }

    /** Returns the resulting [View]. */
    fun build(): View {
        return View.Companion.create(name, description, aggregation, processor)
    }
}
