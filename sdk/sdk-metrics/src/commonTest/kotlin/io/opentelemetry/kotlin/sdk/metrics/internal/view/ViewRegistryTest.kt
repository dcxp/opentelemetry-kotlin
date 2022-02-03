/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.view

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentType
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentValueType
import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor
import io.opentelemetry.kotlin.sdk.metrics.view.Aggregation
import io.opentelemetry.kotlin.sdk.metrics.view.InstrumentSelector
import io.opentelemetry.kotlin.sdk.metrics.view.View
import kotlin.test.Test

internal class ViewRegistryTest {
    @Test
    fun selection_onType() {
        val view = View.builder().build()
        val viewRegistry =
            ViewRegistry.builder()
                .addView(
                    InstrumentSelector.builder()
                        .setInstrumentType(InstrumentType.COUNTER)
                        .setInstrumentNameRegex(".*")
                        .build(),
                    view
                )
                .build()
        assertSoftly(
            viewRegistry.findViews(
                InstrumentDescriptor.create(
                    "",
                    "",
                    "",
                    InstrumentType.COUNTER,
                    InstrumentValueType.LONG
                ),
                INSTRUMENTATION_LIBRARY_INFO
            )
        ) {
            shouldHaveSize(1)
            first() shouldBe view
        }
        // this one hasn't been configured, so it gets the default still.
        assertSoftly(
            viewRegistry.findViews(
                InstrumentDescriptor.create(
                    "",
                    "",
                    "",
                    InstrumentType.UP_DOWN_COUNTER,
                    InstrumentValueType.LONG
                ),
                INSTRUMENTATION_LIBRARY_INFO
            )
        ) {
            shouldHaveSize(1)
            first() shouldBe ViewRegistry.DEFAULT_VIEW
        }
    }

    @Test
    fun selection_onName() {
        val view = View.builder().build()
        val viewRegistry =
            ViewRegistry.builder()
                .addView(
                    InstrumentSelector.builder()
                        .setInstrumentType(InstrumentType.COUNTER)
                        .setInstrumentNameRegex("overridden")
                        .build(),
                    view
                )
                .build()
        assertSoftly(
            viewRegistry.findViews(
                InstrumentDescriptor.create(
                    "overridden",
                    "",
                    "",
                    InstrumentType.COUNTER,
                    InstrumentValueType.LONG
                ),
                INSTRUMENTATION_LIBRARY_INFO
            )
        ) {
            shouldHaveSize(1)
            first() shouldBe view
        }
        // this one hasn't been configured, so it gets the default still.
        assertSoftly(
            viewRegistry.findViews(
                InstrumentDescriptor.create(
                    "default",
                    "",
                    "",
                    InstrumentType.COUNTER,
                    InstrumentValueType.LONG
                ),
                INSTRUMENTATION_LIBRARY_INFO
            )
        ) {
            shouldHaveSize(1)
            first() shouldBe ViewRegistry.DEFAULT_VIEW
        }
    }

    @Test
    fun selection_FirstAddedViewWins() {
        val view1 = View.builder().setAggregation(Aggregation.lastValue()).build()
        val view2 = View.builder().setAggregation(Aggregation.histogram()).build()
        val viewRegistry =
            ViewRegistry.builder()
                .addView(
                    InstrumentSelector.builder()
                        .setInstrumentType(InstrumentType.COUNTER)
                        .setInstrumentNameRegex("overridden")
                        .build(),
                    view2
                )
                .addView(
                    InstrumentSelector.builder()
                        .setInstrumentType(InstrumentType.COUNTER)
                        .setInstrumentNameRegex(".*")
                        .build(),
                    view1
                )
                .build()
        assertSoftly(
            viewRegistry.findViews(
                InstrumentDescriptor.create(
                    "overridden",
                    "",
                    "",
                    InstrumentType.COUNTER,
                    InstrumentValueType.LONG
                ),
                INSTRUMENTATION_LIBRARY_INFO
            )
        ) {
            shouldHaveSize(2)
            first() shouldBe view2
        }
        assertSoftly(
            viewRegistry.findViews(
                InstrumentDescriptor.create(
                    "default",
                    "",
                    "",
                    InstrumentType.COUNTER,
                    InstrumentValueType.LONG
                ),
                INSTRUMENTATION_LIBRARY_INFO
            )
        ) {
            shouldHaveSize(1)
            first() shouldBe view1
        }
    }

