/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.state

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentType
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentValueType
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.data.DoublePointData
import io.opentelemetry.kotlin.sdk.metrics.exemplar.ExemplarFilter
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.Aggregator
import io.opentelemetry.kotlin.sdk.metrics.internal.aggregator.DoubleAccumulation
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.MetricDescriptor
import io.opentelemetry.kotlin.sdk.metrics.internal.export.CollectionHandle
import io.opentelemetry.kotlin.sdk.metrics.view.Aggregation
import io.opentelemetry.kotlin.sdk.resources.Resource
import kotlin.test.Test

internal class TemporalMetricStorageTest {
    private val collector1: CollectionHandle
    private val collector2: CollectionHandle
    private val allCollectors: Set<CollectionHandle>
    init {
        val supplier = CollectionHandle.createSupplier()
        collector1 = supplier.get()
        collector2 = supplier.get()
        allCollectors = setOf(collector1, collector2)
    }

    @Test
    fun synchronousCumulative_joinsWithLastMeasurementForCumulative() {
        val temporality = AggregationTemporality.CUMULATIVE
        val storage = TemporalMetricStorage(SUM, /* isSynchronous= */ true)
        // Send in new measurement at time 10 for collector 1
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                temporality,
                createMeasurement(3.0),
                0,
                10
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                points.isEmpty().shouldBeFalse()
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 0
                    epochNanos shouldBe 10
                    value shouldBe 3
                }
            }
        }
        // Send in new measurement at time 30 for collector 1
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                temporality,
                createMeasurement(3.0),
                0,
                30
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                points.isEmpty().shouldBeFalse()
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 0
                    epochNanos shouldBe 30
                    value shouldBe 6
                }
            }
        }
        // Send in new measurement at time 40 for collector 2
        assertSoftly(
            storage.buildMetricFor(
                collector2,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                temporality,
                createMeasurement(4.0),
                0,
                60
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                points.isEmpty().shouldBeFalse()
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 0
                    epochNanos shouldBe 60
                    value shouldBe 4
                }
            }
        }
        // Send in new measurement at time 35 for collector 1
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                temporality,
                createMeasurement(2.0),
                0,
                35
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                points.isEmpty().shouldBeFalse()
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 0
                    epochNanos shouldBe 35
                    value shouldBe 8
                }
            }
        }
    }

    @Test
    fun synchronousCumulative_dropsStale() {
        val storage = TemporalMetricStorage(SUM, /* isSynchronous= */ true)

        // Send in new measurement at time 10 for collector 1, with attr1
        val measurement1: MutableMap<Attributes, DoubleAccumulation> = HashMap()
        val attr1 = Attributes.builder().put("key", "value1").build()
        measurement1[attr1] = DoubleAccumulation.create(3.0)
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                AggregationTemporality.CUMULATIVE,
                measurement1,
                0,
                10
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                points.size shouldBe 1
                points.shouldContainExactly(DoublePointData.create(0, 10, attr1, 3.0))
            }
        }

        // Send in new measurement at time 20 for collector 1, with attr2
        // Result should drop accumulation for attr1, only reporting accumulation for attr2
        val measurement2: MutableMap<Attributes, DoubleAccumulation> = HashMap()
        val attr2 = Attributes.builder().put("key", "value2").build()
        measurement2[attr2] = DoubleAccumulation.create(7.0)
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                AggregationTemporality.CUMULATIVE,
                measurement2,
                0,
                20
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                points.size shouldBe 1
                points.shouldContainExactly(DoublePointData.create(0, 20, attr2, 7.0))
            }
        }
    }

    @Test
    fun synchronousDelta_dropsStale() {
        val storage = TemporalMetricStorage(SUM, /* isSynchronous= */ true)

        // Send in new measurement at time 10 for collector 1, with attr1
        val measurement1: MutableMap<Attributes, DoubleAccumulation> = HashMap()
        val attr1 = Attributes.builder().put("key", "value1").build()
        measurement1[attr1] = DoubleAccumulation.create(3.0)
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                AggregationTemporality.DELTA,
                measurement1,
                0,
                10
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.DELTA
                points.size shouldBe 1
                points.shouldContainExactly(DoublePointData.create(0, 10, attr1, 3.0))
            }
        }

        // Send in new measurement at time 20 for collector 1, with attr2
        // Result should drop accumulation for attr1, only reporting accumulation for attr2
        val measurement2: MutableMap<Attributes, DoubleAccumulation> = HashMap()
        val attr2 = Attributes.builder().put("key", "value2").build()
        measurement2[attr2] = DoubleAccumulation.create(7.0)
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                AggregationTemporality.DELTA,
                measurement2,
                0,
                20
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.DELTA
                points.size shouldBe 1
                points.shouldContainExactly(DoublePointData.create(10, 20, attr2, 7.0))
            }
        }
    }

    @Test
    fun synchronousDelta_useLastTimestamp() {
        val temporality = AggregationTemporality.DELTA
        val storage = TemporalMetricStorage(SUM, /* isSynchronous= */ true)
        // Send in new measurement at time 10 for collector 1
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                temporality,
                createMeasurement(3.0),
                0,
                10
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.DELTA
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 0
                    epochNanos shouldBe 10
                    value shouldBe 3
                }
            }
        }

        // Send in new measurement at time 30 for collector 1
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                temporality,
                createMeasurement(3.0),
                0,
                30
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.DELTA
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 10
                    epochNanos shouldBe 30
                    value shouldBe 3
                }
            }
        }
        // Send in new measurement at time 40 for collector 2
        assertSoftly(
            storage.buildMetricFor(
                collector2,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                temporality,
                createMeasurement(4.0),
                0,
                60
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.DELTA
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 0
                    epochNanos shouldBe 60
                    value shouldBe 4
                }
            }
        }

        // Send in new measurement at time 35 for collector 1
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                temporality,
                createMeasurement(2.0),
                0,
                35
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.DELTA
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 30
                    epochNanos shouldBe 35
                    value shouldBe 2
                }
            }
        }
    }

    @Test
    fun synchronous_deltaAndCumulative() {
        val storage = TemporalMetricStorage(SUM, /* isSynchronous= */ true)
        // Send in new measurement at time 10 for collector 1
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                AggregationTemporality.DELTA,
                createMeasurement(3.0),
                0,
                10
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.DELTA
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 0
                    epochNanos shouldBe 10
                    value shouldBe 3
                }
            }
        }
        // Send in new measurement at time 30 for collector 1
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                AggregationTemporality.DELTA,
                createMeasurement(3.0),
                0,
                30
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.DELTA
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 10
                    epochNanos shouldBe 30
                    value shouldBe 3
                }
            }
        }
        // Send in new measurement at time 40 for collector 2
        assertSoftly(
            storage.buildMetricFor(
                collector2,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                AggregationTemporality.CUMULATIVE,
                createMeasurement(4.0),
                0,
                40
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 0
                    epochNanos shouldBe 40
                    value shouldBe 4
                }
            }
        }
        // Send in new measurement at time 35 for collector 1
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                AggregationTemporality.DELTA,
                createMeasurement(2.0),
                0,
                35
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.DELTA
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 30
                    epochNanos shouldBe 35
                    value shouldBe 2
                }
            }
        }
        // Send in new measurement at time 60 for collector 2
        assertSoftly(
            storage.buildMetricFor(
                collector2,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                AggregationTemporality.CUMULATIVE,
                createMeasurement(4.0),
                0,
                60
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 0
                    epochNanos shouldBe 60
                    value shouldBe 8
                }
            }
        }
    }

    @Test
    fun asynchronousCumulative_doesNotJoin() {
        val temporality = AggregationTemporality.CUMULATIVE
        val storage = TemporalMetricStorage(ASYNC_SUM, /* isSynchronous= */ false)
        // Send in new measurement at time 10 for collector 1
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                temporality,
                createMeasurement(3.0),
                0,
                10
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 0
                    epochNanos shouldBe 10
                    value shouldBe 3
                }
            }
        }
        // Send in new measurement at time 30 for collector 1
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                temporality,
                createMeasurement(3.0),
                0,
                30
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 0
                    epochNanos shouldBe 30
                    value shouldBe 3
                }
            }
        }
        // Send in new measurement at time 40 for collector 2
        assertSoftly(
            storage.buildMetricFor(
                collector2,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                temporality,
                createMeasurement(4.0),
                0,
                60
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 0
                    epochNanos shouldBe 60
                    value shouldBe 4
                }
            }
        }
        // Send in new measurement at time 35 for collector 1
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                temporality,
                createMeasurement(2.0),
                0,
                35
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 0
                    epochNanos shouldBe 35
                    value shouldBe 2
                }
            }
        }
    }

    @Test
    fun asynchronousCumulative_dropsStale() {
        val storage = TemporalMetricStorage(ASYNC_SUM, /* isSynchronous= */ false)

        // Send in new measurement at time 10 for collector 1, with attr1
        val measurement1: MutableMap<Attributes, DoubleAccumulation> = HashMap()
        val attr1 = Attributes.builder().put("key", "value1").build()
        measurement1[attr1] = DoubleAccumulation.create(3.0)
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                AggregationTemporality.CUMULATIVE,
                measurement1,
                0,
                10
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                points.size shouldBe 1
                points.shouldContainExactly(DoublePointData.create(0, 10, attr1, 3.0))
            }
        }

        // Send in new measurement at time 20 for collector 1, with attr2
        // Result should drop accumulation for attr1, only reporting accumulation for attr2
        val measurement2: MutableMap<Attributes, DoubleAccumulation> = HashMap()
        val attr2 = Attributes.builder().put("key", "value2").build()
        measurement2[attr2] = DoubleAccumulation.create(7.0)
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                AggregationTemporality.CUMULATIVE,
                measurement2,
                0,
                20
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                points.size shouldBe 1
                points.shouldContainExactly(DoublePointData.create(0, 20, attr2, 7.0))
            }
        }
    }

    @Test
    fun asynchronousDelta_dropsStale() {
        val storage = TemporalMetricStorage(ASYNC_SUM, /* isSynchronous= */ false)

        // Send in new measurement at time 10 for collector 1, with attr1
        val measurement1: MutableMap<Attributes, DoubleAccumulation> = HashMap()
        val attr1 = Attributes.builder().put("key", "value1").build()
        measurement1[attr1] = DoubleAccumulation.create(3.0)
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                AggregationTemporality.DELTA,
                measurement1,
                0,
                10
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.DELTA
                points.size shouldBe 1
                points.shouldContainExactly(DoublePointData.create(0, 10, attr1, 3.0))
            }
        }

        // Send in new measurement at time 20 for collector 1, with attr2
        // Result should drop accumulation for attr1, only reporting accumulation for attr2
        val measurement2: MutableMap<Attributes, DoubleAccumulation> = HashMap()
        val attr2 = Attributes.builder().put("key", "value2").build()
        measurement2[attr2] = DoubleAccumulation.create(7.0)
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                AggregationTemporality.DELTA,
                measurement2,
                0,
                20
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.DELTA
                points.size shouldBe 1
                points.shouldContainExactly(DoublePointData.create(10, 20, attr2, 7.0))
            }
        }
    }

    @Test
    fun asynchronousDelta_diffsLastTimestamp() {
        val temporality = AggregationTemporality.DELTA
        val storage = TemporalMetricStorage(ASYNC_SUM, /* isSynchronous= */ false)
        // Send in new measurement at time 10 for collector 1
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                temporality,
                createMeasurement(3.0),
                0,
                10
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.DELTA
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 0
                    epochNanos shouldBe 10
                    value shouldBe 3
                }
            }
        }
        // Send in new measurement at time 30 for collector 1
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                temporality,
                createMeasurement(3.0),
                0,
                30
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.DELTA
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 10
                    epochNanos shouldBe 30
                    value shouldBe 0
                }
            }
        }
        // Send in new measurement at time 40 for collector 2
        assertSoftly(
            storage.buildMetricFor(
                collector2,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                temporality,
                createMeasurement(4.0),
                0,
                60
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.DELTA
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 0
                    epochNanos shouldBe 60
                    value shouldBe 4
                }
            }
        }
        // Send in new measurement at time 35 for collector 1
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                temporality,
                createMeasurement(2.0),
                0,
                35
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.DELTA
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 30
                    epochNanos shouldBe 35
                    value shouldBe -1
                }
            }
        }
    }

    @Test
    fun asynchronous_DeltaAndCumulative() {
        val storage = TemporalMetricStorage(ASYNC_SUM, /* isSynchronous= */ false)

        // Send in new measurement at time 10 for collector 1
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                AggregationTemporality.DELTA,
                createMeasurement(3.0),
                0,
                10
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.DELTA
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 0
                    epochNanos shouldBe 10
                    value shouldBe 3
                }
            }
        }
        // Send in new measurement at time 30 for collector 1
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                AggregationTemporality.DELTA,
                createMeasurement(3.0),
                0,
                30
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.DELTA
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 10
                    epochNanos shouldBe 30
                    value shouldBe 0
                }
            }
        }
        // Send in new measurement at time 40 for collector 2
        assertSoftly(
            storage.buildMetricFor(
                collector2,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                AggregationTemporality.CUMULATIVE,
                createMeasurement(4.0),
                0,
                60
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 0
                    epochNanos shouldBe 60
                    value shouldBe 4
                }
            }
        }
        // Send in new measurement at time 35 for collector 1
        assertSoftly(
            storage.buildMetricFor(
                collector1,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                AggregationTemporality.DELTA,
                createMeasurement(2.0),
                0,
                35
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.DELTA
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 30
                    epochNanos shouldBe 35
                    value shouldBe -1
                }
            }
        }
        // Send in new measurement at time 60 for collector 2
        assertSoftly(
            storage.buildMetricFor(
                collector2,
                Resource.empty(),
                InstrumentationLibraryInfo.empty(),
                METRIC_DESCRIPTOR,
                AggregationTemporality.CUMULATIVE,
                createMeasurement(5.0),
                0,
                60
            )!!
        ) {
            doubleSumData.shouldNotBeNull()
            assertSoftly(doubleSumData) {
                aggregationTemporality shouldBe AggregationTemporality.CUMULATIVE
                points.size shouldBe 1
                assertSoftly(points.single()) {
                    startEpochNanos shouldBe 0
                    epochNanos shouldBe 60
                    value shouldBe 5
                }
            }
        }
    }

    companion object {
        private val DESCRIPTOR =
            InstrumentDescriptor.create(
                "name",
                "description",
                "unit",
                InstrumentType.COUNTER,
                InstrumentValueType.DOUBLE
            )
        private val ASYNC_DESCRIPTOR =
            InstrumentDescriptor.create(
                "name",
                "description",
                "unit",
                InstrumentType.OBSERVABLE_SUM,
                InstrumentValueType.DOUBLE
            )
        private val METRIC_DESCRIPTOR = MetricDescriptor.create("name", "description", "unit")
        private val SUM: Aggregator<DoubleAccumulation> =
            Aggregation.sum().createAggregator(DESCRIPTOR, ExemplarFilter.neverSample())
        private val ASYNC_SUM: Aggregator<DoubleAccumulation> =
            Aggregation.sum().createAggregator(ASYNC_DESCRIPTOR, ExemplarFilter.neverSample())

        private fun createMeasurement(value: Double): Map<Attributes, DoubleAccumulation> {
            val measurement: MutableMap<Attributes, DoubleAccumulation> = HashMap()
            measurement[Attributes.empty()] = DoubleAccumulation.create(value)
            return measurement
        }
    }
}
