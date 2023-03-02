@file:OptIn(pbandk.PublicForGeneratedCode::class)

package io.opentelemetry.proto.collector.metrics.v1

@pbandk.Export
public data class ExportMetricsServiceRequest(
    val resourceMetrics: List<io.opentelemetry.proto.metrics.v1.ResourceMetrics> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest> {
        public val defaultInstance: io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest by lazy { io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest = io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest, *>>(1)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "resource_metrics",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.metrics.v1.ResourceMetrics>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.metrics.v1.ResourceMetrics.Companion)),
                        jsonName = "resourceMetrics",
                        value = io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest::resourceMetrics
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest",
                messageClass = io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class ExportMetricsServiceResponse(
    val partialSuccess: io.opentelemetry.proto.collector.metrics.v1.ExportMetricsPartialSuccess? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse> {
        public val defaultInstance: io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse by lazy { io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse = io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse, *>>(1)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "partial_success",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.collector.metrics.v1.ExportMetricsPartialSuccess.Companion),
                        jsonName = "partialSuccess",
                        value = io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse::partialSuccess
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse",
                messageClass = io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class ExportMetricsPartialSuccess(
    val rejectedDataPoints: Long = 0L,
    val errorMessage: String = "",
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.collector.metrics.v1.ExportMetricsPartialSuccess = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.collector.metrics.v1.ExportMetricsPartialSuccess> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.collector.metrics.v1.ExportMetricsPartialSuccess> {
        public val defaultInstance: io.opentelemetry.proto.collector.metrics.v1.ExportMetricsPartialSuccess by lazy { io.opentelemetry.proto.collector.metrics.v1.ExportMetricsPartialSuccess() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.collector.metrics.v1.ExportMetricsPartialSuccess = io.opentelemetry.proto.collector.metrics.v1.ExportMetricsPartialSuccess.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.collector.metrics.v1.ExportMetricsPartialSuccess> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.collector.metrics.v1.ExportMetricsPartialSuccess, *>>(2)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "rejected_data_points",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.Int64(),
                        jsonName = "rejectedDataPoints",
                        value = io.opentelemetry.proto.collector.metrics.v1.ExportMetricsPartialSuccess::rejectedDataPoints
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "error_message",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(),
                        jsonName = "errorMessage",
                        value = io.opentelemetry.proto.collector.metrics.v1.ExportMetricsPartialSuccess::errorMessage
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.collector.metrics.v1.ExportMetricsPartialSuccess",
                messageClass = io.opentelemetry.proto.collector.metrics.v1.ExportMetricsPartialSuccess::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
@pbandk.JsName("orDefaultForExportMetricsServiceRequest")
public fun ExportMetricsServiceRequest?.orDefault(): io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceRequest = this ?: ExportMetricsServiceRequest.defaultInstance

private fun ExportMetricsServiceRequest.protoMergeImpl(plus: pbandk.Message?): ExportMetricsServiceRequest = (plus as? ExportMetricsServiceRequest)?.let {
    it.copy(
        resourceMetrics = resourceMetrics + plus.resourceMetrics,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ExportMetricsServiceRequest.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ExportMetricsServiceRequest {
    var resourceMetrics: pbandk.ListWithSize.Builder<io.opentelemetry.proto.metrics.v1.ResourceMetrics>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> resourceMetrics = (resourceMetrics ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.metrics.v1.ResourceMetrics> }
        }
    }

    return ExportMetricsServiceRequest(pbandk.ListWithSize.Builder.fixed(resourceMetrics), unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForExportMetricsServiceResponse")
public fun ExportMetricsServiceResponse?.orDefault(): io.opentelemetry.proto.collector.metrics.v1.ExportMetricsServiceResponse = this ?: ExportMetricsServiceResponse.defaultInstance

private fun ExportMetricsServiceResponse.protoMergeImpl(plus: pbandk.Message?): ExportMetricsServiceResponse = (plus as? ExportMetricsServiceResponse)?.let {
    it.copy(
        partialSuccess = partialSuccess?.plus(plus.partialSuccess) ?: plus.partialSuccess,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ExportMetricsServiceResponse.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ExportMetricsServiceResponse {
    var partialSuccess: io.opentelemetry.proto.collector.metrics.v1.ExportMetricsPartialSuccess? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> partialSuccess = _fieldValue as io.opentelemetry.proto.collector.metrics.v1.ExportMetricsPartialSuccess
        }
    }

    return ExportMetricsServiceResponse(partialSuccess, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForExportMetricsPartialSuccess")
public fun ExportMetricsPartialSuccess?.orDefault(): io.opentelemetry.proto.collector.metrics.v1.ExportMetricsPartialSuccess = this ?: ExportMetricsPartialSuccess.defaultInstance

private fun ExportMetricsPartialSuccess.protoMergeImpl(plus: pbandk.Message?): ExportMetricsPartialSuccess = (plus as? ExportMetricsPartialSuccess)?.let {
    it.copy(
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ExportMetricsPartialSuccess.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ExportMetricsPartialSuccess {
    var rejectedDataPoints = 0L
    var errorMessage = ""

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> rejectedDataPoints = _fieldValue as Long
            2 -> errorMessage = _fieldValue as String
        }
    }

    return ExportMetricsPartialSuccess(rejectedDataPoints, errorMessage, unknownFields)
}
