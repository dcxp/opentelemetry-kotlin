/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.view

import io.opentelemetry.kotlin.sdk.metrics.internal.debug.SourceInfo
import io.opentelemetry.kotlin.sdk.metrics.internal.view.AttributesProcessor

/** TODO: javadoc. */
interface View {
    /** The name of the resulting metric to generate, or `null` if the same as the instrument. */
    val name: String?

    /** The name of the resulting metric to generate, or `null` if the same as the instrument. */
    val description: String?

    /** The aggregation used for this view. */
    val aggregation: Aggregation

    /** Processor of attributes before performing aggregation. */
    val attributesProcessor: AttributesProcessor

    /** Information about where the View was defined. */
    val sourceInfo: SourceInfo

    companion object {
        fun builder(): ViewBuilder {
            return ViewBuilder()
        }

        fun create(
            name: String?,
            description: String?,
            aggregation: Aggregation,
            attributesProcessor: AttributesProcessor
        ): View {
            return Implementation(
                name,
                description,
                aggregation,
                attributesProcessor,
                SourceInfo.fromCurrentStack()
            )
        }
    }

    class Implementation(
        override val name: String?,
        override val description: String?,
        override val aggregation: Aggregation,
        override val attributesProcessor: AttributesProcessor,
        override val sourceInfo: SourceInfo
    ) : View
}
