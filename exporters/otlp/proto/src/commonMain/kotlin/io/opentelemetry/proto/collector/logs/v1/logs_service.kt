@file:OptIn(pbandk.PublicForGeneratedCode::class)

package io.opentelemetry.proto.collector.logs.v1

@pbandk.Export
public data class ExportLogsServiceRequest(
    val resourceLogs: List<io.opentelemetry.proto.logs.v1.ResourceLogs> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest> {
        public val defaultInstance: io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest by lazy { io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest = io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest, *>>(1)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "resource_logs",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.logs.v1.ResourceLogs>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.logs.v1.ResourceLogs.Companion)),
                        jsonName = "resourceLogs",
                        value = io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest::resourceLogs
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest",
                messageClass = io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class ExportLogsServiceResponse(
    val partialSuccess: io.opentelemetry.proto.collector.logs.v1.ExportLogsPartialSuccess? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse> {
        public val defaultInstance: io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse by lazy { io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse = io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse, *>>(1)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "partial_success",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.collector.logs.v1.ExportLogsPartialSuccess.Companion),
                        jsonName = "partialSuccess",
                        value = io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse::partialSuccess
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse",
                messageClass = io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class ExportLogsPartialSuccess(
    val rejectedLogRecords: Long = 0L,
    val errorMessage: String = "",
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.collector.logs.v1.ExportLogsPartialSuccess = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.collector.logs.v1.ExportLogsPartialSuccess> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.collector.logs.v1.ExportLogsPartialSuccess> {
        public val defaultInstance: io.opentelemetry.proto.collector.logs.v1.ExportLogsPartialSuccess by lazy { io.opentelemetry.proto.collector.logs.v1.ExportLogsPartialSuccess() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.collector.logs.v1.ExportLogsPartialSuccess = io.opentelemetry.proto.collector.logs.v1.ExportLogsPartialSuccess.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.collector.logs.v1.ExportLogsPartialSuccess> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.collector.logs.v1.ExportLogsPartialSuccess, *>>(2)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "rejected_log_records",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.Int64(),
                        jsonName = "rejectedLogRecords",
                        value = io.opentelemetry.proto.collector.logs.v1.ExportLogsPartialSuccess::rejectedLogRecords
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "error_message",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(),
                        jsonName = "errorMessage",
                        value = io.opentelemetry.proto.collector.logs.v1.ExportLogsPartialSuccess::errorMessage
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.collector.logs.v1.ExportLogsPartialSuccess",
                messageClass = io.opentelemetry.proto.collector.logs.v1.ExportLogsPartialSuccess::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
@pbandk.JsName("orDefaultForExportLogsServiceRequest")
public fun ExportLogsServiceRequest?.orDefault(): io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceRequest = this ?: ExportLogsServiceRequest.defaultInstance

private fun ExportLogsServiceRequest.protoMergeImpl(plus: pbandk.Message?): ExportLogsServiceRequest = (plus as? ExportLogsServiceRequest)?.let {
    it.copy(
        resourceLogs = resourceLogs + plus.resourceLogs,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ExportLogsServiceRequest.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ExportLogsServiceRequest {
    var resourceLogs: pbandk.ListWithSize.Builder<io.opentelemetry.proto.logs.v1.ResourceLogs>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> resourceLogs = (resourceLogs ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.logs.v1.ResourceLogs> }
        }
    }

    return ExportLogsServiceRequest(pbandk.ListWithSize.Builder.fixed(resourceLogs), unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForExportLogsServiceResponse")
public fun ExportLogsServiceResponse?.orDefault(): io.opentelemetry.proto.collector.logs.v1.ExportLogsServiceResponse = this ?: ExportLogsServiceResponse.defaultInstance

private fun ExportLogsServiceResponse.protoMergeImpl(plus: pbandk.Message?): ExportLogsServiceResponse = (plus as? ExportLogsServiceResponse)?.let {
    it.copy(
        partialSuccess = partialSuccess?.plus(plus.partialSuccess) ?: plus.partialSuccess,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ExportLogsServiceResponse.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ExportLogsServiceResponse {
    var partialSuccess: io.opentelemetry.proto.collector.logs.v1.ExportLogsPartialSuccess? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> partialSuccess = _fieldValue as io.opentelemetry.proto.collector.logs.v1.ExportLogsPartialSuccess
        }
    }

    return ExportLogsServiceResponse(partialSuccess, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForExportLogsPartialSuccess")
public fun ExportLogsPartialSuccess?.orDefault(): io.opentelemetry.proto.collector.logs.v1.ExportLogsPartialSuccess = this ?: ExportLogsPartialSuccess.defaultInstance

private fun ExportLogsPartialSuccess.protoMergeImpl(plus: pbandk.Message?): ExportLogsPartialSuccess = (plus as? ExportLogsPartialSuccess)?.let {
    it.copy(
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ExportLogsPartialSuccess.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ExportLogsPartialSuccess {
    var rejectedLogRecords = 0L
    var errorMessage = ""

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> rejectedLogRecords = _fieldValue as Long
            2 -> errorMessage = _fieldValue as String
        }
    }

    return ExportLogsPartialSuccess(rejectedLogRecords, errorMessage, unknownFields)
}
