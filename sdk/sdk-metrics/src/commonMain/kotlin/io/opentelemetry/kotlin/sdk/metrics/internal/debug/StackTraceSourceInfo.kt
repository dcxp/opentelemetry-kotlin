/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.metrics.internal.debug

/*
/** Diagnostic information derived from stack traces. */
internal class StackTraceSourceInfo(stackTraceElements: Array<StackTraceElement>) : SourceInfo {
    private val stackTraceElements: Array<StackTraceElement>

    init {
        this.stackTraceElements = stackTraceElements
    }

    override fun shortDebugString(): String {
        if (stackTraceElements.size > 0) {
            for (e in stackTraceElements) {
                if (isInterestingStackTrace(e)) {
                    return String.format("%s:%d", e.getFileName(), e.getLineNumber())
                }
            }
        }
        return "unknown source"
    }

    override fun multiLineDebugString(): String {
        if (stackTraceElements.size > 0) {
            // TODO - Limit trace length
            val result = StringBuilder()
            for (e in stackTraceElements) {
                if (isInterestingStackTrace(e)) {
                    result.append("\tat ").append(e).append("\n")
                }
            }
            return result.toString()
        }
        return "\tat unknown source"
    }

    companion object {
        private fun isInterestingStackTrace(e: StackTraceElement): Boolean {
            return (!e.getClassName().startsWith("io.opentelemetry.kotlin.sdk.metrics") &&
                !e.getClassName().startsWith("java.lang"))
        }
    }
}
*/
