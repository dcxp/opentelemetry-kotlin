/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.trace

import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.common.getNanoseconds
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.context.ImplicitContextKeyed
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant

/**
 * An interface that represents a span. It has an associated [SpanContext].
 *
 * Spans are created by the [SpanBuilder.startSpan] method.
 *
 * `Span` **must** be ended by calling [.end].
 */
interface Span : ImplicitContextKeyed {
    /**
     * Sets an attribute to the `Span`. If the `Span` previously contained a mapping for the key,
     * the old value is replaced by the specified value.
     *
     * If a null or empty String `value` is passed in, the behavior is undefined, and hence strongly
     * discouraged.
     *
     * Note: It is strongly recommended to use [.setAttribute], and pre-allocate your keys, if
     * possible.
     *
     * @param key the key for this attribute.
     * @param value the value for this attribute.
     * @return this.
     */
    fun setAttribute(key: String, value: String): Span {
        return setAttribute(AttributeKey.stringKey(key), value)
    }

    /**
     * Sets an attribute to the `Span`. If the `Span` previously contained a mapping for the key,
     * the old value is replaced by the specified value.
     *
     * Note: It is strongly recommended to use [.setAttribute], and pre-allocate your keys, if
     * possible.
     *
     * @param key the key for this attribute.
     * @param value the value for this attribute.
     * @return this.
     */
    fun setAttribute(key: String, value: Long): Span {
        return setAttribute(AttributeKey.longKey(key), value)
    }

    /**
     * Sets an attribute to the `Span`. If the `Span` previously contained a mapping for the key,
     * the old value is replaced by the specified value.
     *
     * Note: It is strongly recommended to use [.setAttribute], and pre-allocate your keys, if
     * possible.
     *
     * @param key the key for this attribute.
     * @param value the value for this attribute.
     * @return this.
     */
    fun setAttribute(key: String, value: Double): Span {
        return setAttribute(AttributeKey.doubleKey(key), value)
    }

    /**
     * Sets an attribute to the `Span`. If the `Span` previously contained a mapping for the key,
     * the old value is replaced by the specified value.
     *
     * Note: It is strongly recommended to use [.setAttribute], and pre-allocate your keys, if
     * possible.
     *
     * @param key the key for this attribute.
     * @param value the value for this attribute.
     * @return this.
     */
    fun setAttribute(key: String, value: Boolean): Span {
        return setAttribute(AttributeKey.booleanKey(key), value)
    }

    /**
     * Sets an attribute to the `Span`. If the `Span` previously contained a mapping for the key,
     * the old value is replaced by the specified value.
     *
     * Note: the behavior of null values is undefined, and hence strongly discouraged.
     *
     * @param key the key for this attribute.
     * @param value the value for this attribute.
     * @return this.
     */
    fun <T : Any> setAttribute(key: AttributeKey<T>, value: T): Span

    /**
     * Sets an attribute to the `Span`. If the `Span` previously contained a mapping for the key,
     * the old value is replaced by the specified value.
     *
     * @param key the key for this attribute.
     * @param value the value for this attribute.
     * @return this.
     */
    fun setAttribute(key: AttributeKey<Long>, value: Int): Span {
        return setAttribute<Long>(key, value.toLong())
    }

    /**
     * Sets attributes to the [Span]. If the [Span] previously contained a mapping for any of the
     * keys, the old values are replaced by the specified values.
     *
     * @param attributes the attributes
     * @return this.
     * @since 1.2.0
     */
    @Suppress("UNCHECKED_CAST")
    fun setAllAttributes(attributes: Attributes): Span {
        if (attributes.isEmpty()) {
            return this
        }
        attributes.forEach { attributeKey, value ->
            this.setAttribute(attributeKey as AttributeKey<Any>, value)
        }
        return this
    }

    /**
     * Adds an event to the [Span]. The timestamp of the event will be the current time.
     *
     * @param name the name of the event.
     * @return this.
     */
    fun addEvent(name: String): Span {
        return addEvent(name, Attributes.empty())
    }

