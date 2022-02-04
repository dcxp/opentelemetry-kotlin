/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.opentelemetry.kotlin.api.trace.SpanId
import io.opentelemetry.kotlin.api.trace.TraceId
import io.opentelemetry.kotlin.sdk.internal.RandomSupplier
import kotlin.random.Random

internal enum class RandomIdGenerator : IdGenerator {
    INSTANCE;

    override fun generateSpanId(): String {
        var id: Long
        val random: Random = randomSupplier()
        do {
            id = random.nextLong()
        } while (id == INVALID_ID)
        return SpanId.fromLong(id)
    }

    override fun generateTraceId(): String {
        val random: Random = randomSupplier()
        val idHi: Long = random.nextLong()
        var idLo: Long
        do {
            idLo = random.nextLong()
        } while (idLo == INVALID_ID)
        return TraceId.fromLongs(idHi, idLo)
    }

    companion object {
        private const val INVALID_ID: Long = 0
        private val randomSupplier: () -> Random = RandomSupplier.platformDefault()
    }
}
