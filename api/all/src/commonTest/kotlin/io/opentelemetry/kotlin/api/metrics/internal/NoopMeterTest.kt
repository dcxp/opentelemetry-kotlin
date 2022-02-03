/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.kotlin.api.metrics.internal

import io.opentelemetry.kotlin.api.common.AttributeKey.Companion.stringKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import kotlin.test.Test

class NoopMeterTest {
    private val meter = NoopMeter.instance

    @Test
    fun noopLongCounter_doesNotThrow() {
        val counter =
            meter
                .counterBuilder("size")
                .setDescription("The size I'm measuring")
                .setUnit("1")
                .build()
        counter.add(1)
        counter.add(1, Attributes.of(stringKey("thing"), "car"))
        counter.add(1, Attributes.of(stringKey("thing"), "car"), Context.current())
    }

    @Test
    fun noopBoundLongCounter_doesNotThrow() {
        val counter =
            meter
                .counterBuilder("size")
                .setDescription("The size I'm measuring")
                .setUnit("1")
                .build()
                .bind(Attributes.of(stringKey("thing"), "car"))
        counter.add(1)
        counter.add(1, Context.current())
    }

    @Test
    fun noopDoubleCounter_doesNotThrow() {
        val counter =
            meter
                .counterBuilder("size")
                .ofDoubles()
                .setDescription("The size I'm measuring")
                .setUnit("1")
                .build()
        counter.add(1.2)
        counter.add(2.5, Attributes.of(stringKey("thing"), "car"))
        counter.add(2.5, Attributes.of(stringKey("thing"), "car"), Context.current())
    }

    @Test
    fun noopBoundDoubleCounter_doesNotThrow() {
        val counter =
            meter
                .counterBuilder("size")
                .ofDoubles()
                .setDescription("The size I'm measuring")
                .setUnit("1")
                .build()
                .bind(Attributes.of(stringKey("thing"), "car"))
        counter.add(1.2)
        counter.add(2.5, Context.current())
    }

    @Test
    fun noopLongUpDownCounter_doesNotThrow() {
        val counter =
            meter
                .upDownCounterBuilder("size")
                .setDescription("The size I'm measuring")
                .setUnit("1")
                .build()
        counter.add(-1)
        counter.add(1, Attributes.of(stringKey("thing"), "car"))
        counter.add(1, Attributes.of(stringKey("thing"), "car"), Context.current())
    }

    @Test
    fun noopBoundLongUpDownCounter_doesNotThrow() {
        val counter =
            meter
                .upDownCounterBuilder("size")
                .setDescription("The size I'm measuring")
                .setUnit("1")
                .build()
                .bind(Attributes.of(stringKey("thing"), "car"))
        counter.add(-1)
        counter.add(1, Context.current())
    }

    @Test
    fun noopDoubleUpDownCounter_doesNotThrow() {
        val counter =
            meter
                .upDownCounterBuilder("size")
                .ofDoubles()
                .setDescription("The size I'm measuring")
                .setUnit("1")
                .build()
        counter.add(-2e4)
        counter.add(1.0e-1, Attributes.of(stringKey("thing"), "car"))
        counter.add(1.0e-1, Attributes.of(stringKey("thing"), "car"), Context.current())
    }

    @Test
    fun noopBoundDoubleUpDownCounter_doesNotThrow() {
        val counter =
            meter
                .upDownCounterBuilder("size")
                .ofDoubles()
                .setDescription("The size I'm measuring")
                .setUnit("1")
                .build()
                .bind(Attributes.of(stringKey("thing"), "car"))
        counter.add(-2e4)
        counter.add(1.0e-1, Context.current())
    }

    @Test
    fun noopLongHistogram_doesNotThrow() {
        val histogram =
            meter
                .histogramBuilder("size")
                .ofLongs()
                .setDescription("The size I'm measuring")
                .setUnit("1")
                .build()
        histogram.record(-1)
        histogram.record(1, Attributes.of(stringKey("thing"), "car"))
        histogram.record(1, Attributes.of(stringKey("thing"), "car"), Context.current())
    }

    @Test
    fun noopBoundLongHistogram_doesNotThrow() {
        val histogram =
            meter
                .histogramBuilder("size")
                .ofLongs()
                .setDescription("The size I'm measuring")
                .setUnit("1")
                .build()
                .bind(Attributes.of(stringKey("thing"), "car"))
        histogram.record(-1)
        histogram.record(1, Context.current())
    }

    @Test
    fun noopDoubleHistogram_doesNotThrow() {
        val histogram =
            meter
                .histogramBuilder("size")
                .setDescription("The size I'm measuring")
                .setUnit("1")
                .build()
        histogram.record(-2e4)
        histogram.record(1.0e-1, Attributes.of(stringKey("thing"), "car"))
        histogram.record(1.0e-1, Attributes.of(stringKey("thing"), "car"), Context.current())
    }

    @Test
    fun noopBoundDoubleHistogram_doesNotThrow() {
        val histogram =
            meter
                .histogramBuilder("size")
                .setDescription("The size I'm measuring")
                .setUnit("1")
                .build()
                .bind(Attributes.of(stringKey("thing"), "car"))
        histogram.record(-2e4)
        histogram.record(1.0e-1, Context.current())
    }

    @Test
    fun noopObservableLongGauage_doesNotThrow() {
        meter
            .gaugeBuilder("temperature")
            .ofLongs()
            .setDescription("The current temperature")
            .setUnit("C")
            .buildWithCallback { m ->
                m.observe(1)
                m.observe(2, Attributes.of(stringKey("thing"), "engine"))
            }
    }

    @Test
    fun noopObservableDoubleGauage_doesNotThrow() {
        meter
            .gaugeBuilder("temperature")
            .setDescription("The current temperature")
            .setUnit("C")
            .buildWithCallback { m ->
                m.observe(1.0e1)
                m.observe(-27.4, Attributes.of(stringKey("thing"), "engine"))
            }
    }

    @Test
    fun noopObservableLongCounter_doesNotThrow() {
        meter
            .counterBuilder("temperature")
            .setDescription("The current temperature")
            .setUnit("C")
            .buildWithCallback { m ->
                m.observe(1)
                m.observe(2, Attributes.of(stringKey("thing"), "engine"))
            }
    }

    @Test
    fun noopObservableDoubleCounter_doesNotThrow() {
        meter
            .counterBuilder("temperature")
            .ofDoubles()
            .setDescription("The current temperature")
            .setUnit("C")
            .buildWithCallback { m ->
                m.observe(1.0e1)
                m.observe(-27.4, Attributes.of(stringKey("thing"), "engine"))
            }
    }

    @Test
    fun noopObservableLongUpDownCounter_doesNotThrow() {
        meter
            .upDownCounterBuilder("temperature")
            .setDescription("The current temperature")
            .setUnit("C")
            .buildWithCallback { m ->
                m.observe(1)
                m.observe(2, Attributes.of(stringKey("thing"), "engine"))
            }
    }

    @Test
    fun noopObservableDoubleUpDownCounter_doesNotThrow() {
        meter
            .upDownCounterBuilder("temperature")
            .ofDoubles()
            .setDescription("The current temperature")
            .setUnit("C")
            .buildWithCallback { m ->
                m.observe(1.0e1)
                m.observe(-27.4, Attributes.of(stringKey("thing"), "engine"))
            }
    }
}
