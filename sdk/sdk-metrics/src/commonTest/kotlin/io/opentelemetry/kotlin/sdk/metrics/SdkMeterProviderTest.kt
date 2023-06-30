/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.api.baggage.Baggage
import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentType
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.mock.MetricReaderMock
import io.opentelemetry.kotlin.sdk.metrics.testing.InMemoryMetricReader
import io.opentelemetry.kotlin.sdk.metrics.view.Aggregation
import io.opentelemetry.kotlin.sdk.metrics.view.InstrumentSelector
import io.opentelemetry.kotlin.sdk.metrics.view.View
import io.opentelemetry.kotlin.sdk.resources.Resource
import io.opentelemetry.kotlin.sdk.testing.time.TestClock
import io.opentelemetry.kotlin.use
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DateTimeUnit
import kotlin.test.Test
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class SdkMeterProviderTest {
    private val testClock: TestClock = TestClock.create()
    private val sdkMeterProviderBuilder =
        SdkMeterProvider.builder().setClock(testClock).setResource(RESOURCE)

    val metricReader = MetricReaderMock()

    @Test
    fun collectAllSyncInstruments() {
        val sdkMeterReader = InMemoryMetricReader.create()
        val sdkMeterProvider = sdkMeterProviderBuilder.registerMetricReader(sdkMeterReader).build()
        val sdkMeter = sdkMeterProvider[SdkMeterProviderTest::class.simpleName!!]
        val longCounter = sdkMeter.counterBuilder("testLongCounter").build()
        longCounter.add(10, Attributes.empty())
        val longUpDownCounter = sdkMeter.upDownCounterBuilder("testLongUpDownCounter").build()
        longUpDownCounter.add(-10, Attributes.empty())
        val longValueRecorder = sdkMeter.histogramBuilder("testLongHistogram").ofLongs().build()
        longValueRecorder.record(10, Attributes.empty())
        val doubleCounter = sdkMeter.counterBuilder("testDoubleCounter").ofDoubles().build()
        doubleCounter.add(10.1, Attributes.empty())
        val doubleUpDownCounter =
            sdkMeter.upDownCounterBuilder("testDoubleUpDownCounter").ofDoubles().build()
        doubleUpDownCounter.add(-10.1, Attributes.empty())
        val doubleValueRecorder = sdkMeter.histogramBuilder("testDoubleHistogram").build()
        doubleValueRecorder.record(10.1, Attributes.empty())
        assertSoftly(sdkMeterReader.collectAllMetrics()) {
            assertSoftly(single { it.name == "testDoubleHistogram" }) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                description shouldBe ""
                unit shouldBe "1"
                doubleHistogramData.shouldNotBeNull()
                assertSoftly(doubleHistogramData.points.single()) {
                    startEpochNanos shouldBe testClock.now()
                    epochNanos shouldBe testClock.now()
                    attributes shouldBe Attributes.empty()
                    count shouldBe 1
                    sum shouldBe 10.1
                    counts.shouldContainInOrder(0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
                }
            }
            assertSoftly(single { it.name == "testDoubleCounter" }) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                description shouldBe ""
                unit shouldBe "1"
                doubleSumData.shouldNotBeNull()
                assertSoftly(doubleSumData.points.single()) {
                    startEpochNanos shouldBe testClock.now()
                    epochNanos shouldBe testClock.now()
                    attributes shouldBe Attributes.empty()
                    value shouldBe 10.1
                }
            }
            assertSoftly(single { it.name == "testLongHistogram" }) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                description shouldBe ""
                unit shouldBe "1"
                doubleHistogramData.shouldNotBeNull()
                assertSoftly(doubleHistogramData.points.single()) {
                    startEpochNanos shouldBe testClock.now()
                    epochNanos shouldBe testClock.now()
                    attributes shouldBe Attributes.empty()
                    count shouldBe 1
                    sum shouldBe 10
                    counts.shouldContainInOrder(0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
                }
            }
            assertSoftly(single { it.name == "testLongUpDownCounter" }) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                description shouldBe ""
                unit shouldBe "1"
                longSumData.shouldNotBeNull()
                assertSoftly(longSumData.points.single()) {
                    startEpochNanos shouldBe testClock.now()
                    epochNanos shouldBe testClock.now()
                    attributes shouldBe Attributes.empty()
                    value shouldBe -10
                }
            }
            assertSoftly(single { it.name == "testLongCounter" }) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                description shouldBe ""
                unit shouldBe "1"
                longSumData.shouldNotBeNull()
                io.kotest.assertions.assertSoftly(longSumData.points.single()) {
                    startEpochNanos shouldBe testClock.now()
                    epochNanos shouldBe testClock.now()
                    attributes shouldBe io.opentelemetry.kotlin.api.common.Attributes.empty()
                    value shouldBe 10
                }
            }
            assertSoftly(single { it.name == "testDoubleUpDownCounter" }) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                description shouldBe ""
                unit shouldBe "1"
                doubleSumData.shouldNotBeNull()
                io.kotest.assertions.assertSoftly(doubleSumData.points.single()) {
                    startEpochNanos shouldBe testClock.now()
                    epochNanos shouldBe testClock.now()
                    attributes shouldBe io.opentelemetry.kotlin.api.common.Attributes.empty()
                    value shouldBe -10.1
                }
            }
        }
    }

    @Test
    fun collectAllSyncInstruments_OverwriteTemporality() {
        sdkMeterProviderBuilder.registerView(
            InstrumentSelector.builder().setInstrumentType(InstrumentType.COUNTER).build(),
            View.builder().setAggregation(Aggregation.explicitBucketHistogram(emptyList())).build()
        )
        val sdkMeterReader = InMemoryMetricReader.createDelta()
        val sdkMeterProvider = sdkMeterProviderBuilder.registerMetricReader(sdkMeterReader).build()
        val sdkMeter = sdkMeterProvider[SdkMeterProviderTest::class.simpleName!!]
        val longCounter = sdkMeter.counterBuilder("testLongCounter").build()
        longCounter.add(10, Attributes.empty())
        testClock.advance(1.seconds)
        assertSoftly(sdkMeterReader.collectAllMetrics().single()) {
            resource shouldBe RESOURCE
            instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
            name shouldBe "testLongCounter"
            doubleHistogramData.shouldNotBeNull()
            doubleHistogramData.aggregationTemporality shouldBe AggregationTemporality.DELTA
            assertSoftly(doubleHistogramData.points.single()) {
                startEpochNanos shouldBe testClock.now() - 1000000000
                epochNanos shouldBe testClock.now()
                attributes shouldBe Attributes.empty()
                count shouldBe 1
            }
        }
        longCounter.add(10, Attributes.empty())
        testClock.advance(1.seconds)
        assertSoftly(sdkMeterReader.collectAllMetrics().single()) {
            resource shouldBe RESOURCE
            instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
            name shouldBe "testLongCounter"
            doubleHistogramData.shouldNotBeNull()
            doubleHistogramData.aggregationTemporality shouldBe AggregationTemporality.DELTA
            assertSoftly(doubleHistogramData.points.single()) {
                startEpochNanos shouldBe testClock.now() - 1000000000
                epochNanos shouldBe testClock.now()
                attributes shouldBe Attributes.empty()
                count shouldBe 1
            }
        }
    }

    @Test
    fun collectAllSyncInstruments_DeltaHistogram() {
        registerViewForAllTypes(
            sdkMeterProviderBuilder,
            Aggregation.explicitBucketHistogram(emptyList())
        )
        val sdkMeterReader = InMemoryMetricReader.createDelta()
        val sdkMeterProvider = sdkMeterProviderBuilder.registerMetricReader(sdkMeterReader).build()
        val sdkMeter = sdkMeterProvider[SdkMeterProviderTest::class.simpleName!!]
        val longCounter = sdkMeter.counterBuilder("testLongCounter").build()
        longCounter.add(10, Attributes.empty())
        val longUpDownCounter = sdkMeter.upDownCounterBuilder("testLongUpDownCounter").build()
        longUpDownCounter.add(10, Attributes.empty())
        val longValueRecorder = sdkMeter.histogramBuilder("testLongValueRecorder").ofLongs().build()
        longValueRecorder.record(10, Attributes.empty())
        val doubleCounter = sdkMeter.counterBuilder("testDoubleCounter").ofDoubles().build()
        doubleCounter.add(10.0, Attributes.empty())
        val doubleUpDownCounter =
            sdkMeter.upDownCounterBuilder("testDoubleUpDownCounter").ofDoubles().build()
        doubleUpDownCounter.add(10.0, Attributes.empty())
        val doubleValueRecorder = sdkMeter.histogramBuilder("testDoubleValueRecorder").build()
        doubleValueRecorder.record(10.0, Attributes.empty())
        testClock.advance(1.seconds)
        var result = sdkMeterReader.collectAllMetrics()
        result.forEach { metric ->
            assertSoftly(metric) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                unit shouldBe "1"
                description shouldBe ""
                doubleHistogramData.shouldNotBeNull()
                doubleHistogramData.aggregationTemporality shouldBe AggregationTemporality.DELTA
                assertSoftly(doubleHistogramData.points.single()) {
                    startEpochNanos shouldBe testClock.now() - 1000000000
                    epochNanos shouldBe testClock.now()
                    attributes shouldBe Attributes.empty()
                    count shouldBe 1
                }
            }
        }
        result
            .map { it.name }
            .shouldContainExactlyInAnyOrder(
                "testLongCounter",
                "testDoubleCounter",
                "testLongUpDownCounter",
                "testDoubleUpDownCounter",
                "testLongValueRecorder",
                "testDoubleValueRecorder"
            )
        testClock.advance(1.seconds)
        longCounter.add(10, Attributes.empty())
        longUpDownCounter.add(10, Attributes.empty())
        longValueRecorder.record(10, Attributes.empty())
        doubleCounter.add(10.0, Attributes.empty())
        doubleUpDownCounter.add(10.0, Attributes.empty())
        doubleValueRecorder.record(10.0, Attributes.empty())
        result = sdkMeterReader.collectAllMetrics()
        result.forEach { metric ->
            assertSoftly(metric) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                unit shouldBe "1"
                description shouldBe ""
                doubleHistogramData.shouldNotBeNull()
                doubleHistogramData.aggregationTemporality shouldBe AggregationTemporality.DELTA
                assertSoftly(doubleHistogramData.points.single()) {
                    startEpochNanos shouldBe testClock.now() - 1000000000
                    epochNanos shouldBe testClock.now()
                    attributes shouldBe Attributes.empty()
                    count shouldBe 1
                }
            }
        }
        result
            .map { it.name }
            .shouldContainExactlyInAnyOrder(
                "testLongCounter",
                "testDoubleCounter",
                "testLongUpDownCounter",
                "testDoubleUpDownCounter",
                "testLongValueRecorder",
                "testDoubleValueRecorder"
            )
    }

    @Test
    fun collectAllAsyncInstruments() {
        val sdkMeterReader = InMemoryMetricReader.create()
        val sdkMeterProvider = sdkMeterProviderBuilder.registerMetricReader(sdkMeterReader).build()
        val sdkMeter = sdkMeterProvider[SdkMeterProviderTest::class.simpleName!!]
        sdkMeter.counterBuilder("testLongSumObserver").buildWithCallback { longResult ->
            longResult.observe(10, Attributes.empty())
        }
        sdkMeter.upDownCounterBuilder("testLongUpDownSumObserver").buildWithCallback { longResult ->
            longResult.observe(-10, Attributes.empty())
        }
        sdkMeter.gaugeBuilder("testLongValueObserver").ofLongs().buildWithCallback { longResult ->
            longResult.observe(10, Attributes.empty())
        }
        sdkMeter.counterBuilder("testDoubleSumObserver").ofDoubles().buildWithCallback {
            doubleResult ->
            doubleResult.observe(10.1, Attributes.empty())
        }
        sdkMeter
            .upDownCounterBuilder("testDoubleUpDownSumObserver")
            .ofDoubles()
            .buildWithCallback { doubleResult -> doubleResult.observe(-10.1, Attributes.empty()) }
        sdkMeter.gaugeBuilder("testDoubleValueObserver").buildWithCallback { doubleResult ->
            doubleResult.observe(10.1, Attributes.empty())
        }
        assertSoftly(sdkMeterReader.collectAllMetrics()) {
            assertSoftly(single { it.name == "testLongSumObserver" }) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                description shouldBe ""
                unit shouldBe "1"
                longSumData.shouldNotBeNull()
                assertSoftly(longSumData.points.single()) {
                    startEpochNanos shouldBe testClock.now()
                    epochNanos shouldBe testClock.now()
                    attributes shouldBe Attributes.empty()
                    value shouldBe 10
                }
            }
            assertSoftly(single { it.name == "testDoubleSumObserver" }) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                description shouldBe ""
                unit shouldBe "1"
                doubleSumData.shouldNotBeNull()
                assertSoftly(doubleSumData.points.single()) {
                    startEpochNanos shouldBe testClock.now()
                    epochNanos shouldBe testClock.now()
                    attributes shouldBe Attributes.empty()
                    value shouldBe 10.1
                }
            }
            assertSoftly(single { it.name == "testLongUpDownSumObserver" }) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                description shouldBe ""
                unit shouldBe "1"
                longSumData.shouldNotBeNull()
                assertSoftly(longSumData.points.single()) {
                    startEpochNanos shouldBe testClock.now()
                    epochNanos shouldBe testClock.now()
                    attributes shouldBe Attributes.empty()
                    value shouldBe -10
                }
            }
            assertSoftly(single { it.name == "testDoubleUpDownSumObserver" }) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                description shouldBe ""
                unit shouldBe "1"
                doubleSumData.shouldNotBeNull()
                assertSoftly(doubleSumData.points.single()) {
                    startEpochNanos shouldBe testClock.now()
                    epochNanos shouldBe testClock.now()
                    attributes shouldBe Attributes.empty()
                    value shouldBe -10.1
                }
            }
            assertSoftly(single { it.name == "testLongValueObserver" }) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                description shouldBe ""
                unit shouldBe "1"
                longGaugeData.shouldNotBeNull()
                assertSoftly(longGaugeData.points.single()) {
                    startEpochNanos shouldBe testClock.now()
                    epochNanos shouldBe testClock.now()
                    attributes shouldBe Attributes.empty()
                    value shouldBe 10
                }
            }
            assertSoftly(single { it.name == "testDoubleValueObserver" }) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                description shouldBe ""
                unit shouldBe "1"
                doubleGaugeData.shouldNotBeNull()
                assertSoftly(doubleGaugeData.points.single()) {
                    startEpochNanos shouldBe testClock.now()
                    epochNanos shouldBe testClock.now()
                    attributes shouldBe Attributes.empty()
                    value shouldBe 10.1
                }
            }
        }
    }

    @Test
    fun viewSdk_AllowRenames() {
        val reader = InMemoryMetricReader.create()
        val provider =
            sdkMeterProviderBuilder
                .registerMetricReader(reader)
                .registerView(
                    InstrumentSelector.builder() // TODO: Make instrument type optional.
                        .setInstrumentType(InstrumentType.OBSERVABLE_GAUGE)
                        .setInstrumentName("test")
                        .build(),
                    View.builder()
                        .setName("not_test")
                        .setDescription("not_desc")
                        .setAggregation(Aggregation.lastValue())
                        .build()
                )
                .build()
        val meter = provider[SdkMeterProviderTest::class.simpleName!!]
        meter.gaugeBuilder("test").setDescription("desc").setUnit("unit").buildWithCallback { o ->
            o.observe(1.0)
        }
        assertSoftly(reader.collectAllMetrics().single()) {
            name shouldBe "not_test"
            description shouldBe "not_desc"
            unit shouldBe "unit"
            doubleGaugeData.shouldNotBeNull()
        }
    }

    @Test
    fun viewSdk_AllowMultipleViewsPerSynchronousInstrument() {
        val selector =
            InstrumentSelector.builder() // TODO: Make instrument type optional.
                .setInstrumentType(InstrumentType.HISTOGRAM)
                .setInstrumentName("test")
                .build()
        val reader = InMemoryMetricReader.create()
        val provider =
            sdkMeterProviderBuilder
                .registerMetricReader(reader)
                .registerView(
                    selector,
                    View.builder()
                        .setName("not_test")
                        .setDescription("not_desc")
                        .setAggregation(Aggregation.lastValue())
                        .build()
                )
                .registerView(
                    selector,
                    View.builder()
                        .setName("not_test_2")
                        .setDescription("not_desc_2")
                        .setAggregation(Aggregation.sum())
                        .build()
                )
                .build()
        val meter = provider[SdkMeterProviderTest::class.simpleName!!]
        val histogram =
            meter.histogramBuilder("test").setDescription("desc").setUnit("unit").build()
        histogram.record(1.0)
        assertSoftly(reader.collectAllMetrics()) {
            assertSoftly(single { it.name == "not_test" }) {
                description shouldBe "not_desc"
                unit shouldBe "unit"
                doubleGaugeData.shouldNotBeNull()
            }
            assertSoftly(single { it.name == "not_test_2" }) {
                description shouldBe "not_desc_2"
                unit shouldBe "unit"
                doubleSumData.shouldNotBeNull()
            }
        }
    }

    @Test
    fun viewSdk_AllowMultipleViewsPerAsynchronousInstrument() {
        val selector =
            InstrumentSelector.builder() // TODO: Make instrument type optional.
                .setInstrumentType(InstrumentType.OBSERVABLE_GAUGE)
                .setInstrumentName("test")
                .build()
        val reader = InMemoryMetricReader.create()
        val provider =
            sdkMeterProviderBuilder
                .registerMetricReader(reader)
                .registerView(
                    selector,
                    View.builder()
                        .setName("not_test")
                        .setDescription("not_desc")
                        .setAggregation(Aggregation.lastValue())
                        .build()
                )
                .registerView(
                    selector,
                    View.builder()
                        .setName("not_test_2")
                        .setDescription("not_desc_2")
                        .setAggregation(Aggregation.sum())
                        .build()
                )
                .build()
        val meter = provider[SdkMeterProviderTest::class.simpleName!!]
        meter.gaugeBuilder("test").setDescription("desc").setUnit("unit").buildWithCallback { obs ->
            obs.observe(1.0)
        }
        assertSoftly(reader.collectAllMetrics()) {
            assertSoftly(single { it.name == "not_test" }) {
                description shouldBe "not_desc"
                unit shouldBe "unit"
                doubleGaugeData.shouldNotBeNull()
            }
            assertSoftly(single { it.name == "not_test_2" }) {
                description shouldBe "not_desc_2"
                unit shouldBe "unit"
                doubleSumData.shouldNotBeNull()
            }
        }
    }

    @Test
    fun viewSdk_capturesBaggageFromContext() {
        val selector =
            InstrumentSelector.builder()
                .setInstrumentType(InstrumentType.COUNTER)
                .setInstrumentName("test")
                .build()
        val reader = InMemoryMetricReader.create()
        val provider =
            sdkMeterProviderBuilder
                .registerMetricReader(reader)
                .registerView(
                    selector,
                    View.builder()
                        .setAggregation(Aggregation.sum())
                        .appendAllBaggageAttributes()
                        .build()
                )
                .build()
        val meter = provider[SdkMeterProviderTest::class.simpleName!!]
        val baggage = Baggage.builder().put("baggage", "value").build()
        val context = Context.root().with(baggage)
        val counter = meter.counterBuilder("test").build()

        // Make sure whether or not we explicitly pass baggage, all values have it appended.
        counter.add(1, Attributes.empty(), context)
        counter.bind(Attributes.empty()).add(1, context)
        context.makeCurrent().use {
            counter.add(1, Attributes.empty())
            counter.bind(Attributes.empty()).add(1)
        }
        // Now make sure all metrics have baggage appended.
        // Implicitly we should have ONLY ONE metric data point that has baggage appended.
        assertSoftly(reader.collectAllMetrics().single()) {
            name shouldBe "test"
            unit shouldBe "1"
            longSumData.shouldNotBeNull()
            longSumData.aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
            longSumData.points.single().attributes shouldBe
                Attributes.builder().put("baggage", "value").build()
        }
    }

    @Test
    fun collectAllAsyncInstruments_CumulativeHistogram() {
        registerViewForAllTypes(
            sdkMeterProviderBuilder,
            Aggregation.explicitBucketHistogram(emptyList())
        )
        val sdkMeterReader = InMemoryMetricReader.create()
        val sdkMeterProvider = sdkMeterProviderBuilder.registerMetricReader(sdkMeterReader).build()
        val sdkMeter = sdkMeterProvider[SdkMeterProviderTest::class.simpleName!!]
        sdkMeter.counterBuilder("testLongSumObserver").buildWithCallback { longResult ->
            longResult.observe(10, Attributes.empty())
        }
        sdkMeter.upDownCounterBuilder("testLongUpDownSumObserver").buildWithCallback { longResult ->
            longResult.observe(-10, Attributes.empty())
        }
        sdkMeter.gaugeBuilder("testLongValueObserver").ofLongs().buildWithCallback { longResult ->
            longResult.observe(10, Attributes.empty())
        }
        sdkMeter.counterBuilder("testDoubleSumObserver").ofDoubles().buildWithCallback {
            doubleResult ->
            doubleResult.observe(10.1, Attributes.empty())
        }
        sdkMeter
            .upDownCounterBuilder("testDoubleUpDownSumObserver")
            .ofDoubles()
            .buildWithCallback { doubleResult -> doubleResult.observe(-10.1, Attributes.empty()) }
        sdkMeter.gaugeBuilder("testDoubleValueObserver").buildWithCallback { doubleResult ->
            doubleResult.observe(10.1, Attributes.empty())
        }
        testClock.advance(50.nanoseconds)
        var result = sdkMeterReader.collectAllMetrics()
        result.forEach { metric ->
            assertSoftly(metric) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                unit shouldBe "1"
                description shouldBe ""
                doubleHistogramData.shouldNotBeNull()
                doubleHistogramData.aggregationTemporality shouldBe
                    AggregationTemporality.CUMULATIVE
                assertSoftly(doubleHistogramData.points.single()) {
                    startEpochNanos shouldBe testClock.now() - 50
                    epochNanos shouldBe testClock.now()
                    attributes shouldBe Attributes.empty()
                    count shouldBe 1
                }
            }
        }
        result
            .map { it.name }
            .shouldContainExactlyInAnyOrder(
                "testLongSumObserver",
                "testDoubleSumObserver",
                "testLongUpDownSumObserver",
                "testDoubleUpDownSumObserver",
                "testLongValueObserver",
                "testDoubleValueObserver"
            )
        testClock.advance(50.nanoseconds)
        // When collecting the next set of async measurements, we still only have 1 count per bucket
        // because we assume ALL measurements are cumulative and come in the async callback.
        // Note: We do not support "gauge histogram".
        result = sdkMeterReader.collectAllMetrics()
        result.forEach { metric ->
            assertSoftly(metric) {
                resource shouldBe RESOURCE
                instrumentationLibraryInfo shouldBe INSTRUMENTATION_LIBRARY_INFO
                unit shouldBe "1"
                description shouldBe ""
                doubleHistogramData.shouldNotBeNull()
                doubleHistogramData.aggregationTemporality shouldBe
                    AggregationTemporality.CUMULATIVE
                assertSoftly(doubleHistogramData.points.single()) {
                    startEpochNanos shouldBe testClock.now() - 100
                    epochNanos shouldBe testClock.now()
                    attributes shouldBe Attributes.empty()
                    count shouldBe 1
                }
            }
        }
        result
            .map { it.name }
            .shouldContainExactlyInAnyOrder(
                "testLongSumObserver",
                "testDoubleSumObserver",
                "testLongUpDownSumObserver",
                "testDoubleUpDownSumObserver",
                "testLongValueObserver",
                "testDoubleValueObserver"
            )
    }

    @Test
    fun sdkMeterProvider_supportsMultipleCollectorsCumulative() {
        val collector1 = InMemoryMetricReader.create()
        val collector2 = InMemoryMetricReader.create()
        val meterProvider =
            sdkMeterProviderBuilder
                .registerMetricReader(collector1)
                .registerMetricReader(collector2)
                .build()
        val sdkMeter = meterProvider[SdkMeterProviderTest::class.simpleName!!]
        val counter = sdkMeter.counterBuilder("testSum").build()
        val startTime: Long = testClock.now()
        counter.add(1L)
        testClock.advance(1.seconds)
        assertSoftly(collector1.collectAllMetrics().single()) {
            name shouldBe "testSum"
            resource shouldBe RESOURCE
            longSumData.shouldNotBeNull()
            longSumData.aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
            assertSoftly(longSumData.points.single()) {
                startEpochNanos shouldBe startTime
                epochNanos shouldBe testClock.now()
                value shouldBe 1
            }
        }
        counter.add(1L)
        testClock.advance(1.seconds)

        // Make sure collector 2 sees the value collector 1 saw
        assertSoftly(collector2.collectAllMetrics().single()) {
            name shouldBe "testSum"
            resource shouldBe RESOURCE
            longSumData.shouldNotBeNull()
            longSumData.aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
            assertSoftly(longSumData.points.single()) {
                startEpochNanos shouldBe startTime
                epochNanos shouldBe testClock.now()
                value shouldBe 2
            }
        }

        // Make sure Collector 1 sees the same point as 2
        assertSoftly(collector1.collectAllMetrics().single()) {
            name shouldBe "testSum"
            resource shouldBe RESOURCE
            longSumData.shouldNotBeNull()
            longSumData.aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
            assertSoftly(longSumData.points.single()) {
                startEpochNanos shouldBe startTime
                epochNanos shouldBe testClock.now()
                value shouldBe 2
            }
        }
    }

    @Test
    fun sdkMeterProvider_supportsMultipleCollectorsDelta() {
        // Note: we use a view to do delta aggregation, but any view ALWAYS uses double-precision
        // right
        // now.
        val collector1 = InMemoryMetricReader.createDelta()
        val collector2 = InMemoryMetricReader.createDelta()
        val meterProvider =
            sdkMeterProviderBuilder
                .registerMetricReader(collector1)
                .registerMetricReader(collector2)
                .registerView(
                    InstrumentSelector.builder()
                        .setInstrumentType(InstrumentType.COUNTER)
                        .setInstrumentName("testSum")
                        .build(),
                    View.builder().setAggregation(Aggregation.sum()).build()
                )
                .build()
        val sdkMeter = meterProvider[SdkMeterProviderTest::class.simpleName!!]
        val counter = sdkMeter.counterBuilder("testSum").build()
        val startTime: Long = testClock.now()
        counter.add(1L)
        testClock.advance(1.seconds)
        assertSoftly(collector1.collectAllMetrics().single()) {
            name shouldBe "testSum"
            resource shouldBe RESOURCE
            longSumData.shouldNotBeNull()
            longSumData.aggregationTemporality shouldBe AggregationTemporality.DELTA
            assertSoftly(longSumData.points.single()) {
                startEpochNanos shouldBe startTime
                epochNanos shouldBe testClock.now()
                value shouldBe 1
            }
        }
        val collectorOneTimeOne: Long = testClock.now()
        counter.add(1L)
        testClock.advance(1.seconds)

        // Make sure collector 2 sees the value collector 1 saw
        assertSoftly(collector2.collectAllMetrics().single()) {
            name shouldBe "testSum"
            resource shouldBe RESOURCE
            longSumData.shouldNotBeNull()
            longSumData.aggregationTemporality shouldBe AggregationTemporality.DELTA
            assertSoftly(longSumData.points.single()) {
                startEpochNanos shouldBe startTime
                epochNanos shouldBe testClock.now()
                value shouldBe 2
            }
        }

        // Make sure Collector 1 sees the same point as 2, when it collects.
        assertSoftly(collector1.collectAllMetrics().single()) {
            name shouldBe "testSum"
            resource shouldBe RESOURCE
            longSumData.shouldNotBeNull()
            longSumData.aggregationTemporality shouldBe AggregationTemporality.DELTA
            assertSoftly(longSumData.points.single()) {
                startEpochNanos shouldBe collectorOneTimeOne
                epochNanos shouldBe testClock.now()
                value shouldBe 1
            }
        }
    }

    @Test
    fun shutdown() = runTest( timeout = 200.seconds ) {
        val result =
            SdkMeterProvider.builder()
                .registerMetricReader { _ -> metricReader }
                .build()
                .shutdown()
                .join(10, DateTimeUnit.SECOND)
        result.isSuccess.shouldBeTrue()
    }

    companion object {
        private val RESOURCE =
            Resource.create(Attributes.of(AttributeKey.stringKey("resource_key"), "resource_value"))
        private val INSTRUMENTATION_LIBRARY_INFO =
            InstrumentationLibraryInfo.create(SdkMeterProviderTest::class.simpleName!!, null)

        private fun registerViewForAllTypes(
            meterProviderBuilder: SdkMeterProviderBuilder,
            aggregation: Aggregation
        ) {
            for (instrumentType in InstrumentType.values()) {
                meterProviderBuilder.registerView(
                    InstrumentSelector.builder().setInstrumentType(instrumentType).build(),
                    View.builder().setAggregation(aggregation).build()
                )
            }
        }
    }
}
