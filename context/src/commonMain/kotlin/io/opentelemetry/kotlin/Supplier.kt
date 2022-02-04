package io.opentelemetry.kotlin

fun interface Supplier<T : Any> {
    fun get(): T
}
