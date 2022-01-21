/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.api.trace

/**
 * Tracer is the interface for [Span] creation and interaction with the in-process context.
 *
 * Users may choose to use manual or automatic Context propagation. Because of that this class
 * offers APIs to facilitate both usages.
 *
 * The automatic context propagation is done using [io.opentelemetry.context.Context] which is a
 * gRPC independent implementation for in-process Context propagation mechanism which can carry
 * scoped-values across API boundaries and between threads. Users of the library must propagate the
 * [io.opentelemetry.context.Context] between different threads.
 *
 * Example usage with automatic context propagation:
 *
 * <pre>`class MyClass { private static final Tracer tracer =
 * openTelemetry.getTracer("instrumentation-library-name", "1.0.0"); void doWork() { Span span =
 * tracer.spanBuilder("MyClass.DoWork").startSpan(); try (Scope ignored = span.makeCurrent()) {
 * Span.current().addEvent("Starting the work."); doWorkInternal();
 * Span.current().addEvent("Finished working."); } finally { span.end(); } } } `</pre> *
 *
 * Example usage with manual context propagation:
 *
 * <pre>`class MyClass { private static final Tracer tracer =
 * openTelemetry.getTracer("instrumentation-library-name", "1.0.0"); void doWork(Span parent) { Span
 * childSpan = tracer.spanBuilder("MyChildSpan") setParent(parent).startSpan();
 * childSpan.addEvent("Starting the work."); try { doSomeWork(childSpan); // Manually propagate the
 * new span down the stack. } finally { // To make sure we end the span even in case of an
 * exception. childSpan.end(); // Manually end the span. } } } `</pre> *
 */
interface Tracer {
    /**
     * Returns a [SpanBuilder] to create and start a new [Span].
     *
     * See [SpanBuilder] for usage examples.
     *
     * @param spanName The name of the returned Span.
     * @return a `Span.Builder` to create and start a new `Span`.
     */
    fun spanBuilder(spanName: String): SpanBuilder
}
