@file:OptIn(PublicForGeneratedCode::class)

package io.opentelemetry.proto.common.v1

import pbandk.PublicForGeneratedCode

@pbandk.Export
public data class AnyValue(
    val value: Value<*>? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    public sealed class Value<V>(value: V) : pbandk.Message.OneOf<V>(value) {
        public class StringValue(stringValue: String = "") : Value<String>(stringValue)
        public class BoolValue(boolValue: Boolean = false) : Value<Boolean>(boolValue)
        public class IntValue(intValue: Long = 0L) : Value<Long>(intValue)
        public class DoubleValue(doubleValue: Double = 0.0) : Value<Double>(doubleValue)
        public class ArrayValue(arrayValue: io.opentelemetry.proto.common.v1.ArrayValue) : Value<io.opentelemetry.proto.common.v1.ArrayValue>(arrayValue)
        public class KvlistValue(kvlistValue: io.opentelemetry.proto.common.v1.KeyValueList) : Value<io.opentelemetry.proto.common.v1.KeyValueList>(kvlistValue)
        public class BytesValue(bytesValue: pbandk.ByteArr = pbandk.ByteArr.empty) : Value<pbandk.ByteArr>(bytesValue)
    }

    val stringValue: String?
        get() = (value as? Value.StringValue)?.value
    val boolValue: Boolean?
        get() = (value as? Value.BoolValue)?.value
    val intValue: Long?
        get() = (value as? Value.IntValue)?.value
    val doubleValue: Double?
        get() = (value as? Value.DoubleValue)?.value
    val arrayValue: io.opentelemetry.proto.common.v1.ArrayValue?
        get() = (value as? Value.ArrayValue)?.value
    val kvlistValue: io.opentelemetry.proto.common.v1.KeyValueList?
        get() = (value as? Value.KvlistValue)?.value
    val bytesValue: pbandk.ByteArr?
        get() = (value as? Value.BytesValue)?.value

    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.common.v1.AnyValue = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.common.v1.AnyValue> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.common.v1.AnyValue> {
        public val defaultInstance: io.opentelemetry.proto.common.v1.AnyValue by lazy { io.opentelemetry.proto.common.v1.AnyValue() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.common.v1.AnyValue = io.opentelemetry.proto.common.v1.AnyValue.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.common.v1.AnyValue> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.common.v1.AnyValue, *>>(7)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "string_value",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(hasPresence = true),
                        oneofMember = true,
                        jsonName = "stringValue",
                        value = io.opentelemetry.proto.common.v1.AnyValue::stringValue
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "bool_value",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.Bool(hasPresence = true),
                        oneofMember = true,
                        jsonName = "boolValue",
                        value = io.opentelemetry.proto.common.v1.AnyValue::boolValue
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "int_value",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.Int64(hasPresence = true),
                        oneofMember = true,
                        jsonName = "intValue",
                        value = io.opentelemetry.proto.common.v1.AnyValue::intValue
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "double_value",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Primitive.Double(hasPresence = true),
                        oneofMember = true,
                        jsonName = "doubleValue",
                        value = io.opentelemetry.proto.common.v1.AnyValue::doubleValue
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "array_value",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.common.v1.ArrayValue.Companion),
                        oneofMember = true,
                        jsonName = "arrayValue",
                        value = io.opentelemetry.proto.common.v1.AnyValue::arrayValue
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "kvlist_value",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.common.v1.KeyValueList.Companion),
                        oneofMember = true,
                        jsonName = "kvlistValue",
                        value = io.opentelemetry.proto.common.v1.AnyValue::kvlistValue
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "bytes_value",
                        number = 7,
                        type = pbandk.FieldDescriptor.Type.Primitive.Bytes(hasPresence = true),
                        oneofMember = true,
                        jsonName = "bytesValue",
                        value = io.opentelemetry.proto.common.v1.AnyValue::bytesValue
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.common.v1.AnyValue",
                messageClass = io.opentelemetry.proto.common.v1.AnyValue::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class ArrayValue(
    val values: List<io.opentelemetry.proto.common.v1.AnyValue> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.common.v1.ArrayValue = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.common.v1.ArrayValue> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.common.v1.ArrayValue> {
        public val defaultInstance: io.opentelemetry.proto.common.v1.ArrayValue by lazy { io.opentelemetry.proto.common.v1.ArrayValue() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.common.v1.ArrayValue = io.opentelemetry.proto.common.v1.ArrayValue.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.common.v1.ArrayValue> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.common.v1.ArrayValue, *>>(1)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "values",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.common.v1.AnyValue>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.common.v1.AnyValue.Companion)),
                        jsonName = "values",
                        value = io.opentelemetry.proto.common.v1.ArrayValue::values
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.common.v1.ArrayValue",
                messageClass = io.opentelemetry.proto.common.v1.ArrayValue::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class KeyValueList(
    val values: List<io.opentelemetry.proto.common.v1.KeyValue> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.common.v1.KeyValueList = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.common.v1.KeyValueList> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.common.v1.KeyValueList> {
        public val defaultInstance: io.opentelemetry.proto.common.v1.KeyValueList by lazy { io.opentelemetry.proto.common.v1.KeyValueList() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.common.v1.KeyValueList = io.opentelemetry.proto.common.v1.KeyValueList.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.common.v1.KeyValueList> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.common.v1.KeyValueList, *>>(1)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "values",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.common.v1.KeyValue>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.common.v1.KeyValue.Companion)),
                        jsonName = "values",
                        value = io.opentelemetry.proto.common.v1.KeyValueList::values
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.common.v1.KeyValueList",
                messageClass = io.opentelemetry.proto.common.v1.KeyValueList::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class KeyValue(
    val key: String = "",
    val value: io.opentelemetry.proto.common.v1.AnyValue? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.common.v1.KeyValue = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.common.v1.KeyValue> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.common.v1.KeyValue> {
        public val defaultInstance: io.opentelemetry.proto.common.v1.KeyValue by lazy { io.opentelemetry.proto.common.v1.KeyValue() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.common.v1.KeyValue = io.opentelemetry.proto.common.v1.KeyValue.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.common.v1.KeyValue> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.common.v1.KeyValue, *>>(2)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "key",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(),
                        jsonName = "key",
                        value = io.opentelemetry.proto.common.v1.KeyValue::key
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "value",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.common.v1.AnyValue.Companion),
                        jsonName = "value",
                        value = io.opentelemetry.proto.common.v1.KeyValue::value
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.common.v1.KeyValue",
                messageClass = io.opentelemetry.proto.common.v1.KeyValue::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class InstrumentationScope(
    val name: String = "",
    val version: String = "",
    val attributes: List<io.opentelemetry.proto.common.v1.KeyValue> = emptyList(),
    val droppedAttributesCount: Int = 0,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.common.v1.InstrumentationScope = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.common.v1.InstrumentationScope> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.common.v1.InstrumentationScope> {
        public val defaultInstance: io.opentelemetry.proto.common.v1.InstrumentationScope by lazy { io.opentelemetry.proto.common.v1.InstrumentationScope() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.common.v1.InstrumentationScope = io.opentelemetry.proto.common.v1.InstrumentationScope.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.common.v1.InstrumentationScope> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.common.v1.InstrumentationScope, *>>(4)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "name",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(),
                        jsonName = "name",
                        value = io.opentelemetry.proto.common.v1.InstrumentationScope::name
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "version",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(),
                        jsonName = "version",
                        value = io.opentelemetry.proto.common.v1.InstrumentationScope::version
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "attributes",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.common.v1.KeyValue>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.common.v1.KeyValue.Companion)),
                        jsonName = "attributes",
                        value = io.opentelemetry.proto.common.v1.InstrumentationScope::attributes
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "dropped_attributes_count",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(),
                        jsonName = "droppedAttributesCount",
                        value = io.opentelemetry.proto.common.v1.InstrumentationScope::droppedAttributesCount
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.common.v1.InstrumentationScope",
                messageClass = io.opentelemetry.proto.common.v1.InstrumentationScope::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
@pbandk.JsName("orDefaultForAnyValue")
public fun AnyValue?.orDefault(): io.opentelemetry.proto.common.v1.AnyValue = this ?: AnyValue.defaultInstance

private fun AnyValue.protoMergeImpl(plus: pbandk.Message?): AnyValue = (plus as? AnyValue)?.let {
    it.copy(
        value = when {
            value is AnyValue.Value.ArrayValue && plus.value is AnyValue.Value.ArrayValue ->
                AnyValue.Value.ArrayValue(value.value + plus.value.value)
            value is AnyValue.Value.KvlistValue && plus.value is AnyValue.Value.KvlistValue ->
                AnyValue.Value.KvlistValue(value.value + plus.value.value)
            else ->
                plus.value ?: value
        },
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun AnyValue.Companion.decodeWithImpl(u: pbandk.MessageDecoder): AnyValue {
    var value: AnyValue.Value<*>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> value = AnyValue.Value.StringValue(_fieldValue as String)
            2 -> value = AnyValue.Value.BoolValue(_fieldValue as Boolean)
            3 -> value = AnyValue.Value.IntValue(_fieldValue as Long)
            4 -> value = AnyValue.Value.DoubleValue(_fieldValue as Double)
            5 -> value = AnyValue.Value.ArrayValue(_fieldValue as io.opentelemetry.proto.common.v1.ArrayValue)
            6 -> value = AnyValue.Value.KvlistValue(_fieldValue as io.opentelemetry.proto.common.v1.KeyValueList)
            7 -> value = AnyValue.Value.BytesValue(_fieldValue as pbandk.ByteArr)
        }
    }

    return AnyValue(value, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForArrayValue")
public fun ArrayValue?.orDefault(): io.opentelemetry.proto.common.v1.ArrayValue = this ?: ArrayValue.defaultInstance

private fun ArrayValue.protoMergeImpl(plus: pbandk.Message?): ArrayValue = (plus as? ArrayValue)?.let {
    it.copy(
        values = values + plus.values,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ArrayValue.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ArrayValue {
    var values: pbandk.ListWithSize.Builder<io.opentelemetry.proto.common.v1.AnyValue>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> values = (values ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.common.v1.AnyValue> }
        }
    }

    return ArrayValue(pbandk.ListWithSize.Builder.fixed(values), unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForKeyValueList")
public fun KeyValueList?.orDefault(): io.opentelemetry.proto.common.v1.KeyValueList = this ?: KeyValueList.defaultInstance

private fun KeyValueList.protoMergeImpl(plus: pbandk.Message?): KeyValueList = (plus as? KeyValueList)?.let {
    it.copy(
        values = values + plus.values,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun KeyValueList.Companion.decodeWithImpl(u: pbandk.MessageDecoder): KeyValueList {
    var values: pbandk.ListWithSize.Builder<io.opentelemetry.proto.common.v1.KeyValue>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> values = (values ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.common.v1.KeyValue> }
        }
    }

    return KeyValueList(pbandk.ListWithSize.Builder.fixed(values), unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForKeyValue")
public fun KeyValue?.orDefault(): io.opentelemetry.proto.common.v1.KeyValue = this ?: KeyValue.defaultInstance

private fun KeyValue.protoMergeImpl(plus: pbandk.Message?): KeyValue = (plus as? KeyValue)?.let {
    it.copy(
        value = value?.plus(plus.value) ?: plus.value,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun KeyValue.Companion.decodeWithImpl(u: pbandk.MessageDecoder): KeyValue {
    var key = ""
    var value: io.opentelemetry.proto.common.v1.AnyValue? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> key = _fieldValue as String
            2 -> value = _fieldValue as io.opentelemetry.proto.common.v1.AnyValue
        }
    }

    return KeyValue(key, value, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForInstrumentationScope")
public fun InstrumentationScope?.orDefault(): io.opentelemetry.proto.common.v1.InstrumentationScope = this ?: InstrumentationScope.defaultInstance

private fun InstrumentationScope.protoMergeImpl(plus: pbandk.Message?): InstrumentationScope = (plus as? InstrumentationScope)?.let {
    it.copy(
        attributes = attributes + plus.attributes,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun InstrumentationScope.Companion.decodeWithImpl(u: pbandk.MessageDecoder): InstrumentationScope {
    var name = ""
    var version = ""
    var attributes: pbandk.ListWithSize.Builder<io.opentelemetry.proto.common.v1.KeyValue>? = null
    var droppedAttributesCount = 0

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> name = _fieldValue as String
            2 -> version = _fieldValue as String
            3 -> attributes = (attributes ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.common.v1.KeyValue> }
            4 -> droppedAttributesCount = _fieldValue as Int
        }
    }

    return InstrumentationScope(name, version, pbandk.ListWithSize.Builder.fixed(attributes), droppedAttributesCount, unknownFields)
}
