package io.opentelemetry

actual class KotlinTarget {
    actual companion object {
        actual fun isJvm(): Boolean {
            return false
        }

        actual fun isJs(): Boolean {
            return false
        }

        actual fun isNative(): Boolean {
            return true
        }
    }
}
