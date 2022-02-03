package io.opentelemetry.kotlin.sdk.metrics.exemplar.mock

import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.context.Context
import io.opentelemetry.kotlin.sdk.metrics.internal.view.AttributesProcessor
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.collections.immutable.persistentListOf

class AttributesProcessorMock(private val wrappedProcessor: AttributesProcessor? = null) :
    AttributesProcessor() {

    private val store = atomic(persistentListOf<ProcessCall>())

    val calls: List<ProcessCall>
        get() {
            return store.value
        }

    override fun process(incoming: Attributes, context: Context): Attributes {
        store.update { it.add(ProcessCall(incoming, context)) }
        return if (wrappedProcessor == null) {
            incoming
        } else {
            wrappedProcessor.process(incoming, context)
        }
    }

    override fun usesContext(): Boolean {
        return if (wrappedProcessor == null) {
            false
        } else {
            wrappedProcessor.usesContext()
        }
    }

    data class ProcessCall(val incoming: Attributes, val context: Context)
}
