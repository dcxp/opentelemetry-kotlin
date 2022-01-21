/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.metrics.internal

import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.metrics.BoundDoubleCounter
import io.opentelemetry.api.metrics.BoundDoubleHistogram
import io.opentelemetry.api.metrics.BoundDoubleUpDownCounter
import io.opentelemetry.api.metrics.BoundLongCounter
import io.opentelemetry.api.metrics.BoundLongHistogram
import io.opentelemetry.api.metrics.BoundLongUpDownCounter
import io.opentelemetry.api.metrics.DoubleCounter
import io.opentelemetry.api.metrics.DoubleCounterBuilder
import io.opentelemetry.api.metrics.DoubleGaugeBuilder
import io.opentelemetry.api.metrics.DoubleHistogram
import io.opentelemetry.api.metrics.DoubleHistogramBuilder
import io.opentelemetry.api.metrics.DoubleUpDownCounter
import io.opentelemetry.api.metrics.DoubleUpDownCounterBuilder
import io.opentelemetry.api.metrics.LongCounter
import io.opentelemetry.api.metrics.LongCounterBuilder
import io.opentelemetry.api.metrics.LongGaugeBuilder
import io.opentelemetry.api.metrics.LongHistogram
import io.opentelemetry.api.metrics.LongHistogramBuilder
import io.opentelemetry.api.metrics.LongUpDownCounter
import io.opentelemetry.api.metrics.LongUpDownCounterBuilder
import io.opentelemetry.api.metrics.Meter
import io.opentelemetry.api.metrics.ObservableDoubleMeasurement
import io.opentelemetry.api.metrics.ObservableLongMeasurement
import io.opentelemetry.context.Context

/**
 * No-op implementations of [Meter].
 *
 * This implementation should induce as close to zero overhead as possible.
 *
 * A few notes from the specificaiton on allowed behaviors leading to this deasign [
 * [Instrument
 * Spec](https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/metrics/api.md#instrument)
 * ]:
 *
 * * Multiple Insturments with the same name under the same Meter MUST return an error
 * * Different Meters MUST be treated as separate namespaces
 * * Implementations MUST NOT require users to repeatedly obtain a Meter again with the same
 * name+version+schema_url to pick up configuration changes. This can be achieved either by allowing
 * to work with an outdated configuration or by ensuring that new configuration applies also to
 * previously returned Meters.
 * * A MeterProvider could also return a no-op Meter here if application owners configure the SDK to
 * suppress telemetry produced by this library
 * * In case an invalid name (null or empty string) is specified, a working Meter implementation
 * MUST be returned as a fallback rather than returning null or throwing an exception,
 */
class NoopMeter private constructor() : Meter {
    override fun counterBuilder(name: String): LongCounterBuilder {
        return NoopLongCounterBuilder()
    }

    override fun upDownCounterBuilder(name: String): LongUpDownCounterBuilder {
        return NoopLongUpDownCounterBuilder()
    }

    override fun histogramBuilder(name: String): DoubleHistogramBuilder {
        return NoopDoubleHistogramBuilder()
    }

    override fun gaugeBuilder(name: String): DoubleGaugeBuilder {
        return NoopDoubleObservableInstrumentBuilder()
    }

    private class NoopLongCounter : LongCounter {
        override fun add(value: Long, attributes: Attributes, context: Context) {}
        override fun add(value: Long, attributes: Attributes) {}
        override fun add(value: Long) {}
        override fun bind(attributes: Attributes): BoundLongCounter {
            return NoopBoundLongCounter()
        }
    }

    private class NoopBoundLongCounter : BoundLongCounter {
        override fun add(value: Long) {}
        override fun add(value: Long, context: Context) {}
        override fun unbind() {}
    }

    private class NoopDoubleCounter : DoubleCounter {
        override fun add(value: Double, attributes: Attributes, context: Context) {}
        override fun add(value: Double, attributes: Attributes) {}
        override fun add(value: Double) {}
        override fun bind(attributes: Attributes): BoundDoubleCounter {
            return NoopBoundDoubleCounter()
        }
    }

    private class NoopBoundDoubleCounter : BoundDoubleCounter {
        override fun add(value: Double) {}
        override fun add(value: Double, context: Context) {}
        override fun unbind() {}
    }

    private class NoopLongCounterBuilder : LongCounterBuilder {
        override fun setDescription(description: String): LongCounterBuilder {
            return this
        }

        override fun setUnit(unit: String): LongCounterBuilder {
            return this
        }

        override fun ofDoubles(): DoubleCounterBuilder {
            return NoopDoubleCounterBuilder()
        }

        override fun build(): LongCounter {
            return NoopLongCounter()
        }

        override fun buildWithCallback(callback: (ObservableLongMeasurement) -> Unit) {}
    }

    private class NoopDoubleCounterBuilder : DoubleCounterBuilder {
        override fun setDescription(description: String): DoubleCounterBuilder {
            return this
        }

        override fun setUnit(unit: String): DoubleCounterBuilder {
            return this
        }

        override fun ofLongs(): LongCounterBuilder {
            return NoopLongCounterBuilder()
        }

        override fun build(): DoubleCounter {
            return NoopDoubleCounter()
        }

        override fun buildWithCallback(callback: (ObservableDoubleMeasurement) -> Unit) {}
    }

