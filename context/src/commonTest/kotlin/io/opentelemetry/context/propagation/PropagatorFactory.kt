package io.opentelemetry.context.propagation

import io.opentelemetry.context.Context

class PropagatorFactory {
    companion object {
        fun createDummyTextMapPropagator(): TextMapPropagator {
            return createTextMapPropagatorWithFields()
        }

        fun createTextMapPropagatorWithFields(vararg fields: String): TextMapPropagator {
            return ObservableTextMapPropagator(fields.toHashSet())
        }

        fun createObservableTextMapPropagator(): ObservableTextMapPropagator {
            return ObservableTextMapPropagator(listOf())
        }

        class ObservableTextMapPropagator(val fieldNames: Collection<String>) : TextMapPropagator {
            private var internalInjectCount = 0
            val injectCount: Int
                get() {
                    return internalInjectCount
                }

            private var internalExtractCount = 0
            val extractCount: Int
                get() {
                    return internalExtractCount
                }

            override fun fields(): Collection<String> {
                return fieldNames
            }

            override fun <C> inject(context: Context, carrier: C, setter: TextMapSetter<C>) {
                internalInjectCount++
            }

            override fun <C> extract(
                context: Context,
                carrier: C,
                getter: TextMapGetter<C>
            ): Context {
                internalExtractCount++
                return context
            }
        }
    }
}
