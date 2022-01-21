/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.context

/**
 * A Java SPI (Service Provider Interface) to allow replacing the default [ContextStorage]. This can
 * be useful if, for example, you want to store OpenTelemetry [Context] in another context
 * propagation system. For example, the returned [ContextStorage] could delegate to methods in
 *
 * [`com.linecorp.armeria.common.RequestContext`](https://javadoc.io/doc/com.linecorp.armeria/armeria-javadoc/latest/com/linecorp/armeria/common/RequestContext.html)
 * , [`io.grpc.context.Context`](https://grpc.github.io/grpc-java/javadoc/io/grpc/Context.html), or
 * [`org.eclipse.microprofile.context.ThreadContext`](https://download.eclipse.org/microprofile/microprofile-context-propagation-1.0.2/apidocs/org/eclipse/microprofile/context/ThreadContext.html)
 *
 * if you are already using one of those systems in your application. Then you would not have to use
 * methods like [Context.wrap] and can use your current system instead.
 */
interface ContextStorageProvider {
    /** Returns the [ContextStorage] to use to store [Context]. */
    fun get(): ContextStorage
}
