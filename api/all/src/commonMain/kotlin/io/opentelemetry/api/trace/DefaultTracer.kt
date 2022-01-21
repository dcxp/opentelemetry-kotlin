/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.trace

import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.context.Context
import kotlinx.datetime.DateTimeUnit

/** No-op implementations of [Tracer]. */
internal class DefaultTracer private constructor() : Tracer {
    override fun spanBuilder(spanName: String): SpanBuilder {
        return NoopSpanBuilder.create()
    }

    // Noop implementation of Span.Builder.
    private class NoopSpanBuilder private constructor() : SpanBuilder {
        private var spanContext: SpanContext? = null
        override fun startSpan(): Span {
            if (spanContext == null) {
                spanContext = Span.current().spanContext
            }
            return Span.wrap(spanContext!!)
        }

        override fun setParent(context: Context): SpanBuilder {
            spanContext = Span.fromContext(context).spanContext
            return this
        }

        override fun setNoParent(): SpanBuilder {
            spanContext = SpanContext.invalid
            return this
        }

        override fun addLink(spanContext: SpanContext): SpanBuilder {
            return this
        }

        override fun addLink(spanContext: SpanContext, attributes: Attributes): NoopSpanBuilder {
            return this
        }

        override fun setAttribute(key: String, value: String): SpanBuilder {
            return this
        }

        override fun setAttribute(key: String, value: Long): SpanBuilder {
            return this
        }

        override fun setAttribute(key: String, value: Double): SpanBuilder {
            return this
        }

        override fun setAttribute(key: String, value: Boolean): SpanBuilder {
            return this
        }

        override fun <T : Any> setAttribute(key: AttributeKey<T>, value: T): SpanBuilder {
            return this
        }

        override fun setAllAttributes(attributes: Attributes): SpanBuilder {
            return this
        }

        override fun setSpanKind(spanKind: SpanKind): SpanBuilder {
            return this
        }

        override fun setStartTimestamp(startTimestamp: Long, unit: DateTimeUnit): SpanBuilder {
            return this
        }

        companion object {
            fun create(): NoopSpanBuilder {
                return NoopSpanBuilder()
            }
        }
    }

    companion object {
        private val INSTANCE: Tracer = DefaultTracer()
        val instance: Tracer
            get() = INSTANCE
    }
}
