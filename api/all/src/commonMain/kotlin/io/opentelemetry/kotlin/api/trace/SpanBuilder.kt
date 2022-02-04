/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.api.trace

import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.api.common.getNanoseconds
import io.opentelemetry.kotlin.context.Context
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant

/**
 * [SpanBuilder] is used to construct [Span] instances which define arbitrary scopes of code that
 * are sampled for distributed tracing as a single atomic unit.
 *
 * This is a simple example where all the work is being done within a single scope and a single
 * thread and the Context is automatically propagated:
 *
 * <pre>`class MyClass { private static final Tracer tracer =
 * OpenTelemetry.get().getTracer("com.anyco.rpc");
 *
 * void doWork { // Create a Span as a child of the current Span. Span span =
 * tracer.spanBuilder("MyChildSpan").startSpan(); try (Scope ss = span.makeCurrent()) {
 * span.addEvent("my event"); doSomeWork(); // Here the new span is in the current Context, so it
 * can be used // implicitly anywhere down the stack. } finally { span.end(); } } } `</pre> *
 *
 * There might be cases where you do not perform all the work inside one static scope and the
 * Context is automatically propagated:
 *
 * <pre>`class MyRpcServerInterceptorListener implements RpcServerInterceptor.Listener { private
 * static final Tracer tracer = OpenTelemetry.get().getTracer("com.example.rpc"); private Span
 * mySpan;
 *
 * public MyRpcInterceptor() {}
 *
 * public void onRequest(String rpcName, Metadata metadata) { // Create a Span as a child of the
 * remote Span. mySpan = tracer.spanBuilder(rpcName)
 * .setParent(extractContextFromMetadata(metadata)).startSpan(); }
 *
 * public void onExecuteHandler(ServerCallHandler serverCallHandler) { try (Scope ws =
 * mySpan.makeCurrent()) { Span.current().addEvent("Start rpc execution."); serverCallHandler.run();
 * // Here the new span is in the current Context, so it can be // used implicitly anywhere down the
 * stack. } }
 *
 * // Called when the RPC is canceled and guaranteed onComplete will not be called. public void
 * onCancel() { // IMPORTANT: DO NOT forget to ended the Span here as the work is done.
 * mySpan.setStatus(StatusCode.ERROR); mySpan.end(); }
 *
 * // Called when the RPC is done and guaranteed onCancel will not be called. public void
 * onComplete(RpcStatus rpcStatus) { // IMPORTANT: DO NOT forget to ended the Span here as the work
 * is done. mySpan.setStatus(rpcStatusToCanonicalTraceStatus(status); mySpan.end(); } } `</pre> *
 *
 * This is a simple example where all the work is being done within a single scope and the Context
 * is manually propagated:
 *
 * <pre>`class MyClass { private static final Tracer tracer =
 * OpenTelemetry.get().getTracer("com.example.rpc");
 *
 * void doWork(Span parent) { Span childSpan = tracer.spanBuilder("MyChildSpan")
 * .setParent(Context.current().with(parent)) .startSpan(); childSpan.addEvent("my event"); try {
 * doSomeWork(childSpan); // Manually propagate the new span down the stack. } finally { // To make
 * sure we end the span even in case of an exception. childSpan.end(); // Manually end the span. } }
 * } `</pre> *
 *
 * see [SpanBuilder.startSpan] for usage examples.
 */
interface SpanBuilder {
    /**
     * Sets the parent to use from the specified `Context`. If not set, the value of
     * `Span.current()` at [.startSpan] time will be used as parent.
     *
     * If no [Span] is available in the specified `Context`, the resulting `Span` will become a root
     * instance, as if [.setNoParent] had been called.
     *
     * If called multiple times, only the last specified value will be used. Observe that the state
     * defined by a previous call to [.setNoParent] will be discarded.
     *
     * @param context the `Context`.
     * @return this.
     */
    fun setParent(context: Context): SpanBuilder

    /**
     * Sets the option to become a root `Span` for a new trace. If not set, the value of
     * `Span.current()` at [.startSpan] time will be used as parent.
     *
     * Observe that any previously set parent will be discarded.
     *
     * @return this.
     */
    fun setNoParent(): SpanBuilder

    /**
     * Adds a link to the newly created `Span`.
     *
     * Links are used to link [Span]s in different traces. Used (for example) in batching
     * operations, where a single batch handler processes multiple requests from different traces or
     * the same trace.
     *
     * Implementations may ignore calls with an [invalid span][SpanContext.isValid].
     *
     * @param spanContext the context of the linked `Span`.
     * @return this.
     */
    fun addLink(spanContext: SpanContext): SpanBuilder

    /**
     * Adds a link to the newly created `Span`.
     *
     * Links are used to link [Span]s in different traces. Used (for example) in batching
     * operations, where a single batch handler processes multiple requests from different traces or
     * the same trace.
     *
     * Implementations may ignore calls with an [invalid span][SpanContext.isValid].
     *
     * @param spanContext the context of the linked `Span`.
     * @param attributes the attributes of the `Link`.
     * @return this.
     */
    fun addLink(spanContext: SpanContext, attributes: Attributes): SpanBuilder

