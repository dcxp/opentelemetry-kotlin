/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.kotest.matchers.nulls.shouldNotBeNull
import io.opentelemetry.kotlin.sdk.metrics.testing.InMemoryMetricReader
import kotlin.test.Test

internal class SdkMeterTest {
    // Meter must have an exporter configured to actual run.
    private val testMeterProvider =
        SdkMeterProvider.builder().registerMetricReader(InMemoryMetricReader.create()).build()
    private val sdkMeter = testMeterProvider[SdkMeterTest::class.simpleName!!]

    // @RegisterExtension
    // var logs: LogCapturer = LogCapturer.create().captureForType(MeterSharedState::class.java)
    @Test
    fun testLongCounter() {
        val longCounter =
            sdkMeter
                .counterBuilder("testLongCounter")
                .setDescription("My very own counter")
                .setUnit("metric tonnes")
                .build()
        longCounter.shouldNotBeNull()

        // Note: We no longer get the same instrument instance as these instances are lightweight
        // objects backed by storage now.  Here we just make sure it doesn't log a warning.
        sdkMeter
            .counterBuilder("testLongCounter")
            .setDescription("My very own counter")
            .setUnit("metric tonnes")
            .build()
        // assertThat(logs.getEvents()).isEmpty()
        sdkMeter.counterBuilder("testLongCounter").build()
        /*assertThat(
            logs.assertContains(
                { loggingEvent -> loggingEvent.getLevel().equals(WARN) },
                "Failed to register metric."
            )
                .getThrowable()
        )
            .hasMessageContaining("Metric with same name and different descriptor already created.")*/
    }

    @Test
    fun testLongCounter_upperCaseConflict() {
        val longCounter =
            sdkMeter
                .counterBuilder("testLongCounter")
                .setDescription("My very own counter")
                .setUnit("metric tonnes")
                .build()
        longCounter.shouldNotBeNull()
        sdkMeter.counterBuilder("testLongCounter".uppercase()).build()
        /*assertThat(
            logs.assertContains(
                { loggingEvent -> loggingEvent.getLevel().equals(WARN) },
                "Failed to register metric."
            )
                .getThrowable()
        )
            .hasMessageContaining("Metric with same name and different descriptor already created.")*/
    }
    // Todo Re enable when proper logging is added

