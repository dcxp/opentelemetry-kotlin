/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace

import io.opentelemetry.kotlin.api.trace.Span

/**
 * A combination of the write methods from the [Span] interface and the read methods from the
 * [ReadableSpan] interface.
 */
interface ReadWriteSpan : Span, ReadableSpan
