/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.view

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import kotlin.test.Test

class MeterSelectorTest {
    @Test
    fun nameSelection_works() {
        val exactName = MeterSelector.builder().setName("example").build()
        exactName.nameFilter("example").shouldBeTrue()
        exactName.nameFilter("example2").shouldBeFalse()
        val patternName = MeterSelector.builder().setNamePattern(Regex("ex.*")).build()
        patternName.nameFilter("example").shouldBeTrue()
        patternName.nameFilter("example2").shouldBeTrue()
        patternName.nameFilter("axample").shouldBeFalse()
        val filterName =
            MeterSelector.builder().setNameFilter { name -> name.startsWith("ex") }.build()
        filterName.nameFilter("example").shouldBeTrue()
        filterName.nameFilter("example2").shouldBeTrue()
        filterName.nameFilter("axample").shouldBeFalse()
    }

    @Test
    fun nameSelection_lastFilterWins() {
        val filterName =
            MeterSelector.builder()
                .setName("example")
                .setNamePattern(Regex("ex.*"))
                .setNameFilter { name -> false }
                .build()
        filterName.nameFilter("example").shouldBeFalse()
    }

    @Test
    fun versionSelection_works() {
        val exactVersion = MeterSelector.builder().setVersion("1.2.3").build()
        exactVersion.versionFilter("1.2.3").shouldBeTrue()
        exactVersion.versionFilter("1.2.4").shouldBeFalse()
        val patternVersion = MeterSelector.builder().setVersionPattern(Regex("1\\.2\\..*")).build()
        patternVersion.versionFilter("1.2.3").shouldBeTrue()
        patternVersion.versionFilter("1.2.4").shouldBeTrue()
        patternVersion.versionFilter("2.0.0").shouldBeFalse()
        val filterVersion =
            MeterSelector.builder().setVersionFilter { v -> v.startsWith("1") }.build()
        filterVersion.versionFilter("1.2.3").shouldBeTrue()
        filterVersion.versionFilter("1.1.1").shouldBeTrue()
        filterVersion.versionFilter("2.0.0").shouldBeFalse()
    }

    @Test
    fun versionSelection_lastFilterWins() {
        val filterVersion =
            MeterSelector.builder()
                .setVersion("1.0")
                .setVersionPattern(Regex("1.*"))
                .setVersionFilter { name -> false }
                .build()
        filterVersion.versionFilter("1.0").shouldBeFalse()
        filterVersion.versionFilter("1.2").shouldBeFalse()
    }

    @Test
    fun schemaUrlSelection_works() {
        val exact = MeterSelector.builder().setSchemaUrl("1.2.3").build()
        exact.schemaUrlFilter("1.2.3").shouldBeTrue()
        exact.schemaUrlFilter("1.2.4").shouldBeFalse()
        val pattern = MeterSelector.builder().setSchemaUrlPattern(Regex("1\\.2\\..*")).build()
        pattern.schemaUrlFilter("1.2.3").shouldBeTrue()
        pattern.schemaUrlFilter("1.2.4").shouldBeTrue()
        pattern.schemaUrlFilter("2.0.0").shouldBeFalse()
        val filter = MeterSelector.builder().setSchemaUrlFilter { s -> s.startsWith("1") }.build()
        filter.schemaUrlFilter("1.2.3").shouldBeTrue()
        filter.schemaUrlFilter("1.1.1").shouldBeTrue()
        filter.schemaUrlFilter("2.0.0").shouldBeFalse()
    }

    @Test
    fun schemaUrlSelection_lastFilterWins() {
        val schemaUrl =
            MeterSelector.builder()
                .setSchemaUrl("1.0")
                .setSchemaUrlPattern(Regex("1.*"))
                .setSchemaUrlFilter { s -> false }
                .build()
        schemaUrl.schemaUrlFilter("1.0").shouldBeFalse()
        schemaUrl.schemaUrlFilter("1.2").shouldBeFalse()
    }
}