    /**
     * Adds an event to the [Span] with the given `timestamp`, as nanos since epoch. Note, this
     * `timestamp` is not the same as [System.nanoTime] but may be computed using it, for example,
     * by taking a difference of readings from [System.nanoTime] and adding to the span start time.
     *
     * When possible, it is preferred to use [.addEvent] at the time the event occurred.
     *
     * @param name the name of the event.
     * @param timestamp the explicit event timestamp since epoch.
     * @param unit the unit of the timestamp
     * @return this.
     */
    fun addEvent(name: String, timestamp: Long, unit: DateTimeUnit): Span {
        return addEvent(name, Attributes.empty(), timestamp, unit)
    }

    /**
     * Adds an event to the [Span] with the given `timestamp`, as nanos since epoch. Note, this
     * `timestamp` is not the same as [System.nanoTime] but may be computed using it, for example,
     * by taking a difference of readings from [System.nanoTime] and adding to the span start time.
     *
     * When possible, it is preferred to use [.addEvent] at the time the event occurred.
     *
     * @param name the name of the event.
     * @param timestamp the explicit event timestamp since epoch.
     * @return this.
     */
    fun addEvent(name: String, timestamp: Instant): Span {
        return addEvent(name, timestamp.getNanoseconds(), DateTimeUnit.NANOSECOND)
    }

    /**
     * Adds an event to the [Span] with the given [Attributes]. The timestamp of the event will be
     * the current time.
     *
     * @param name the name of the event.
     * @param attributes the attributes that will be added; these are associated with this event,
     * not the `Span` as for `setAttribute()`.
     * @return this.
     */
    fun addEvent(name: String, attributes: Attributes): Span

    /**
     * Adds an event to the [Span] with the given [Attributes] and `timestamp`. Note, this
     * `timestamp` is not the same as [System.nanoTime] but may be computed using it, for example,
     * by taking a difference of readings from [System.nanoTime] and adding to the span start time.
     *
     * When possible, it is preferred to use [.addEvent] at the time the event occurred.
     *
     * @param name the name of the event.
     * @param attributes the attributes that will be added; these are associated with this event,
     * not the `Span` as for `setAttribute()`.
     * @param timestamp the explicit event timestamp since epoch.
     * @param unit the unit of the timestamp
     * @return this.
     */
    fun addEvent(name: String, attributes: Attributes, timestamp: Long, unit: DateTimeUnit): Span

    /**
     * Adds an event to the [Span] with the given [Attributes] and `timestamp`. Note, this
     * `timestamp` is not the same as [System.nanoTime] but may be computed using it, for example,
     * by taking a difference of readings from [System.nanoTime] and adding to the span start time.
     *
     * When possible, it is preferred to use [.addEvent] at the time the event occurred.
     *
     * @param name the name of the event.
     * @param attributes the attributes that will be added; these are associated with this event,
     * not the `Span` as for `setAttribute()`.
     * @param timestamp the explicit event timestamp since epoch.
     * @return this.
     */
    fun addEvent(name: String, attributes: Attributes, timestamp: Instant): Span {
        return addEvent(name, attributes, timestamp.getNanoseconds(), DateTimeUnit.NANOSECOND)
    }

    /**
     * Sets the status to the `Span`.
     *
     * If used, this will override the default `Span` status. Default status code is [ ]
     * [StatusCode.UNSET].
     *
     * Only the value of the last call will be recorded, and implementations are free to ignore
     * previous calls.
     *
     * @param statusCode the [StatusCode] to set.
     * @return this.
     */
    fun setStatus(statusCode: io.opentelemetry.kotlin.api.trace.StatusCode): Span {
        return setStatus(statusCode, "")
    }

