package io.opentelemetry.api.common

import kotlinx.datetime.DateTimeUnit

fun DateTimeUnit.getNanos(): Long {
    return when (this) {
        is DateTimeUnit.TimeBased -> nanoseconds
        is DateTimeUnit.DayBased -> DateTimeUnit.HOUR.nanoseconds * 24 * days
        is DateTimeUnit.MonthBased ->
            throw IllegalStateException("Month based DateTimeUnits can not be converted to nanos")
    }
}

fun DateTimeUnit.normalizeToNanos(timestamp: Long): Long {
    return this.getNanos() * timestamp
}

fun DateTimeUnit.normalizeToNanos(timestamp: Int): Long {
    return this.getNanos() * timestamp
}

fun DateTimeUnit.normalizeToMilliseconds(timestamp: Int): Long {
    return normalizeToNanos(timestamp) / 1000
}

fun DateTimeUnit.normalizeToMilliseconds(timestamp: Long): Long {
    return normalizeToNanos(timestamp) / 1000
}
