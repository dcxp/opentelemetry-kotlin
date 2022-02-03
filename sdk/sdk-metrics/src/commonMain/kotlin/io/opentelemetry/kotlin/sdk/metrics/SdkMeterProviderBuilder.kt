/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.opentelemetry.kotlin.api.common.normalizeToNanos
import io.opentelemetry.kotlin.api.metrics.GlobalMeterProvider
import io.opentelemetry.kotlin.sdk.common.Clock
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarFilter
import io.opentelemetry.kotlin.sdk.metrics.export.MetricReaderFactory
import io.opentelemetry.kotlin.sdk.metrics.internal.view.ViewRegistry
import io.opentelemetry.kotlin.sdk.metrics.internal.view.ViewRegistryBuilder
import io.opentelemetry.kotlin.sdk.metrics.view.InstrumentSelector
import io.opentelemetry.kotlin.sdk.metrics.view.View
import io.opentelemetry.kotlin.sdk.resources.Resource
import kotlinx.datetime.DateTimeUnit
import kotlin.time.Duration
import kotlin.time.DurationUnit

/** Builder class for the [SdkMeterProvider]. */
class SdkMeterProviderBuilder internal constructor() {
    private var clock: Clock = Clock.default
    private var resource: Resource = Resource.default
    private val viewRegistryBuilder: ViewRegistryBuilder = ViewRegistry.builder()
    private val metricReaders: MutableList<MetricReaderFactory> = mutableListOf()

    // Default the sampling strategy.
    private var exemplarFilter: ExemplarFilter = ExemplarFilter.sampleWithTraces()
    private var minimumCollectionIntervalNanos: Long =
        DateTimeUnit.MILLISECOND.normalizeToNanos(100)

    /**
     * Assign a [Clock].
     *
     * @param clock The clock to use for all temporal needs.
     * @return this
     */
    fun setClock(clock: Clock): SdkMeterProviderBuilder {
        this.clock = clock
        return this
    }

    /**
     * Assign a [Resource] to be attached to all metrics created by Meters.
     *
     * @param resource A Resource implementation.
     * @return this
     */
    fun setResource(resource: Resource): SdkMeterProviderBuilder {
        this.resource = resource
        return this
    }

    /**
     * Assign an [ExemplarFilter] for all metrics created by Meters.
     *
     * @return this
     */
    fun setExemplarFilter(filter: ExemplarFilter): SdkMeterProviderBuilder {
        exemplarFilter = filter
        return this
    }

    /**
     * Register a view with the given [InstrumentSelector].
     *
     * Example on how to register a view:
     *
     * <pre>`// create a SdkMeterProviderBuilder SdkMeterProviderBuilder meterProviderBuilder =
     * SdkMeterProvider.builder();
     *
     * // create a selector to select which instruments to customize: InstrumentSelector
     * instrumentSelector = InstrumentSelector.builder() .setInstrumentType(InstrumentType.COUNTER)
     * .build();
     *
     * // create a specification of how you want the metrics aggregated: AggregatorFactory
     * aggregatorFactory = AggregatorFactory.minMaxSumCount();
     *
     * // register the view with the SdkMeterProviderBuilder
     * meterProviderBuilder.registerView(instrumentSelector, View.builder()
     * .setAggregatorFactory(aggregatorFactory).build()); `</pre> *
     *
     * @since 1.1.0
     */
    fun registerView(selector: InstrumentSelector, view: View): SdkMeterProviderBuilder {
        viewRegistryBuilder.addView(selector, view)
        return this
    }

    /**
     * Returns a new [SdkMeterProvider] built with the configuration of this [ ] and registers it as
     * the global [ ].
     *
     * @see GlobalMeterProvider
     */
    fun buildAndRegisterGlobal(): SdkMeterProvider {
        val meterProvider: SdkMeterProvider = build()
        GlobalMeterProvider.set(meterProvider)
        return meterProvider
    }

    /**
     * Registers a [MetricReader] for this SDK.
     *
     * @param reader The factory for a reader of metrics.
     * @return this
     */
    fun registerMetricReader(reader: MetricReaderFactory): SdkMeterProviderBuilder {
        metricReaders.add(reader)
        return this
    }

    /**
     * Configure the minimum duration between synchronous collections. If collections occur more
     * frequently than this, synchronous collection will be suppressed.
     *
     * @param duration The duration.
     * @return this
     */
    fun setMinimumCollectionInterval(duration: Duration): SdkMeterProviderBuilder {
        require(!duration.isNegative()) { "duration must not be negative" }
        minimumCollectionIntervalNanos = duration.toLong(DurationUnit.NANOSECONDS)
        return this
    }

    /**
     * Returns a new [SdkMeterProvider] built with the configuration of this [ ]. This provider is
     * not registered as the global [ ]. It is recommended that you register one provider using
     * [SdkMeterProviderBuilder.buildAndRegisterGlobal] for use by instrumentation when that
     * requires access to a global instance of [ ].
     *
     * @see GlobalMeterProvider
     */
    fun build(): SdkMeterProvider {
        return SdkMeterProvider(
            metricReaders,
            clock,
            resource,
            viewRegistryBuilder.build(),
            exemplarFilter,
            minimumCollectionIntervalNanos
        )
    }
}
