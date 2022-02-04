package io.opentelemetry.kotlin

actual class KotlinTarget {
    actual companion object {
        actual fun isJvm(): Boolean {
            return false
        }

        actual fun isJs(): Boolean {
            return true
        }

        actual fun isNative(): Boolean {
            return false
        }
    }
}
