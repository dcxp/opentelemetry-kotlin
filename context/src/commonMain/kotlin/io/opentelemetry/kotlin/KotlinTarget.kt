package io.opentelemetry.kotlin

expect class KotlinTarget {
    companion object {
        fun isJvm(): Boolean
        fun isJs(): Boolean
        fun isNative(): Boolean
    }
}

