/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.metrics.Meter
import io.opentelemetry.kotlin.api.metrics.ObservableLongMeasurement
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.testing.InMemoryMetricReader
import kotlinx.atomicfu.atomic
import kotlin.test.Test
import kotlin.time.Duration

internal class CardinalityTest {
    private val deltaReader: InMemoryMetricReader = InMemoryMetricReader.createDelta()
    private val cumulativeReader: InMemoryMetricReader = InMemoryMetricReader.create()
    private val meter: Meter
    init {
        val sdkMeterProvider =
            SdkMeterProvider.builder()
                .registerMetricReader(deltaReader)
                .registerMetricReader(cumulativeReader)
                .setMinimumCollectionInterval(Duration.ZERO)
                .build()
        meter = sdkMeterProvider[CardinalityTest::class.simpleName!!]
    }

    /**
     * Records to sync instruments, with distinct attributes each time. Validates that stale metrics
     * are dropped for delta and cumulative readers. Stale metrics are those with attributes that
     * did not receive recordings in the most recent collection.
     */
    @Test
    fun staleMetricsDropped_synchronousInstrument() {
        val syncCounter = meter.counterBuilder("sync-counter").build()
        for (i in 1..5) {
            syncCounter.add(1, Attributes.builder().put("key", "num_$i").build())
            assertSoftly(deltaReader.collectAllMetrics()) {
                withClue("Delta collection $i") {
                    shouldHaveSize(1)
                    assertSoftly(this.single()) {
                        name shouldBe "sync-counter"
                        longSumData.shouldNotBeNull()
                        assertSoftly(longSumData) {
                            aggregationTemporality shouldBe AggregationTemporality.DELTA
                            points.shouldHaveSize(1)
                        }
                    }
                }
            }
            assertSoftly(cumulativeReader.collectAllMetrics()) {
                withClue("Cumulative collection $i") {
                    shouldHaveSize(1)
                    assertSoftly(this.single()) {
                        name shouldBe "sync-counter"
                        longSumData.shouldNotBeNull()
                        assertSoftly(longSumData) {
                            aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                            points.shouldHaveSize(1)
                        }
                    }
                }
            }
        }
    }

    /**
     * Records to async instruments, with distinct attributes each time. Validates that stale
     * metrics are dropped for delta and cumulative readers. Stale metrics are those with attributes
     * that did not receive recordings in the most recent collection.
     */
    private val count = atomic(0L)
    @Test
    fun staleMetricsDropped_asynchronousInstrument() {
        count.lazySet(0L)
        meter.counterBuilder("async-counter").buildWithCallback { measurement ->
            measurement.observe(
                1,
                Attributes.builder().put("key", "num_" + count.incrementAndGet()).build()
            )
        }
        for (i in 1..5) {
            assertSoftly(deltaReader.collectAllMetrics()) {
                withClue("Delta collection $i") {
                    shouldHaveSize(1)
                    assertSoftly(this.single()) {
                        name shouldBe "async-counter"
                        longSumData.shouldNotBeNull()
                        assertSoftly(longSumData) {
                            aggregationTemporality shouldBe AggregationTemporality.DELTA
                            points.shouldHaveSize(1)
                        }
                    }
                }
            }
            assertSoftly(cumulativeReader.collectAllMetrics()) {
                withClue("Cumulative collection $i") {
                    shouldHaveSize(1)
                    assertSoftly(this.single()) {
                        name shouldBe "async-counter"
                        longSumData.shouldNotBeNull()
                        assertSoftly(longSumData) {
                            aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                            points.shouldHaveSize(1)
                        }
                    }
                }
            }
        }
    }

