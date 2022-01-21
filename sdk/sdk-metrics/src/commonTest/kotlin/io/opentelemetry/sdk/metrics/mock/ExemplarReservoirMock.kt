package io.opentelemetry.sdk.metrics.mock

import io.opentelemetry.api.common.Attributes
import io.opentelemetry.context.Context
import io.opentelemetry.sdk.metrics.data.ExemplarData
import io.opentelemetry.sdk.metrics.exemplar.ExemplarReservoir

class ExemplarReservoirMock(private val result: List<ExemplarData> = listOf()) : ExemplarReservoir {
    var value: Double = 0.0
    var attributes: Attributes = Attributes.empty()
    var context: Context = Context.root()

    constructor(vararg exemplarData: ExemplarData) : this(exemplarData.toList())

    override fun offerMeasurement(value: Long, attributes: Attributes, context: Context) {
        this.value = value.toDouble()
        this.attributes = attributes
        this.context = context
    }

    override fun offerMeasurement(value: Double, attributes: Attributes, context: Context) {
        this.value = value
        this.attributes = attributes
        this.context = context
    }

    override fun collectAndReset(pointAttributes: Attributes): List<ExemplarData> {
        return result
    }
}
