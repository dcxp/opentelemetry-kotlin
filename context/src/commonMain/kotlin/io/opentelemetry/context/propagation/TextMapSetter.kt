/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.context.propagation

/**
 * Class that allows a `TextMapPropagator` to set propagated fields into a carrier.
 *
 * `Setter` is stateless and allows to be saved as a constant to avoid runtime allocations.
 *
 * @param <C> carrier of propagation fields, such as an http request </C>
 */
fun interface TextMapSetter<C> {
    /**
     * Replaces a propagated field with the given value.
     *
     * For example, a setter for an [java.net.HttpURLConnection] would be the method reference
     * [java.net.HttpURLConnection.addRequestProperty]
     *
     * @param carrier holds propagation fields. For example, an outgoing message or http request. To
     * facilitate implementations as java lambdas, this parameter may be null.
     * @param key the key of the field.
     * @param value the value of the field.
     */
    operator fun set(carrier: C, key: String, value: String?)
}
