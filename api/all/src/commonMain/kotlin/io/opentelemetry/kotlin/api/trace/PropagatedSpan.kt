/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.trace

import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.common.Attributes
import kotlinx.datetime.DateTimeUnit

/**
 * The default [Span] that is used when no `Span` implementation is available. All operations are
 * no-op except context propagation.
 */
internal class PropagatedSpan private constructor(override val spanContext: SpanContext) : Span {

    override fun setAttribute(key: String, value: String): Span {
        return this
    }

    override fun setAttribute(key: String, value: Long): Span {
        return this
    }

    override fun setAttribute(key: String, value: Double): Span {
        return this
    }

    override fun setAttribute(key: String, value: Boolean): Span {
        return this
    }

    override fun <T : Any> setAttribute(key: AttributeKey<T>, value: T): Span {
        return this
    }

    override fun setAllAttributes(attributes: Attributes): Span {
        return this
    }

    override fun addEvent(name: String): Span {
        return this
    }

    override fun addEvent(name: String, timestamp: Long, unit: DateTimeUnit): Span {
        return this
    }

    override fun addEvent(name: String, attributes: Attributes): Span {
        return this
    }

    override fun addEvent(
        name: String,
        attributes: Attributes,
        timestamp: Long,
        unit: DateTimeUnit
    ): Span {
        return this
    }

    override fun setStatus(statusCode: StatusCode): Span {
        return this
    }

    override fun setStatus(statusCode: StatusCode, description: String): Span {
        return this
    }

    override fun recordException(exception: Throwable, additionalAttributes: Attributes): Span {
        return this
    }

    override fun updateName(name: String): Span {
        return this
    }

    override fun end() {}

    override fun end(timestamp: Long, unit: DateTimeUnit) {}

    override fun isRecording(): Boolean {
        return false
    }

    override fun toString(): String {
        return "PropagatedSpan($spanContext)"
    }

    companion object {
        val INVALID = PropagatedSpan(SpanContext.invalid)

        // Used by auto-instrumentation agent. Check with auto-instrumentation before making changes
        // to
        // this method.
        //
        // In particular, do not change this return type to PropagatedSpan because
        // auto-instrumentation
        // hijacks this method and returns a bridged implementation of Span.
        //
        // Ideally auto-instrumentation would hijack the public Span.wrap() instead of this
        // method, but auto-instrumentation also needs to inject its own implementation of Span
        // into the class loader at the same time, which causes a problem because injecting a class
        // into
        // the class loader automatically resolves its super classes (interfaces), which in this
        // case is
        // Span, which would be the same class (interface) being instrumented at that time,
        // which would lead to the JVM throwing a LinkageError "attempted duplicate interface
        // definition"
        fun create(spanContext: SpanContext): Span {
            return PropagatedSpan(spanContext)
        }
    }
}
