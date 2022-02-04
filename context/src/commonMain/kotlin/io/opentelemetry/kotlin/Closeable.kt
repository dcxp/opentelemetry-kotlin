package io.opentelemetry.kotlin

interface Closeable {
    fun close()
}

inline fun <T : Closeable, R> T.use(block: (T) -> R): R {
    try {
        return block(this)
    } finally {
        close()
    }
}

inline fun <T : Closeable> T.use(block: (T) -> Unit) {
    try {
        block(this)
    } finally {
        close()
    }
}
