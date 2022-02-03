package io.opentelemetry.kotlin

actual class KotlinTarget {
    actual companion object {
        actual fun isJvm(): Boolean {
            return true
        }

        actual fun isJs(): Boolean {
            return false
        }

        actual fun isNative(): Boolean {
            return false
        }
    }
}