package io.opentelemetry.kotlin.context

import kotlinx.atomicfu.atomic

class ArrayBasedContextStorage : ContextStorage {
    val context = atomic(ArrayBasedContext.root())

    override fun attach(toAttach: Context): Scope {
        val oldContext = context.value
        context.value = toAttach
        return ContextScope { context.value = oldContext }
    }

    override fun current(): Context {
        return context.value
    }

    private class ContextScope(private val reset: () -> Unit) : Scope {
        override fun close() {
            reset()
        }
    }
}
