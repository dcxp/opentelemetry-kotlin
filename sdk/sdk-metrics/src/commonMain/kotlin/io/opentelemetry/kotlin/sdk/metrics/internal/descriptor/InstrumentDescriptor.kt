/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.descriptor

import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentType
import io.opentelemetry.kotlin.sdk.metrics.common.InstrumentValueType
import io.opentelemetry.kotlin.sdk.metrics.internal.debug.SourceInfo

/**
 * Describes an instrument that was registered to record data.
 *
 * This class is internal and is hence not for public use. Its APIs are unstable and can change at
 * any time.
 */
interface InstrumentDescriptor {
    val name: String
    val description: String
    val unit: String
    val type: InstrumentType
    val valueType: InstrumentValueType

    /** Debugging information for this instrument. */
    val sourceInfo: SourceInfo

    companion object {
        fun create(
            name: String,
            description: String,
            unit: String,
            type: InstrumentType,
            valueType: InstrumentValueType
        ): InstrumentDescriptor {
            return Implementation(
                name,
                description,
                unit,
                type,
                valueType,
                SourceInfo.fromCurrentStack()
            )
        }

        data class Implementation(
            override val name: String,
            override val description: String,
            override val unit: String,
            override val type: InstrumentType,
            override val valueType: InstrumentValueType,
            override val sourceInfo: SourceInfo
        ) : InstrumentDescriptor
    }
}
