@file:OptIn(pbandk.PublicForGeneratedCode::class)

package io.opentelemetry.proto.logs.v1

@pbandk.Export
public sealed class SeverityNumber(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
    override fun equals(other: kotlin.Any?): Boolean = other is SeverityNumber && other.value == value
    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = "SeverityNumber.${name ?: "UNRECOGNIZED"}(value=$value)"

    public object UNSPECIFIED : SeverityNumber(0, "SEVERITY_NUMBER_UNSPECIFIED")
    public object TRACE : SeverityNumber(1, "SEVERITY_NUMBER_TRACE")
    public object TRACE2 : SeverityNumber(2, "SEVERITY_NUMBER_TRACE2")
    public object TRACE3 : SeverityNumber(3, "SEVERITY_NUMBER_TRACE3")
    public object TRACE4 : SeverityNumber(4, "SEVERITY_NUMBER_TRACE4")
    public object DEBUG : SeverityNumber(5, "SEVERITY_NUMBER_DEBUG")
    public object DEBUG2 : SeverityNumber(6, "SEVERITY_NUMBER_DEBUG2")
    public object DEBUG3 : SeverityNumber(7, "SEVERITY_NUMBER_DEBUG3")
    public object DEBUG4 : SeverityNumber(8, "SEVERITY_NUMBER_DEBUG4")
    public object INFO : SeverityNumber(9, "SEVERITY_NUMBER_INFO")
    public object INFO2 : SeverityNumber(10, "SEVERITY_NUMBER_INFO2")
    public object INFO3 : SeverityNumber(11, "SEVERITY_NUMBER_INFO3")
    public object INFO4 : SeverityNumber(12, "SEVERITY_NUMBER_INFO4")
    public object WARN : SeverityNumber(13, "SEVERITY_NUMBER_WARN")
    public object WARN2 : SeverityNumber(14, "SEVERITY_NUMBER_WARN2")
    public object WARN3 : SeverityNumber(15, "SEVERITY_NUMBER_WARN3")
    public object WARN4 : SeverityNumber(16, "SEVERITY_NUMBER_WARN4")
    public object ERROR : SeverityNumber(17, "SEVERITY_NUMBER_ERROR")
    public object ERROR2 : SeverityNumber(18, "SEVERITY_NUMBER_ERROR2")
    public object ERROR3 : SeverityNumber(19, "SEVERITY_NUMBER_ERROR3")
    public object ERROR4 : SeverityNumber(20, "SEVERITY_NUMBER_ERROR4")
    public object FATAL : SeverityNumber(21, "SEVERITY_NUMBER_FATAL")
    public object FATAL2 : SeverityNumber(22, "SEVERITY_NUMBER_FATAL2")
    public object FATAL3 : SeverityNumber(23, "SEVERITY_NUMBER_FATAL3")
    public object FATAL4 : SeverityNumber(24, "SEVERITY_NUMBER_FATAL4")
    public class UNRECOGNIZED(value: Int) : SeverityNumber(value)

    public companion object : pbandk.Message.Enum.Companion<SeverityNumber> {
        public val values: List<SeverityNumber> by lazy { listOf(UNSPECIFIED, TRACE, TRACE2, TRACE3, TRACE4, DEBUG, DEBUG2, DEBUG3, DEBUG4, INFO, INFO2, INFO3, INFO4, WARN, WARN2, WARN3, WARN4, ERROR, ERROR2, ERROR3, ERROR4, FATAL, FATAL2, FATAL3, FATAL4) }
        override fun fromValue(value: Int): SeverityNumber = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
        override fun fromName(name: String): SeverityNumber = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No SeverityNumber with name: $name")
    }
}

@pbandk.Export
public sealed class LogRecordFlags(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
    override fun equals(other: kotlin.Any?): Boolean = other is LogRecordFlags && other.value == value
    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = "LogRecordFlags.${name ?: "UNRECOGNIZED"}(value=$value)"

    public object LOG_RECORD_FLAG_UNSPECIFIED : LogRecordFlags(0, "LOG_RECORD_FLAG_UNSPECIFIED")
    public object LOG_RECORD_FLAG_TRACE_FLAGS_MASK : LogRecordFlags(255, "LOG_RECORD_FLAG_TRACE_FLAGS_MASK")
    public class UNRECOGNIZED(value: Int) : LogRecordFlags(value)

    public companion object : pbandk.Message.Enum.Companion<LogRecordFlags> {
        public val values: List<LogRecordFlags> by lazy { listOf(LOG_RECORD_FLAG_UNSPECIFIED, LOG_RECORD_FLAG_TRACE_FLAGS_MASK) }
        override fun fromValue(value: Int): LogRecordFlags = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
        override fun fromName(name: String): LogRecordFlags = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No LogRecordFlags with name: $name")
    }
}