    /**
     * Sets an attribute to the newly created `Span`. If `SpanBuilder` previously contained a
     * mapping for the key, the old value is replaced by the specified value.
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
    fun setAttribute(key: String, value: String): SpanBuilder

    /**
     * Sets an attribute to the newly created `Span`. If `SpanBuilder` previously contained a
     * mapping for the key, the old value is replaced by the specified value.
     *
     * Note: It is strongly recommended to use [.setAttribute], and pre-allocate your keys, if
     * possible.
     *
     * @param key the key for this attribute.
     * @param value the value for this attribute.
     * @return this.
     */
    fun setAttribute(key: String, value: Long): SpanBuilder

    /**
     * Sets an attribute to the newly created `Span`. If `SpanBuilder` previously contained a
     * mapping for the key, the old value is replaced by the specified value.
     *
     * Note: It is strongly recommended to use [.setAttribute], and pre-allocate your keys, if
     * possible.
     *
     * @param key the key for this attribute.
     * @param value the value for this attribute.
     * @return this.
     */
    fun setAttribute(key: String, value: Double): SpanBuilder

    /**
     * Sets an attribute to the newly created `Span`. If `SpanBuilder` previously contained a
     * mapping for the key, the old value is replaced by the specified value.
     *
     * Note: It is strongly recommended to use [.setAttribute], and pre-allocate your keys, if
     * possible.
     *
     * @param key the key for this attribute.
     * @param value the value for this attribute.
     * @return this.
     */
    fun setAttribute(key: String, value: Boolean): SpanBuilder

    /**
     * Sets an attribute to the newly created `Span`. If `SpanBuilder` previously contained a
     * mapping for the key, the old value is replaced by the specified value.
     *
     * Note: the behavior of null values is undefined, and hence strongly discouraged.
     *
     * @param key the key for this attribute.
     * @param value the value for this attribute.
     * @return this.
     */
    fun <T : Any> setAttribute(key: AttributeKey<T>, value: T): SpanBuilder

    /**
     * Sets attributes to the [SpanBuilder]. If the [SpanBuilder] previously contained a mapping for
     * any of the keys, the old values are replaced by the specified values.
     *
     * @param attributes the attributes
     * @return this.
     * @since 1.2.0
     */
    @Suppress("UNCHECKED_CAST")
    fun setAllAttributes(attributes: Attributes): SpanBuilder {
        if (attributes.isEmpty()) {
            return this
        }
        attributes.forEach { attributeKey, value ->
            setAttribute(attributeKey as AttributeKey<Any>, value)
        }
        return this
    }

    /**
     * Sets the [SpanKind] for the newly created `Span`. If not called, the implementation will
     * provide a default value [SpanKind.INTERNAL].
     *
     * @param spanKind the kind of the newly created `Span`.
     * @return this.
     */
    fun setSpanKind(spanKind: SpanKind): SpanBuilder

    /**
     * Sets an explicit start timestamp for the newly created `Span`.
     *
     * LIRInstruction.Use this method to specify an explicit start timestamp. If not called, the
     * implementation will use the timestamp value at [.startSpan] time, which should be the default
     * case.
     *
     * Important this is NOT equivalent with System.nanoTime().
     *
     * @param startTimestamp the explicit start timestamp from the epoch of the newly created
     * `Span`.
     * @param unit the unit of the timestamp.
     * @return this.
     */
    fun setStartTimestamp(startTimestamp: Long, unit: DateTimeUnit): SpanBuilder

    /**
     * Sets an explicit start timestamp for the newly created `Span`.
     *
     * Use this method to specify an explicit start timestamp. If not called, the implementation
     * will use the timestamp value at [.startSpan] time, which should be the default case.
     *
     * Important this is NOT equivalent with System.nanoTime().
     *
     * @param startTimestamp the explicit start timestamp from the epoch of the newly created
     * `Span`.
     * @return this.
     */
    fun setStartTimestamp(startTimestamp: Instant): SpanBuilder {
        return setStartTimestamp(startTimestamp.getNanoseconds(), DateTimeUnit.NANOSECOND)
    }

    /**
     * Starts a new [Span].
     *
     * Users **must** manually call [Span.end] to end this `Span`.
     *
     * Does not install the newly created `Span` to the current Context.
     *
     * IMPORTANT: This method can be called only once per [SpanBuilder] instance and as the last
     * method called. After this method is called calling any method is undefined behavior.
     *
     * Example of usage:
     *
     * <pre>`class MyClass { private static final Tracer tracer =
     * OpenTelemetry.get().getTracer("com.example.rpc");
     *
     * void doWork(Span parent) { Span childSpan = tracer.spanBuilder("MyChildSpan")
     * .setParent(Context.current().with(parent)) .startSpan(); childSpan.addEvent("my event"); try
     * { doSomeWork(childSpan); // Manually propagate the new span down the stack. } finally { // To
     * make sure we end the span even in case of an exception. childSpan.end(); // Manually end the
     * span. } } } `</pre> *
     *
     * @return the newly created `Span`.
     */
    fun startSpan(): Span
}
