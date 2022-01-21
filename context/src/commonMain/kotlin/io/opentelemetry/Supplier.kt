package io.opentelemetry

fun interface Supplier<T : Any> {
    fun get(): T
}