    /*
       @Test
       fun testLongUpDownCounter() {
           val longUpDownCounter = sdkMeter
               .upDownCounterBuilder("testLongUpDownCounter")
               .setDescription("My very own counter")
               .setUnit("metric tonnes")
               .build()
           assertThat(longUpDownCounter).isNotNull()

           // Note: We no longer get the same instrument instance as these instances are lightweight
           // objects backed by storage now.  Here we just make sure it doesn't throw to grab
           // a second instance.
           sdkMeter
               .upDownCounterBuilder("testLongUpDownCounter")
               .setDescription("My very own counter")
               .setUnit("metric tonnes")
               .build()
           assertThat(logs.getEvents()).isEmpty()
           sdkMeter.upDownCounterBuilder("testLongUpDownCounter").build()
           assertThat(
               logs.assertContains(
                   { loggingEvent -> loggingEvent.getLevel().equals(WARN) },
                   "Failed to register metric."
               )
                   .getThrowable()
           )
               .hasMessageContaining("Metric with same name and different descriptor already created.")
       }

       @Test
       fun testLongUpDownCounter_upperCaseConflict() {
           val longUpDownCounter = sdkMeter
               .upDownCounterBuilder("testLongUpDownCounter")
               .setDescription("My very own counter")
               .setUnit("metric tonnes")
               .build()
           assertThat(longUpDownCounter).isNotNull()
           assertThat(logs.getEvents()).isEmpty()
           sdkMeter.upDownCounterBuilder("testLongUpDownCounter".uppercase(Locale.getDefault())).build()
           assertThat(
               logs.assertContains(
                   { loggingEvent -> loggingEvent.getLevel().equals(WARN) },
                   "Failed to register metric."
               )
                   .getThrowable()
           )
               .hasMessageContaining("Metric with same name and different descriptor already created.")
       }

       @Test
       fun testLongHistogram() {
           val longHistogram = sdkMeter
               .histogramBuilder("testLongValueRecorder")
               .ofLongs()
               .setDescription("My very own counter")
               .setUnit("metric tonnes")
               .build()
           assertThat(longHistogram).isNotNull()
           assertThat(logs.getEvents()).isEmpty()

           // Note: We no longer get the same instrument instance as these instances are lightweight
           // objects backed by storage now.  Here we just make sure it doesn't log an error.
           sdkMeter
               .histogramBuilder("testLongValueRecorder")
               .ofLongs()
               .setDescription("My very own counter")
               .setUnit("metric tonnes")
               .build()
           assertThat(logs.getEvents()).isEmpty()
           sdkMeter.histogramBuilder("testLongValueRecorder").ofLongs().build()
           assertThat(
               logs.assertContains(
                   { loggingEvent -> loggingEvent.getLevel().equals(WARN) },
                   "Failed to register metric."
               )
                   .getThrowable()
           )
               .hasMessageContaining("Metric with same name and different descriptor already created.")
       }

       @Test
       fun testLongHistogram_upperCaseConflict() {
           val longHistogram = sdkMeter
               .histogramBuilder("testLongValueRecorder")
               .ofLongs()
               .setDescription("My very own counter")
               .setUnit("metric tonnes")
               .build()
           assertThat(longHistogram).isNotNull()
           assertThat(logs.getEvents()).isEmpty()
           sdkMeter.histogramBuilder("testLongValueRecorder".uppercase(Locale.getDefault())).ofLongs().build()
           assertThat(
               logs.assertContains(
                   { loggingEvent -> loggingEvent.getLevel().equals(WARN) },
                   "Failed to register metric."
               )
                   .getThrowable()
           )
               .hasMessageContaining("Metric with same name and different descriptor already created.")
       }

       @Test
       fun testLongGauge_conflicts() {
           sdkMeter
               .gaugeBuilder("longValueObserver")
               .ofLongs()
               .setDescription("My very own counter")
               .setUnit("metric tonnes")
               .buildWithCallback(
                   kotlin.jvm.functions.Function1 < ? super io . opentelemetry . api . metrics . ObservableLongMeasurement,
                   Unit > < in ObservableLongMeasurement?, kotlin.Unit?> ({ obs -> }))
           assertThat(logs.getEvents()).isEmpty()
           sdkMeter.gaugeBuilder("longValueObserver").ofLongs().buildWithCallback(
               kotlin.jvm.functions.Function1 < ? super io . opentelemetry . api . metrics . ObservableLongMeasurement,
               Unit > < in ObservableLongMeasurement?, kotlin.Unit?> ({ x -> }))
           assertThat(
               logs.assertContains(
                   { loggingEvent -> loggingEvent.getLevel().equals(WARN) },
                   "Failed to register metric."
               )
                   .getThrowable()
           )
               .hasMessageContaining("Metric with same name and different descriptor already created.")
       }

       @Test
       fun testLongGauge_upperCaseConflicts() {
           sdkMeter
               .gaugeBuilder("longValueObserver")
               .ofLongs()
               .setDescription("My very own counter")
               .setUnit("metric tonnes")
               .buildWithCallback(
                   kotlin.jvm.functions.Function1 < ? super io . opentelemetry . api . metrics . ObservableLongMeasurement,
                   Unit > < in ObservableLongMeasurement?, kotlin.Unit?> ({ obs -> }))
           assertThat(logs.getEvents()).isEmpty()
           sdkMeter.gaugeBuilder("longValueObserver".uppercase(Locale.getDefault())).ofLongs().buildWithCallback(
               kotlin.jvm.functions.Function1 < ? super io . opentelemetry . api . metrics . ObservableLongMeasurement,
               Unit > < in ObservableLongMeasurement?, kotlin.Unit?> ({ x -> }))
           assertThat(
               logs.assertContains(
                   { loggingEvent -> loggingEvent.getLevel().equals(WARN) },
                   "Failed to register metric."
               )
                   .getThrowable()
           )
               .hasMessageContaining("Metric with same name and different descriptor already created.")
       }

       @Test
       fun testLongSumObserver_conflicts() {
           sdkMeter
               .counterBuilder("testLongSumObserver")
               .setDescription("My very own counter")
               .setUnit("metric tonnes")
               .buildWithCallback(
                   kotlin.jvm.functions.Function1 < ? super io . opentelemetry . api . metrics . ObservableLongMeasurement,
                   Unit > < in ObservableLongMeasurement?, kotlin.Unit?> ({ x -> }))
           assertThat(logs.getEvents()).isEmpty()
           sdkMeter.counterBuilder("testLongSumObserver").buildWithCallback(
               kotlin.jvm.functions.Function1 < ? super io . opentelemetry . api . metrics . ObservableLongMeasurement,
               Unit > < in ObservableLongMeasurement?, kotlin.Unit?> ({ x -> }))
           assertThat(
               logs.assertContains(
                   { loggingEvent -> loggingEvent.getLevel().equals(WARN) },
                   "Failed to register metric."
               )
                   .getThrowable()
           )
               .hasMessageContaining("Metric with same name and different descriptor already created.")
       }

       @Test
       fun testLongSumObserver_upperCaseConflicts() {
           sdkMeter
               .counterBuilder("testLongSumObserver")
               .setDescription("My very own counter")
               .setUnit("metric tonnes")
               .buildWithCallback(
                   kotlin.jvm.functions.Function1 < ? super io . opentelemetry . api . metrics . ObservableLongMeasurement,
                   Unit > < in ObservableLongMeasurement?, kotlin.Unit?> ({ x -> }))
           assertThat(logs.getEvents()).isEmpty()
           sdkMeter.counterBuilder("testLongSumObserver".uppercase(Locale.getDefault())).buildWithCallback(
               kotlin.jvm.functions.Function1 < ? super io . opentelemetry . api . metrics . ObservableLongMeasurement,
               Unit > < in ObservableLongMeasurement?, kotlin.Unit?> ({ x -> }))
           assertThat(
               logs.assertContains(
                   { loggingEvent -> loggingEvent.getLevel().equals(WARN) },
                   "Failed to register metric."
               )
                   .getThrowable()
           )
               .hasMessageContaining("Metric with same name and different descriptor already created.")
       }

       @Test
       fun testLongUpDownSumObserver_conflicts() {
           sdkMeter
               .upDownCounterBuilder("testLongUpDownSumObserver")
               .setDescription("My very own counter")
               .setUnit("metric tonnes")
               .buildWithCallback(
                   kotlin.jvm.functions.Function1 < ? super io . opentelemetry . api . metrics . ObservableLongMeasurement,
                   Unit > < in ObservableLongMeasurement?, kotlin.Unit?> ({ x -> }))
           assertThat(logs.getEvents()).isEmpty()
           sdkMeter.upDownCounterBuilder("testLongUpDownSumObserver").buildWithCallback(
               kotlin.jvm.functions.Function1 < ? super io . opentelemetry . api . metrics . ObservableLongMeasurement,
               Unit > < in ObservableLongMeasurement?, kotlin.Unit?> ({ x -> }))
           assertThat(
               logs.assertContains(
                   { loggingEvent -> loggingEvent.getLevel().equals(WARN) },
                   "Failed to register metric."
               )
                   .getThrowable()
           )
               .hasMessageContaining("Metric with same name and different descriptor already created.")
       }

       @Test
       fun testLongUpDownSumObserver_upperCaseConflicts() {
           sdkMeter
               .upDownCounterBuilder("testLongUpDownSumObserver")
               .setDescription("My very own counter")
               .setUnit("metric tonnes")
               .buildWithCallback(
                   kotlin.jvm.functions.Function1 < ? super io . opentelemetry . api . metrics . ObservableLongMeasurement,
                   Unit > < in ObservableLongMeasurement?, kotlin.Unit?> ({ x -> }))
           assertThat(logs.getEvents()).isEmpty()
           sdkMeter
               .upDownCounterBuilder("testLongUpDownSumObserver".uppercase(Locale.getDefault()))
               .buildWithCallback(
                   kotlin.jvm.functions.Function1 < ? super io . opentelemetry . api . metrics . ObservableLongMeasurement,
                   Unit > < in ObservableLongMeasurement?, kotlin.Unit?> ({ x -> }))
           assertThat(
               logs.assertContains(
                   { loggingEvent -> loggingEvent.getLevel().equals(WARN) },
                   "Failed to register metric."
               )
                   .getThrowable()
           )
               .hasMessageContaining("Metric with same name and different descriptor already created.")
       }

       @Test
       fun testDoubleCounter() {
           val doubleCounter = sdkMeter
               .counterBuilder("testDoubleCounter")
               .ofDoubles()
               .setDescription("My very own counter")
               .setUnit("metric tonnes")
               .build()
           assertThat(doubleCounter).isNotNull()

           // Note: We no longer get the same instrument instance as these instances are lightweight
           // objects backed by storage now.  Here we just make sure it doesn't log an error.
           sdkMeter
               .counterBuilder("testDoubleCounter")
               .ofDoubles()
               .setDescription("My very own counter")
               .setUnit("metric tonnes")
               .build()
           assertThat(logs.getEvents()).isEmpty()
           sdkMeter.counterBuilder("testDoubleCounter").ofDoubles().build()
           assertThat(
               logs.assertContains(
                   { loggingEvent -> loggingEvent.getLevel().equals(WARN) },
                   "Failed to register metric."
               )
                   .getThrowable()
           )
               .hasMessageContaining("Metric with same name and different descriptor already created.")
       }

       @Test
       fun testDoubleUpDownCounter() {
           val doubleUpDownCounter = sdkMeter
               .upDownCounterBuilder("testDoubleUpDownCounter")
               .ofDoubles()
               .setDescription("My very own counter")
               .setUnit("metric tonnes")
               .build()
           assertThat(doubleUpDownCounter).isNotNull()

           // Note: We no longer get the same instrument instance as these instances are lightweight
           // objects backed by storage now.  Here we just make sure it doesn't log an error.
           sdkMeter
               .upDownCounterBuilder("testDoubleUpDownCounter")
               .ofDoubles()
               .setDescription("My very own counter")
               .setUnit("metric tonnes")
               .build()
           assertThat(logs.getEvents()).isEmpty()
           sdkMeter.upDownCounterBuilder("testDoubleUpDownCounter").ofDoubles().build()
           assertThat(
               logs.assertContains(
                   { loggingEvent -> loggingEvent.getLevel().equals(WARN) },
                   "Failed to register metric."
               )
                   .getThrowable()
           )
               .hasMessageContaining("Metric with same name and different descriptor already created.")
       }

       @Test
       fun testDoubleHistogram() {
           val doubleValueRecorder = sdkMeter
               .histogramBuilder("testDoubleValueRecorder")
               .setDescription("My very own ValueRecorder")
               .setUnit("metric tonnes")
               .build()
           assertThat(doubleValueRecorder).isNotNull()

           // Note: We no longer get the same instrument instance as these instances are lightweight
           // objects backed by storage now.  Here we just make sure it doesn't log an error
           sdkMeter
               .histogramBuilder("testDoubleValueRecorder")
               .setDescription("My very own ValueRecorder")
               .setUnit("metric tonnes")
               .build()
           assertThat(logs.getEvents()).isEmpty()
           sdkMeter.histogramBuilder("testDoubleValueRecorder").build()
           assertThat(
               logs.assertContains(
                   { loggingEvent -> loggingEvent.getLevel().equals(WARN) },
                   "Failed to register metric."
               )
                   .getThrowable()
           )
               .hasMessageContaining("Metric with same name and different descriptor already created.")
       }

       @Test
       fun testDoubleSumObserver() {
           sdkMeter
               .counterBuilder("testDoubleSumObserver")
               .ofDoubles()
               .setDescription("My very own counter")
               .setUnit("metric tonnes")
               .buildWithCallback(
                   kotlin.jvm.functions.Function1 < ?
                   super io . opentelemetry . api . metrics . ObservableDoubleMeasurement,
                   Unit > < in ObservableDoubleMeasurement?, kotlin.Unit?> ({ x -> }))
           assertThat(logs.getEvents()).isEmpty()
           sdkMeter.counterBuilder("testDoubleSumObserver").ofDoubles().buildWithCallback(
               kotlin.jvm.functions.Function1 < ? super io . opentelemetry . api . metrics . ObservableDoubleMeasurement,
               Unit > < in ObservableDoubleMeasurement?, kotlin.Unit?> ({ x -> }))
           sdkMeter.histogramBuilder("testDoubleValueRecorder").build()
           assertThat(
               logs.assertContains(
                   { loggingEvent -> loggingEvent.getLevel().equals(WARN) },
                   "Failed to register metric."
               )
                   .getThrowable()
           )
               .hasMessageContaining("Metric with same name and different descriptor already created.")
       }

       @Test
       fun testDoubleUpDownSumObserver() {
           sdkMeter
               .upDownCounterBuilder("testDoubleUpDownSumObserver")
               .ofDoubles()
               .setDescription("My very own counter")
               .setUnit("metric tonnes")
               .buildWithCallback(
                   kotlin.jvm.functions.Function1 < ?
                   super io . opentelemetry . api . metrics . ObservableDoubleMeasurement,
                   Unit > < in ObservableDoubleMeasurement?, kotlin.Unit?> ({ x -> }))
           assertThat(logs.getEvents()).isEmpty()
           sdkMeter
               .upDownCounterBuilder("testDoubleUpDownSumObserver")
               .ofDoubles()
               .buildWithCallback(
                   kotlin.jvm.functions.Function1 < ?
                   super io . opentelemetry . api . metrics . ObservableDoubleMeasurement,
                   Unit > < in ObservableDoubleMeasurement?, kotlin.Unit?> ({ x -> }))
           sdkMeter.histogramBuilder("testDoubleValueRecorder").build()
           assertThat(
               logs.assertContains(
                   { loggingEvent -> loggingEvent.getLevel().equals(WARN) },
                   "Failed to register metric."
               )
                   .getThrowable()
           )
               .hasMessageContaining("Metric with same name and different descriptor already created.")
       }

       @Test
       fun testDoubleGauge() {
           sdkMeter
               .gaugeBuilder("doubleValueObserver")
               .setDescription("My very own counter")
               .setUnit("metric tonnes")
               .buildWithCallback(
                   kotlin.jvm.functions.Function1 < ?
                   super io . opentelemetry . api . metrics . ObservableDoubleMeasurement,
                   Unit > < in ObservableDoubleMeasurement?, kotlin.Unit?> ({ x -> }))
           assertThat(logs.getEvents()).isEmpty()
           sdkMeter.gaugeBuilder("doubleValueObserver").buildWithCallback(
               kotlin.jvm.functions.Function1 < ? super io . opentelemetry . api . metrics . ObservableDoubleMeasurement,
               Unit > < in ObservableDoubleMeasurement?, kotlin.Unit?> ({ x -> }))
           assertThat(
               logs.assertContains(
                   { loggingEvent -> loggingEvent.getLevel().equals(WARN) },
                   "Failed to register metric."
               )
                   .getThrowable()
           )
               .hasMessageContaining("Metric with same name and different descriptor already created.")
       }

    */
}
