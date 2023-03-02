@file:OptIn(pbandk.PublicForGeneratedCode::class)

package io.opentelemetry.proto.resource.v1

@pbandk.Export
public data class Resource(
    val attributes: List<io.opentelemetry.proto.common.v1.KeyValue> = emptyList(),
    val droppedAttributesCount: Int = 0,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.resource.v1.Resource = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.resource.v1.Resource> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.resource.v1.Resource> {
        public val defaultInstance: io.opentelemetry.proto.resource.v1.Resource by lazy { io.opentelemetry.proto.resource.v1.Resource() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.resource.v1.Resource = io.opentelemetry.proto.resource.v1.Resource.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.resource.v1.Resource> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.resource.v1.Resource, *>>(2)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "attributes",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.common.v1.KeyValue>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.common.v1.KeyValue.Companion)),
                        jsonName = "attributes",
                        value = io.opentelemetry.proto.resource.v1.Resource::attributes
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "dropped_attributes_count",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(),
                        jsonName = "droppedAttributesCount",
                        value = io.opentelemetry.proto.resource.v1.Resource::droppedAttributesCount
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.resource.v1.Resource",
                messageClass = io.opentelemetry.proto.resource.v1.Resource::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
@pbandk.JsName("orDefaultForResource")
public fun Resource?.orDefault(): io.opentelemetry.proto.resource.v1.Resource = this ?: Resource.defaultInstance

private fun Resource.protoMergeImpl(plus: pbandk.Message?): Resource = (plus as? Resource)?.let {
    it.copy(
        attributes = attributes + plus.attributes,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Resource.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Resource {
    var attributes: pbandk.ListWithSize.Builder<io.opentelemetry.proto.common.v1.KeyValue>? = null
    var droppedAttributesCount = 0

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> attributes = (attributes ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.common.v1.KeyValue> }
            2 -> droppedAttributesCount = _fieldValue as Int
        }
    }

    return Resource(pbandk.ListWithSize.Builder.fixed(attributes), droppedAttributesCount, unknownFields)
}
