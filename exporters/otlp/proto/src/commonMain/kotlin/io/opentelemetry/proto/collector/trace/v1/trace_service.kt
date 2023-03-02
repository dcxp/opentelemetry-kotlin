@file:OptIn(pbandk.PublicForGeneratedCode::class)

package io.opentelemetry.proto.collector.trace.v1

@pbandk.Export
public data class ExportTraceServiceRequest(
    val resourceSpans: List<io.opentelemetry.proto.trace.v1.ResourceSpans> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest> {
        public val defaultInstance: io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest by lazy { io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest = io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest, *>>(1)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "resource_spans",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.trace.v1.ResourceSpans>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.trace.v1.ResourceSpans.Companion)),
                        jsonName = "resourceSpans",
                        value = io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest::resourceSpans
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest",
                messageClass = io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class ExportTraceServiceResponse(
    val partialSuccess: io.opentelemetry.proto.collector.trace.v1.ExportTracePartialSuccess? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse> {
        public val defaultInstance: io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse by lazy { io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse = io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse, *>>(1)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "partial_success",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.collector.trace.v1.ExportTracePartialSuccess.Companion),
                        jsonName = "partialSuccess",
                        value = io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse::partialSuccess
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse",
                messageClass = io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class ExportTracePartialSuccess(
    val rejectedSpans: Long = 0L,
    val errorMessage: String = "",
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.collector.trace.v1.ExportTracePartialSuccess = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.collector.trace.v1.ExportTracePartialSuccess> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.collector.trace.v1.ExportTracePartialSuccess> {
        public val defaultInstance: io.opentelemetry.proto.collector.trace.v1.ExportTracePartialSuccess by lazy { io.opentelemetry.proto.collector.trace.v1.ExportTracePartialSuccess() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.collector.trace.v1.ExportTracePartialSuccess = io.opentelemetry.proto.collector.trace.v1.ExportTracePartialSuccess.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.collector.trace.v1.ExportTracePartialSuccess> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.collector.trace.v1.ExportTracePartialSuccess, *>>(2)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "rejected_spans",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.Int64(),
                        jsonName = "rejectedSpans",
                        value = io.opentelemetry.proto.collector.trace.v1.ExportTracePartialSuccess::rejectedSpans
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "error_message",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(),
                        jsonName = "errorMessage",
                        value = io.opentelemetry.proto.collector.trace.v1.ExportTracePartialSuccess::errorMessage
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.collector.trace.v1.ExportTracePartialSuccess",
                messageClass = io.opentelemetry.proto.collector.trace.v1.ExportTracePartialSuccess::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
@pbandk.JsName("orDefaultForExportTraceServiceRequest")
public fun ExportTraceServiceRequest?.orDefault(): io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest = this ?: ExportTraceServiceRequest.defaultInstance

private fun ExportTraceServiceRequest.protoMergeImpl(plus: pbandk.Message?): ExportTraceServiceRequest = (plus as? ExportTraceServiceRequest)?.let {
    it.copy(
        resourceSpans = resourceSpans + plus.resourceSpans,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ExportTraceServiceRequest.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ExportTraceServiceRequest {
    var resourceSpans: pbandk.ListWithSize.Builder<io.opentelemetry.proto.trace.v1.ResourceSpans>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> resourceSpans = (resourceSpans ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.trace.v1.ResourceSpans> }
        }
    }

    return ExportTraceServiceRequest(pbandk.ListWithSize.Builder.fixed(resourceSpans), unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForExportTraceServiceResponse")
public fun ExportTraceServiceResponse?.orDefault(): io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceResponse = this ?: ExportTraceServiceResponse.defaultInstance

private fun ExportTraceServiceResponse.protoMergeImpl(plus: pbandk.Message?): ExportTraceServiceResponse = (plus as? ExportTraceServiceResponse)?.let {
    it.copy(
        partialSuccess = partialSuccess?.plus(plus.partialSuccess) ?: plus.partialSuccess,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ExportTraceServiceResponse.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ExportTraceServiceResponse {
    var partialSuccess: io.opentelemetry.proto.collector.trace.v1.ExportTracePartialSuccess? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> partialSuccess = _fieldValue as io.opentelemetry.proto.collector.trace.v1.ExportTracePartialSuccess
        }
    }

    return ExportTraceServiceResponse(partialSuccess, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForExportTracePartialSuccess")
public fun ExportTracePartialSuccess?.orDefault(): io.opentelemetry.proto.collector.trace.v1.ExportTracePartialSuccess = this ?: ExportTracePartialSuccess.defaultInstance

private fun ExportTracePartialSuccess.protoMergeImpl(plus: pbandk.Message?): ExportTracePartialSuccess = (plus as? ExportTracePartialSuccess)?.let {
    it.copy(
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ExportTracePartialSuccess.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ExportTracePartialSuccess {
    var rejectedSpans = 0L
    var errorMessage = ""

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> rejectedSpans = _fieldValue as Long
            2 -> errorMessage = _fieldValue as String
        }
    }

    return ExportTracePartialSuccess(rejectedSpans, errorMessage, unknownFields)
}