    @Test
    fun selection_regex() {
        val view = View.builder().setAggregation(Aggregation.lastValue()).build()
        val viewRegistry =
            ViewRegistry.builder()
                .addView(
                    InstrumentSelector.builder()
                        .setInstrumentNameRegex("overrid(es|den)")
                        .setInstrumentType(InstrumentType.COUNTER)
                        .build(),
                    view
                )
                .build()
        assertSoftly(
            viewRegistry.findViews(
                InstrumentDescriptor.create(
                    "overridden",
                    "",
                    "",
                    InstrumentType.COUNTER,
                    InstrumentValueType.LONG
                ),
                INSTRUMENTATION_LIBRARY_INFO
            )
        ) {
            shouldHaveSize(1)
            first() shouldBe view
        }
        assertSoftly(
            viewRegistry.findViews(
                InstrumentDescriptor.create(
                    "overrides",
                    "",
                    "",
                    InstrumentType.COUNTER,
                    InstrumentValueType.LONG
                ),
                INSTRUMENTATION_LIBRARY_INFO
            )
        ) {
            shouldHaveSize(1)
            first() shouldBe view
        }
        // this one hasn't been configured, so it gets the default still..
        assertSoftly(
            viewRegistry.findViews(
                InstrumentDescriptor.create(
                    "default",
                    "",
                    "",
                    InstrumentType.UP_DOWN_COUNTER,
                    InstrumentValueType.LONG
                ),
                INSTRUMENTATION_LIBRARY_INFO
            )
        ) {
            shouldHaveSize(1)
            first() shouldBe ViewRegistry.DEFAULT_VIEW
        }
    }

    @Test
    fun defaults() {
        val viewRegistry = ViewRegistry.builder().build()
        assertSoftly(
            viewRegistry.findViews(
                InstrumentDescriptor.create(
                    "",
                    "",
                    "",
                    InstrumentType.COUNTER,
                    InstrumentValueType.LONG
                ),
                INSTRUMENTATION_LIBRARY_INFO
            )
        ) {
            shouldHaveSize(1)
            first() shouldBe ViewRegistry.DEFAULT_VIEW
        }
        assertSoftly(
            viewRegistry.findViews(
                InstrumentDescriptor.create(
                    "",
                    "",
                    "",
                    InstrumentType.UP_DOWN_COUNTER,
                    InstrumentValueType.LONG
                ),
                INSTRUMENTATION_LIBRARY_INFO
            )
        ) {
            shouldHaveSize(1)
            first() shouldBe ViewRegistry.DEFAULT_VIEW
        }
        assertSoftly(
            viewRegistry.findViews(
                InstrumentDescriptor.create(
                    "",
                    "",
                    "",
                    InstrumentType.HISTOGRAM,
                    InstrumentValueType.LONG
                ),
                INSTRUMENTATION_LIBRARY_INFO
            )
        ) {
            shouldHaveSize(1)
            first() shouldBe ViewRegistry.DEFAULT_VIEW
        }
        assertSoftly(
            viewRegistry.findViews(
                InstrumentDescriptor.create(
                    "",
                    "",
                    "",
                    InstrumentType.OBSERVABLE_SUM,
                    InstrumentValueType.LONG
                ),
                INSTRUMENTATION_LIBRARY_INFO
            )
        ) {
            shouldHaveSize(1)
            first() shouldBe ViewRegistry.DEFAULT_VIEW
        }
        assertSoftly(
            viewRegistry.findViews(
                InstrumentDescriptor.create(
                    "",
                    "",
                    "",
                    InstrumentType.OBSERVABLE_GAUGE,
                    InstrumentValueType.LONG
                ),
                INSTRUMENTATION_LIBRARY_INFO
            )
        ) {
            shouldHaveSize(1)
            first() shouldBe ViewRegistry.DEFAULT_VIEW
        }
        assertSoftly(
            viewRegistry.findViews(
                InstrumentDescriptor.create(
                    "",
                    "",
                    "",
                    InstrumentType.OBSERVABLE_UP_DOWN_SUM,
                    InstrumentValueType.LONG
                ),
                INSTRUMENTATION_LIBRARY_INFO
            )
        ) {
            shouldHaveSize(1)
            first() shouldBe ViewRegistry.DEFAULT_VIEW
        }
    }

    companion object {
        private val INSTRUMENTATION_LIBRARY_INFO =
            InstrumentationLibraryInfo.create("name", "version", "schema_url")
    }
}