@pbandk.Export
public data class LogsData(
    val resourceLogs: List<io.opentelemetry.proto.logs.v1.ResourceLogs> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.logs.v1.LogsData = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.logs.v1.LogsData> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.logs.v1.LogsData> {
        public val defaultInstance: io.opentelemetry.proto.logs.v1.LogsData by lazy { io.opentelemetry.proto.logs.v1.LogsData() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.logs.v1.LogsData = io.opentelemetry.proto.logs.v1.LogsData.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.logs.v1.LogsData> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.logs.v1.LogsData, *>>(1)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "resource_logs",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.logs.v1.ResourceLogs>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.logs.v1.ResourceLogs.Companion)),
                        jsonName = "resourceLogs",
                        value = io.opentelemetry.proto.logs.v1.LogsData::resourceLogs
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.logs.v1.LogsData",
                messageClass = io.opentelemetry.proto.logs.v1.LogsData::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class ResourceLogs(
    val resource: io.opentelemetry.proto.resource.v1.Resource? = null,
    val scopeLogs: List<io.opentelemetry.proto.logs.v1.ScopeLogs> = emptyList(),
    val schemaUrl: String = "",
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.logs.v1.ResourceLogs = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.logs.v1.ResourceLogs> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.logs.v1.ResourceLogs> {
        public val defaultInstance: io.opentelemetry.proto.logs.v1.ResourceLogs by lazy { io.opentelemetry.proto.logs.v1.ResourceLogs() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.logs.v1.ResourceLogs = io.opentelemetry.proto.logs.v1.ResourceLogs.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.logs.v1.ResourceLogs> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.logs.v1.ResourceLogs, *>>(3)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "resource",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.resource.v1.Resource.Companion),
                        jsonName = "resource",
                        value = io.opentelemetry.proto.logs.v1.ResourceLogs::resource
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "scope_logs",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.logs.v1.ScopeLogs>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.logs.v1.ScopeLogs.Companion)),
                        jsonName = "scopeLogs",
                        value = io.opentelemetry.proto.logs.v1.ResourceLogs::scopeLogs
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "schema_url",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(),
                        jsonName = "schemaUrl",
                        value = io.opentelemetry.proto.logs.v1.ResourceLogs::schemaUrl
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.logs.v1.ResourceLogs",
                messageClass = io.opentelemetry.proto.logs.v1.ResourceLogs::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class ScopeLogs(
    val scope: io.opentelemetry.proto.common.v1.InstrumentationScope? = null,
    val logRecords: List<io.opentelemetry.proto.logs.v1.LogRecord> = emptyList(),
    val schemaUrl: String = "",
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.logs.v1.ScopeLogs = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.logs.v1.ScopeLogs> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.logs.v1.ScopeLogs> {
        public val defaultInstance: io.opentelemetry.proto.logs.v1.ScopeLogs by lazy { io.opentelemetry.proto.logs.v1.ScopeLogs() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.logs.v1.ScopeLogs = io.opentelemetry.proto.logs.v1.ScopeLogs.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.logs.v1.ScopeLogs> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.logs.v1.ScopeLogs, *>>(3)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "scope",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.common.v1.InstrumentationScope.Companion),
                        jsonName = "scope",
                        value = io.opentelemetry.proto.logs.v1.ScopeLogs::scope
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "log_records",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.logs.v1.LogRecord>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.logs.v1.LogRecord.Companion)),
                        jsonName = "logRecords",
                        value = io.opentelemetry.proto.logs.v1.ScopeLogs::logRecords
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "schema_url",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(),
                        jsonName = "schemaUrl",
                        value = io.opentelemetry.proto.logs.v1.ScopeLogs::schemaUrl
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.logs.v1.ScopeLogs",
                messageClass = io.opentelemetry.proto.logs.v1.ScopeLogs::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class LogRecord(
    val timeUnixNano: Long = 0L,
    val observedTimeUnixNano: Long = 0L,
    val severityNumber: io.opentelemetry.proto.logs.v1.SeverityNumber = io.opentelemetry.proto.logs.v1.SeverityNumber.fromValue(0),
    val severityText: String = "",
    val body: io.opentelemetry.proto.common.v1.AnyValue? = null,
    val attributes: List<io.opentelemetry.proto.common.v1.KeyValue> = emptyList(),
    val droppedAttributesCount: Int = 0,
    val flags: Int = 0,
    val traceId: pbandk.ByteArr = pbandk.ByteArr.empty,
    val spanId: pbandk.ByteArr = pbandk.ByteArr.empty,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.logs.v1.LogRecord = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.logs.v1.LogRecord> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.logs.v1.LogRecord> {
        public val defaultInstance: io.opentelemetry.proto.logs.v1.LogRecord by lazy { io.opentelemetry.proto.logs.v1.LogRecord() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.logs.v1.LogRecord = io.opentelemetry.proto.logs.v1.LogRecord.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.logs.v1.LogRecord> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.logs.v1.LogRecord, *>>(10)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "time_unix_nano",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.Fixed64(),
                        jsonName = "timeUnixNano",
                        value = io.opentelemetry.proto.logs.v1.LogRecord::timeUnixNano
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "severity_number",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = io.opentelemetry.proto.logs.v1.SeverityNumber.Companion),
                        jsonName = "severityNumber",
                        value = io.opentelemetry.proto.logs.v1.LogRecord::severityNumber
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "severity_text",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(),
                        jsonName = "severityText",
                        value = io.opentelemetry.proto.logs.v1.LogRecord::severityText
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "body",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.common.v1.AnyValue.Companion),
                        jsonName = "body",
                        value = io.opentelemetry.proto.logs.v1.LogRecord::body
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "attributes",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.common.v1.KeyValue>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.common.v1.KeyValue.Companion)),
                        jsonName = "attributes",
                        value = io.opentelemetry.proto.logs.v1.LogRecord::attributes
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "dropped_attributes_count",
                        number = 7,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(),
                        jsonName = "droppedAttributesCount",
                        value = io.opentelemetry.proto.logs.v1.LogRecord::droppedAttributesCount
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "flags",
                        number = 8,
                        type = pbandk.FieldDescriptor.Type.Primitive.Fixed32(),
                        jsonName = "flags",
                        value = io.opentelemetry.proto.logs.v1.LogRecord::flags
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "trace_id",
                        number = 9,
                        type = pbandk.FieldDescriptor.Type.Primitive.Bytes(),
                        jsonName = "traceId",
                        value = io.opentelemetry.proto.logs.v1.LogRecord::traceId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "span_id",
                        number = 10,
                        type = pbandk.FieldDescriptor.Type.Primitive.Bytes(),
                        jsonName = "spanId",
                        value = io.opentelemetry.proto.logs.v1.LogRecord::spanId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "observed_time_unix_nano",
                        number = 11,
                        type = pbandk.FieldDescriptor.Type.Primitive.Fixed64(),
                        jsonName = "observedTimeUnixNano",
                        value = io.opentelemetry.proto.logs.v1.LogRecord::observedTimeUnixNano
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.logs.v1.LogRecord",
                messageClass = io.opentelemetry.proto.logs.v1.LogRecord::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
