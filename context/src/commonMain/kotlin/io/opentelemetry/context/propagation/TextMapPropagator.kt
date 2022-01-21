/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.context.propagation

import io.opentelemetry.context.Context

/**
 * Injects and extracts a value as text into carriers that travel in-band across process boundaries.
 * Encoding is expected to conform to the HTTP Header Field semantics. Values are often encoded as
 * RPC/HTTP request headers.
 *
 * The carrier of propagated data on both the client (injector) and server (extractor) side is
 * usually an http request. Propagation is usually implemented via library- specific request
 * interceptors, where the client-side injects values and the server-side extracts them.
 *
 * Specific concern values (traces, correlations, etc) will be read from the specified `Context`,
 * and resulting values will be stored in a new `Context` upon extraction. It is recommended to use
 * a single `Context.Key` to store the entire concern data:
 *
 * <pre>`public static final Context.Key CONCERN_KEY = Context.key("my-concern-key"); public
 * MyConcernPropagator implements TextMapPropagator { public <C> void inject(Context context, C
 * carrier, Setter<C> setter) { Object concern = CONCERN_KEY.get(context); // Use concern in the
 * specified context to propagate data. } public <C> Context extract(Context context, C carrier,
 * Getter<C> getter) { // Use getter to get the data from the carrier. return
 * context.withValue(CONCERN_KEY, concern); } } `</pre> *
 */
interface TextMapPropagator {
    /**
     * The propagation fields defined. If your carrier is reused, you should delete the fields here
     * before calling [.inject] )}.
     *
     * For example, if the carrier is a single-use or immutable request object, you don't need to
     * clear fields as they couldn't have been set before. If it is a mutable, retryable object,
     * successive calls should clear these fields first.
     *
     * Some use cases for this are:
     *
     * * Allow pre-allocation of fields, especially in systems like gRPC Metadata
     * * Allow a single-pass over an iterator
     *
     * @return the fields that will be used by this formatter.
     */
    fun fields(): Collection<String>

    /**
     * Injects data for downstream consumers, for example as HTTP headers. The carrier may be null
     * to facilitate calling this method with a lambda for the [TextMapSetter], in which case that
     * null will be passed to the [TextMapSetter] implementation.
     *
     * @param context the `Context` containing the value to be injected.
     * @param carrier holds propagation fields. For example, an outgoing message or http request.
     * @param setter invoked for each propagation key to add or remove.
     * @param <C> carrier of propagation fields, such as an http request </C>
     */
    fun <C> inject(context: Context, carrier: C, setter: TextMapSetter<C>)

    fun <C> inject(carrier: C, setter: TextMapSetter<C>) {
        inject(Context.root(), carrier, setter)
    }

    /**
     * Extracts data from upstream. For example, from incoming http headers. The returned Context
     * should contain the extracted data, if any, merged with the data from the passed-in Context.
     *
     * If the incoming information could not be parsed, implementations MUST return the original
     * Context, unaltered.
     *
     * @param context the `Context` used to store the extracted value.
     * @param carrier holds propagation fields. For example, an outgoing message or http request.
     * @param getter invoked for each propagation key to get data from the carrier.
     * @param <C> the type of carrier of the propagation fields, such as an http request.
     * @return the `Context` containing the extracted data. </C>
     */
    fun <C> extract(context: Context, carrier: C, getter: TextMapGetter<C>): Context

    fun <C> extract(carrier: C, getter: TextMapGetter<C>): Context {
        return extract(Context.root(), carrier, getter)
    }

    companion object {
        /**
         * Returns a [TextMapPropagator] which simply delegates injection and extraction to the
         * provided propagators.
         *
         * Invocation order of `TextMapPropagator#inject()` and `TextMapPropagator#extract()` for
         * registered trace propagators is undefined.
         */
        fun composite(vararg propagators: TextMapPropagator): TextMapPropagator {
            return composite(propagators.toList())
        }

        /**
         * Returns a [TextMapPropagator] which simply delegates injection and extraction to the
         * provided propagators.
         *
         * Invocation order of `TextMapPropagator#inject()` and `TextMapPropagator#extract()` for
         * registered trace propagators is undefined.
         */
        fun composite(propagators: List<TextMapPropagator>): TextMapPropagator {
            if (propagators.isEmpty()) {
                return NoopTextMapPropagator.instance
            }
            return if (propagators.size == 1) {
                propagators.first()
            } else MultiTextMapPropagator(propagators)
        }

        /** Returns a [TextMapPropagator] which does no injection or extraction. */
        fun noop(): TextMapPropagator {
            return NoopTextMapPropagator.instance
        }
    }
}
