package io.opentelemetry.kotlin.api.common

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant

fun Instant.getNanoseconds(): Long {
    return (DateTimeUnit.SECOND.nanoseconds * this.epochSeconds) + this.nanosecondsOfSecond
}
