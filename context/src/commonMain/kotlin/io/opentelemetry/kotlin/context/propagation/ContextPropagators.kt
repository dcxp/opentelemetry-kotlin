/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.context.propagation

/**
 * A container of the registered propagators for every supported format.
 *
 * This container can be used to access a single, composite propagator for each supported format,
 * which will be responsible for injecting and extracting data for each registered concern (traces,
 * correlations, etc). Propagation will happen through [io.opentelemetry.kotlin.context.Context],
 * from which values will be read upon injection, and which will store values from the extraction
 * step. The resulting `Context` can then be used implicitly or explicitly by the OpenTelemetry API.
 *
 * Example of usage on the client:
 *
 * <pre>`private static final Tracer tracer = OpenTelemetry.getTracer(); void onSendRequest() { try
 * (Scope ignored = span.makeCurrent()) { ContextPropagators propagators =
 * OpenTelemetry.getPropagators(); TextMapPropagator textMapPropagator =
 * propagators.getTextMapPropagator();
 *
 * // Inject the span's SpanContext and other available concerns (such as correlations) // contained
 * in the specified Context. Map<String, String> map = new HashMap<>();
 * textMapPropagator.inject(Context.current(), map, new Setter<String, String>() { public void
 * put(Map<String, String> map, String key, String value) { map.put(key, value); } }); // Send the
 * request including the text map and wait for the response. } } `</pre> *
 *
 * Example of usage in the server:
 *
 * <pre>`private static final Tracer tracer = OpenTelemetry.getTracer(); void onRequestReceived() {
 * ContextPropagators propagators = OpenTelemetry.getPropagators(); TextMapPropagator
 * textMapPropagator = propagators.getTextMapPropagator();
 *
 * // Extract and store the propagated span's SpanContext and other available concerns // in the
 * specified Context. Context context = textMapPropagator.extract(Context.current(), request, new
 * Getter<String, String>() { public String get(Object request, String key) { // Return the value
 * associated to the key, if available. } } ); Span span = tracer.spanBuilder("MyRequest")
 * .setParent(context) .setSpanKind(SpanKind.SERVER).startSpan(); try (Scope ignored =
 * span.makeCurrent()) { // Handle request and send response back. } finally { span.end(); } }
 * `</pre> *
 */
interface ContextPropagators {
    /**
     * Returns a [TextMapPropagator] propagator.
     *
     * The returned value will be a composite instance containing all the registered [ ]
     * propagators. If none is registered, the returned value will be a no-op instance.
     *
     * @return the [TextMapPropagator] propagator to inject and extract data.
     */
    val textMapPropagator: TextMapPropagator

    companion object {
        /**
         * Returns a [ContextPropagators] which can be used to extract and inject context in text
         * payloads with the given [TextMapPropagator]. Use [ ][TextMapPropagator.composite] to
         * register multiple propagators, which will all be executed when extracting or injecting.
         *
         * <pre>`ContextPropagators propagators = ContextPropagators.create(
         * TextMapPropagator.composite( HttpTraceContext.getInstance(),
         * W3CBaggagePropagator.getInstance(), new MyCustomContextPropagator())); `</pre> *
         */
        fun create(textPropagator: TextMapPropagator): ContextPropagators {
            return DefaultContextPropagators(textPropagator)
        }

        /** Returns a [ContextPropagators] which performs no injection or extraction. */
        fun noop(): ContextPropagators {
            return DefaultContextPropagators.noop()
        }
    }
}
