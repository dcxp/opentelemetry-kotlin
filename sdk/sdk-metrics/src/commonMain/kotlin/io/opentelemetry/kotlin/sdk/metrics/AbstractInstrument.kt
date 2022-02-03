/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics

import io.opentelemetry.kotlin.sdk.metrics.internal.descriptor.InstrumentDescriptor

abstract class AbstractInstrument(descriptor: InstrumentDescriptor) : Instrument {
    private val descriptor: InstrumentDescriptor

    // All arguments cannot be null because they are checked in the abstract builder classes.
    init {
        this.descriptor = descriptor
    }

    fun getDescriptor(): InstrumentDescriptor {
        return descriptor
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is AbstractInstrument) {
            return false
        }
        return descriptor == other.descriptor
    }

    override fun hashCode(): Int {
        return descriptor.hashCode()
    }
}