    /**
     * Sets the status to the `Span`.
     *
     * If used, this will override the default `Span` status. Default status code is [ ]
     * [StatusCode.UNSET].
     *
     * Only the value of the last call will be recorded, and implementations are free to ignore
     * previous calls.
     *
     * @param statusCode the [StatusCode] to set.
     * @param description the description of the `Status`.
     * @return this.
     */
    fun setStatus(
        statusCode: io.opentelemetry.kotlin.api.trace.StatusCode,
        description: String
    ): Span
    /**
     * Records information about the [Throwable] to the [Span].
     *
     * @param exception the [Throwable] to record.
     * @param additionalAttributes the additional [Attributes] to record.
     * @return this.
     */
    /**
     * Records information about the [Throwable] to the [Span].
     *
     * Note that the EXCEPTION_ESCAPED value from the Semantic Conventions cannot be determined by
     * this function. You should record this attribute manually using [ ][.recordException] if you
     * know that an exception is escaping.
     *
     * @param exception the [Throwable] to record.
     * @return this.
     */
    fun recordException(
        exception: Throwable,
        additionalAttributes: Attributes = Attributes.empty()
    ): Span

    /**
     * Updates the `Span` name.
     *
     * If used, this will override the name provided via `Span.Builder`.
     *
     * Upon this update, any sampling behavior based on `Span` name will depend on the
     * implementation.
     *
     * @param name the `Span` name.
     * @return this.
     */
    fun updateName(name: String): Span

    /**
     * Marks the end of `Span` execution.
     *
     * Only the timing of the first end call for a given `Span` will be recorded, and
     * implementations are free to ignore all further calls.
     */
    fun end()

    /**
     * Marks the end of `Span` execution with the specified timestamp.
     *
     * Only the timing of the first end call for a given `Span` will be recorded, and
     * implementations are free to ignore all further calls.
     *
     * Use this method for specifying explicit end options, such as end `Timestamp`. When no
     * explicit values are required, use [.end].
     *
     * @param timestamp the explicit timestamp from the epoch, for this `Span`. `0` indicates
     * current time should be used.
     * @param unit the unit of the timestamp
     */
    fun end(timestamp: Long, unit: DateTimeUnit)

    /**
     * Marks the end of `Span` execution with the specified timestamp.
     *
     * Only the timing of the first end call for a given `Span` will be recorded, and
     * implementations are free to ignore all further calls.
     *
     * Use this method for specifying explicit end options, such as end `Timestamp`. When no
     * explicit values are required, use [.end].
     *
     * @param timestamp the explicit timestamp from the epoch, for this `Span`. `0` indicates
     * current time should be used.
     */
    fun end(timestamp: Instant) {
        end(timestamp.getNanoseconds(), DateTimeUnit.NANOSECOND)
    }

    /**
     * Returns the `SpanContext` associated with this `Span`.
     *
     * @return the `SpanContext` associated with this `Span`.
     */
    val spanContext: SpanContext

    /**
     * Returns `true` if this `Span` records tracing events (e.g. [ ][.addEvent], [.setAttribute]).
     *
     * @return `true` if this `Span` records tracing events.
     */
    fun isRecording(): Boolean

    override fun storeInContext(context: Context): Context {
        return context.with(SpanContextKey.KEY, this)
    }

    companion object {
        /**
         * Returns the [Span] from the current [Context], falling back to a default, no-op [Span] if
         * there is no span in the current context.
         */
        fun current(): Span {
            return Context.current().getOrElse(SpanContextKey.KEY, invalid())
        }

        /**
         * Returns the [Span] from the specified [Context], falling back to a default, no-op [Span]
         * if there is no span in the context.
         */
        fun fromContext(context: Context): Span {
            return context.getOrElse(SpanContextKey.KEY, invalid())
        }

        /**
         * Returns the [Span] from the specified [Context], or `null` if there is no span in the
         * context.
         */
        fun fromContextOrNull(context: Context): Span? {
            return context[SpanContextKey.KEY]
        }

        /**
         * Returns an invalid [Span]. An invalid [Span] is used when tracing is disabled, usually
         * because there is no OpenTelemetry SDK installed.
         */
        fun invalid(): Span {
            return PropagatedSpan.INVALID
        }

        /**
         * Returns a non-recording [Span] that holds the provided [SpanContext] but has no
         * functionality. It will not be exported and all tracing operations are no-op, but it can
         * be used to propagate a valid [SpanContext] downstream.
         */
        fun wrap(spanContext: SpanContext): Span {
            return if (!spanContext.isValid) {
                invalid()
            } else PropagatedSpan.create(spanContext)
        }
    }
}