    private class NoopLongUpDownCounter : LongUpDownCounter {
        override fun add(value: Long, attributes: Attributes, context: Context) {}
        override fun add(value: Long, attributes: Attributes) {}
        override fun add(value: Long) {}
        override fun bind(attributes: Attributes): BoundLongUpDownCounter {
            return NoopBoundLongUpDownCounter()
        }
    }

    private class NoopBoundLongUpDownCounter : BoundLongUpDownCounter {
        override fun add(value: Long, context: Context) {}
        override fun add(value: Long) {}
        override fun unbind() {}
    }

    private class NoopDoubleUpDownCounter : DoubleUpDownCounter {
        override fun add(value: Double, attributes: Attributes, context: Context) {}
        override fun add(value: Double, attributes: Attributes) {}
        override fun add(value: Double) {}
        override fun bind(attributes: Attributes): BoundDoubleUpDownCounter {
            return NoopBoundDoubleUpDownCounter()
        }
    }

    private class NoopBoundDoubleUpDownCounter : BoundDoubleUpDownCounter {
        override fun add(value: Double, context: Context) {}
        override fun add(value: Double) {}
        override fun unbind() {}
    }

    private class NoopLongUpDownCounterBuilder : LongUpDownCounterBuilder {
        override fun setDescription(description: String): LongUpDownCounterBuilder {
            return this
        }

        override fun setUnit(unit: String): LongUpDownCounterBuilder {
            return this
        }

        override fun ofDoubles(): DoubleUpDownCounterBuilder {
            return NoopDoubleUpDownCounterBuilder()
        }

        override fun build(): LongUpDownCounter {
            return NoopLongUpDownCounter()
        }

        override fun buildWithCallback(callback: (ObservableLongMeasurement) -> Unit) {}
    }

    private class NoopDoubleUpDownCounterBuilder : DoubleUpDownCounterBuilder {
        override fun setDescription(description: String): DoubleUpDownCounterBuilder {
            return this
        }

        override fun setUnit(unit: String): DoubleUpDownCounterBuilder {
            return this
        }

        override fun ofLongs(): LongUpDownCounterBuilder {
            return NoopLongUpDownCounterBuilder()
        }

        override fun build(): DoubleUpDownCounter {
            return NoopDoubleUpDownCounter()
        }

        override fun buildWithCallback(callback: (ObservableDoubleMeasurement) -> Unit) {}
    }

    class NoopDoubleHistogram : DoubleHistogram {
        override fun record(value: Double, attributes: Attributes, context: Context) {}
        override fun record(value: Double, attributes: Attributes) {}
        override fun record(value: Double) {}
        override fun bind(attributes: Attributes): BoundDoubleHistogram {
            return NoopBoundDoubleHistogram()
        }
    }

    class NoopBoundDoubleHistogram : BoundDoubleHistogram {
        override fun record(value: Double, context: Context) {}
        override fun record(value: Double) {}
        override fun unbind() {}
    }

    class NoopLongHistogram : LongHistogram {
        override fun record(value: Long, attributes: Attributes, context: Context) {}
        override fun record(value: Long, attributes: Attributes) {}
        override fun record(value: Long) {}
        override fun bind(attributes: Attributes): BoundLongHistogram {
            return NoopBoundLongHistogram()
        }
    }

    class NoopBoundLongHistogram : BoundLongHistogram {
        override fun record(value: Long, context: Context) {}
        override fun record(value: Long) {}
        override fun unbind() {}
    }

    class NoopDoubleHistogramBuilder : DoubleHistogramBuilder {
        override fun setDescription(description: String): DoubleHistogramBuilder {
            return this
        }

        override fun setUnit(unit: String): DoubleHistogramBuilder {
            return this
        }

        override fun ofLongs(): LongHistogramBuilder {
            return NoopLongHistogramBuilder()
        }

        override fun build(): DoubleHistogram {
            return NoopDoubleHistogram()
        }
    }

    class NoopLongHistogramBuilder : LongHistogramBuilder {
        override fun setDescription(description: String): LongHistogramBuilder {
            return this
        }

        override fun setUnit(unit: String): LongHistogramBuilder {
            return this
        }

        override fun ofDoubles(): DoubleHistogramBuilder {
            return NoopDoubleHistogramBuilder()
        }

        override fun build(): LongHistogram {
            return NoopLongHistogram()
        }
    }

    class NoopDoubleObservableInstrumentBuilder : DoubleGaugeBuilder {
        override fun setDescription(description: String): DoubleGaugeBuilder {
            return this
        }

        override fun setUnit(unit: String): DoubleGaugeBuilder {
            return this
        }

        override fun ofLongs(): LongGaugeBuilder {
            return NoopLongObservableInstrumentBuilder()
        }

        override fun buildWithCallback(callback: (ObservableDoubleMeasurement) -> Unit) {}
    }

    class NoopLongObservableInstrumentBuilder : LongGaugeBuilder {
        override fun setDescription(description: String): LongGaugeBuilder {
            return this
        }

        override fun setUnit(unit: String): LongGaugeBuilder {
            return this
        }

        override fun ofDoubles(): DoubleGaugeBuilder {
            return NoopDoubleObservableInstrumentBuilder()
        }

        override fun buildWithCallback(callback: (ObservableLongMeasurement) -> Unit) {}
    }

    companion object {
        private val INSTANCE = NoopMeter()
        val instance: Meter
            get() = INSTANCE
    }
}