    /**
     * Records to sync instruments, many distinct attributes. Validates that the
     * `MetricStorageUtils#MAX_ACCUMULATIONS` is enforced for each instrument.
     */
    @Test
    fun cardinalityLimits_synchronousInstrument() {
        val syncCounter1 = meter.counterBuilder("sync-counter1").build()
        val syncCounter2 = meter.counterBuilder("sync-counter2").build()
        for (i in 0 until MAX_ACCUMULATIONS + 1) {
            syncCounter1.add(1, Attributes.builder().put("key", "value$i").build())
            syncCounter2.add(1, Attributes.builder().put("key", "value$i").build())
        }
        assertSoftly(deltaReader.collectAllMetrics()) {
            withClue("Delta collection") {
                shouldHaveSize(2)
                assertSoftly(this.single { it.name == "sync-counter1" }) {
                    longSumData.shouldNotBeNull()
                    assertSoftly(longSumData) {
                        aggregationTemporality shouldBe AggregationTemporality.DELTA
                        points.shouldHaveSize(MAX_ACCUMULATIONS)
                    }
                }
                assertSoftly(this.single { it.name == "sync-counter2" }) {
                    longSumData.shouldNotBeNull()
                    assertSoftly(longSumData) {
                        aggregationTemporality shouldBe AggregationTemporality.DELTA
                        points.shouldHaveSize(MAX_ACCUMULATIONS)
                    }
                }
            }
        }
        assertSoftly(cumulativeReader.collectAllMetrics()) {
            withClue("Cumulative collection") {
                shouldHaveSize(2)
                assertSoftly(this.single { it.name == "sync-counter1" }) {
                    longSumData.shouldNotBeNull()
                    assertSoftly(longSumData) {
                        aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                        points.shouldHaveSize(MAX_ACCUMULATIONS)
                    }
                }
                assertSoftly(this.single { it.name == "sync-counter2" }) {
                    longSumData.shouldNotBeNull()
                    assertSoftly(longSumData) {
                        aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                        points.shouldHaveSize(MAX_ACCUMULATIONS)
                    }
                }
            }
        }
    }

    /**
     * Records to sync instruments, many distinct attributes. Validates that the
     * `MetricStorageUtils#MAX_ACCUMULATIONS` is enforced for each instrument.
     */
    @Test
    fun cardinalityLimits_asynchronousInstrument() {
        val callback = { measurement: ObservableLongMeasurement ->
            for (i in 0 until MAX_ACCUMULATIONS + 1) {
                measurement.observe(1, Attributes.builder().put("key", "value$i").build())
            }
        }
        meter.counterBuilder("async-counter1").buildWithCallback(callback)
        meter.counterBuilder("async-counter2").buildWithCallback(callback)
        assertSoftly(deltaReader.collectAllMetrics()) {
            withClue("Delta collection") {
                shouldHaveSize(2)
                assertSoftly(this.single { it.name == "async-counter1" }) {
                    longSumData.shouldNotBeNull()
                    assertSoftly(longSumData) {
                        aggregationTemporality shouldBe AggregationTemporality.DELTA
                        points.shouldHaveSize(MAX_ACCUMULATIONS)
                    }
                }
                assertSoftly(this.single { it.name == "async-counter2" }) {
                    longSumData.shouldNotBeNull()
                    assertSoftly(longSumData) {
                        aggregationTemporality shouldBe AggregationTemporality.DELTA
                        points.shouldHaveSize(MAX_ACCUMULATIONS)
                    }
                }
            }
        }
        assertSoftly(cumulativeReader.collectAllMetrics()) {
            withClue("Cumulative collection") {
                shouldHaveSize(2)
                assertSoftly(this.single { it.name == "async-counter1" }) {
                    longSumData.shouldNotBeNull()
                    assertSoftly(longSumData) {
                        aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                        points.shouldHaveSize(MAX_ACCUMULATIONS)
                    }
                }
                assertSoftly(this.single { it.name == "async-counter2" }) {
                    longSumData.shouldNotBeNull()
                    assertSoftly(longSumData) {
                        aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                        points.shouldHaveSize(MAX_ACCUMULATIONS)
                    }
                }
            }
        }
    }

    companion object {
        /** Traces `MetricStorageUtils#MAX_ACCUMULATIONS`. */
        private const val MAX_ACCUMULATIONS = 2000
    }
}