@pbandk.JsName("orDefaultForLogsData")
public fun LogsData?.orDefault(): io.opentelemetry.proto.logs.v1.LogsData = this ?: LogsData.defaultInstance

private fun LogsData.protoMergeImpl(plus: pbandk.Message?): LogsData = (plus as? LogsData)?.let {
    it.copy(
        resourceLogs = resourceLogs + plus.resourceLogs,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun LogsData.Companion.decodeWithImpl(u: pbandk.MessageDecoder): LogsData {
    var resourceLogs: pbandk.ListWithSize.Builder<io.opentelemetry.proto.logs.v1.ResourceLogs>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> resourceLogs = (resourceLogs ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.logs.v1.ResourceLogs> }
        }
    }

    return LogsData(pbandk.ListWithSize.Builder.fixed(resourceLogs), unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForResourceLogs")
public fun ResourceLogs?.orDefault(): io.opentelemetry.proto.logs.v1.ResourceLogs = this ?: ResourceLogs.defaultInstance

private fun ResourceLogs.protoMergeImpl(plus: pbandk.Message?): ResourceLogs = (plus as? ResourceLogs)?.let {
    it.copy(
        resource = resource?.plus(plus.resource) ?: plus.resource,
        scopeLogs = scopeLogs + plus.scopeLogs,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ResourceLogs.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ResourceLogs {
    var resource: io.opentelemetry.proto.resource.v1.Resource? = null
    var scopeLogs: pbandk.ListWithSize.Builder<io.opentelemetry.proto.logs.v1.ScopeLogs>? = null
    var schemaUrl = ""

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> resource = _fieldValue as io.opentelemetry.proto.resource.v1.Resource
            2 -> scopeLogs = (scopeLogs ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.logs.v1.ScopeLogs> }
            3 -> schemaUrl = _fieldValue as String
        }
    }

    return ResourceLogs(resource, pbandk.ListWithSize.Builder.fixed(scopeLogs), schemaUrl, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForScopeLogs")
public fun ScopeLogs?.orDefault(): io.opentelemetry.proto.logs.v1.ScopeLogs = this ?: ScopeLogs.defaultInstance

private fun ScopeLogs.protoMergeImpl(plus: pbandk.Message?): ScopeLogs = (plus as? ScopeLogs)?.let {
    it.copy(
        scope = scope?.plus(plus.scope) ?: plus.scope,
        logRecords = logRecords + plus.logRecords,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ScopeLogs.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ScopeLogs {
    var scope: io.opentelemetry.proto.common.v1.InstrumentationScope? = null
    var logRecords: pbandk.ListWithSize.Builder<io.opentelemetry.proto.logs.v1.LogRecord>? = null
    var schemaUrl = ""

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> scope = _fieldValue as io.opentelemetry.proto.common.v1.InstrumentationScope
            2 -> logRecords = (logRecords ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.logs.v1.LogRecord> }
            3 -> schemaUrl = _fieldValue as String
        }
    }

    return ScopeLogs(scope, pbandk.ListWithSize.Builder.fixed(logRecords), schemaUrl, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForLogRecord")
public fun LogRecord?.orDefault(): io.opentelemetry.proto.logs.v1.LogRecord = this ?: LogRecord.defaultInstance

private fun LogRecord.protoMergeImpl(plus: pbandk.Message?): LogRecord = (plus as? LogRecord)?.let {
    it.copy(
        body = body?.plus(plus.body) ?: plus.body,
        attributes = attributes + plus.attributes,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun LogRecord.Companion.decodeWithImpl(u: pbandk.MessageDecoder): LogRecord {
    var timeUnixNano = 0L
    var observedTimeUnixNano = 0L
    var severityNumber: io.opentelemetry.proto.logs.v1.SeverityNumber = io.opentelemetry.proto.logs.v1.SeverityNumber.fromValue(0)
    var severityText = ""
    var body: io.opentelemetry.proto.common.v1.AnyValue? = null
    var attributes: pbandk.ListWithSize.Builder<io.opentelemetry.proto.common.v1.KeyValue>? = null
    var droppedAttributesCount = 0
    var flags = 0
    var traceId: pbandk.ByteArr = pbandk.ByteArr.empty
    var spanId: pbandk.ByteArr = pbandk.ByteArr.empty

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> timeUnixNano = _fieldValue as Long
            2 -> severityNumber = _fieldValue as io.opentelemetry.proto.logs.v1.SeverityNumber
            3 -> severityText = _fieldValue as String
            5 -> body = _fieldValue as io.opentelemetry.proto.common.v1.AnyValue
            6 -> attributes = (attributes ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.common.v1.KeyValue> }
            7 -> droppedAttributesCount = _fieldValue as Int
            8 -> flags = _fieldValue as Int
            9 -> traceId = _fieldValue as pbandk.ByteArr
            10 -> spanId = _fieldValue as pbandk.ByteArr
            11 -> observedTimeUnixNano = _fieldValue as Long
        }
    }

    return LogRecord(timeUnixNano, observedTimeUnixNano, severityNumber, severityText,
        body, pbandk.ListWithSize.Builder.fixed(attributes), droppedAttributesCount, flags,
        traceId, spanId, unknownFields)
}
