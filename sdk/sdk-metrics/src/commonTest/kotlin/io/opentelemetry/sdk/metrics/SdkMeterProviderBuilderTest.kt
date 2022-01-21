/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.sdk.metrics

import io.kotest.matchers.shouldBe
import io.opentelemetry.api.metrics.GlobalMeterProvider
import io.opentelemetry.sdk.metrics.testing.InMemoryMetricReader
import io.opentelemetry.sdk.resources.Resource
import kotlin.test.Test

internal class SdkMeterProviderBuilderTest {
    @Test
    fun buildAndRegisterGlobal() {
        val meterProvider = SdkMeterProvider.builder().buildAndRegisterGlobal()
        try {
            GlobalMeterProvider.get() shouldBe meterProvider
        } finally {
            GlobalMeterProvider.disableGlobalMeterProvider()
        }
    }

    @Test
    fun defaultResource() {
        // We need a reader to have a resource.
        val meterProvider =
            SdkMeterProvider.builder().registerMetricReader(InMemoryMetricReader.create()).build()
        meterProvider.sharedState.resource shouldBe Resource.default
    }
}
