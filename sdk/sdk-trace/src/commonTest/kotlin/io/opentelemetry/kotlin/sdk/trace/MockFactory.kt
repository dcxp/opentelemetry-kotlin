package io.opentelemetry.kotlin.sdk.trace

import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.trace.Span
import io.opentelemetry.kotlin.api.trace.SpanContext
import io.opentelemetry.kotlin.api.trace.SpanKind
import io.opentelemetry.kotlin.api.trace.StatusCode
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.common.InstrumentationLibraryInfo
import io.opentelemetry.kotlin.sdk.trace.data.SpanData
import kotlinx.atomicfu.atomic
import kotlinx.datetime.DateTimeUnit

object MockFactory {

    fun createSpanProcessor(
        isStartRequired: Boolean = true,
        isEndRequired: Boolean = true
    ): MockSpanProcessor {
        return MockSpanProcessor(isStartRequired, isEndRequired)
    }
    class MockSpanProcessor(
        private val isStartRequired: Boolean,
        private val isEndRequired: Boolean
    ) : SpanProcessor {
        val startContext: Context?
            get() = startContextInternal.value
        val startSpan: ReadWriteSpan?
            get() = startSpanInternal.value
        val endSpan: ReadableSpan?
            get() = endSpanInternal.value
        val flushCalled: Boolean
            get() = flushCalledInternal.value
        val shutdownCalled: Boolean
            get() = shutdownCalledInternal.value

        val startContextInternal = atomic<Context?>(null)
        val startSpanInternal = atomic<ReadWriteSpan?>(null)
        val endSpanInternal = atomic<ReadableSpan?>(null)
        val flushCalledInternal = atomic(false)
        val shutdownCalledInternal = atomic(false)

        override fun onStart(parentContext: Context, span: ReadWriteSpan) {
            startContextInternal.value = parentContext
            startSpanInternal.value = span
        }

        override fun isStartRequired(): Boolean {
            return isStartRequired
        }

        override fun onEnd(span: ReadableSpan) {
            endSpanInternal.value = span
        }

        override fun isEndRequired(): Boolean {
            return isEndRequired
        }

        override fun forceFlush(): CompletableResultCode {
            flushCalledInternal.value = true
            return CompletableResultCode.ofSuccess()
        }

        override fun shutdown(): CompletableResultCode {
            shutdownCalledInternal.value = true
            return CompletableResultCode.ofSuccess()
        }

        fun reset() {
            startContextInternal.value = null
            startSpanInternal.value = null
            endSpanInternal.value = null
            flushCalledInternal.value = false
            shutdownCalledInternal.value = false
        }
    }
    fun createReadWriteSpan(): ReadWriteSpan {
        return object : ReadWriteSpan {
            override fun <T : Any> setAttribute(key: AttributeKey<T>, value: T): Span {
                TODO("Not yet implemented")
            }

            override fun addEvent(name: String, attributes: Attributes): Span {
                TODO("Not yet implemented")
            }

            override fun addEvent(
                name: String,
                attributes: Attributes,
                timestamp: Long,
                unit: DateTimeUnit
            ): Span {
                TODO("Not yet implemented")
            }

            override fun setStatus(statusCode: StatusCode, description: String): Span {
                TODO("Not yet implemented")
            }

            override fun recordException(
                exception: Throwable,
                additionalAttributes: Attributes
            ): Span {
                TODO("Not yet implemented")
            }

            override fun updateName(name: String): Span {
                TODO("Not yet implemented")
            }

            override fun end() {
                TODO("Not yet implemented")
            }

            override fun end(timestamp: Long, unit: DateTimeUnit) {
                TODO("Not yet implemented")
            }

            override val spanContext: SpanContext
                get() = TODO("Not yet implemented")

            override fun isRecording(): Boolean {
                TODO("Not yet implemented")
            }

            override val parentSpanContext: SpanContext
                get() = TODO("Not yet implemented")
            override val name: String?
                get() = TODO("Not yet implemented")

            override fun toSpanData(): SpanData {
                TODO("Not yet implemented")
            }

            override val instrumentationLibraryInfo: InstrumentationLibraryInfo
                get() = TODO("Not yet implemented")

            override fun hasEnded(): Boolean {
                TODO("Not yet implemented")
            }

            override val latencyNanos: Long
                get() = TODO("Not yet implemented")
            override val kind: SpanKind
                get() = TODO("Not yet implemented")

            override fun <T : Any> getAttribute(key: AttributeKey<T>): T? {
                TODO("Not yet implemented")
            }
        }
    }
}
