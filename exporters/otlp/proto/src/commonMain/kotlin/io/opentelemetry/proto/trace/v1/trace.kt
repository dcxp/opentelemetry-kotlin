@file:OptIn(pbandk.PublicForGeneratedCode::class)

package io.opentelemetry.proto.trace.v1

@pbandk.Export
public data class TracesData(
    val resourceSpans: List<io.opentelemetry.proto.trace.v1.ResourceSpans> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.trace.v1.TracesData = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.trace.v1.TracesData> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.trace.v1.TracesData> {
        public val defaultInstance: io.opentelemetry.proto.trace.v1.TracesData by lazy { io.opentelemetry.proto.trace.v1.TracesData() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.trace.v1.TracesData = io.opentelemetry.proto.trace.v1.TracesData.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.trace.v1.TracesData> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.trace.v1.TracesData, *>>(1)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "resource_spans",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.trace.v1.ResourceSpans>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.trace.v1.ResourceSpans.Companion)),
                        jsonName = "resourceSpans",
                        value = io.opentelemetry.proto.trace.v1.TracesData::resourceSpans
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.trace.v1.TracesData",
                messageClass = io.opentelemetry.proto.trace.v1.TracesData::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class ResourceSpans(
    val resource: io.opentelemetry.proto.resource.v1.Resource? = null,
    val scopeSpans: List<io.opentelemetry.proto.trace.v1.ScopeSpans> = emptyList(),
    val schemaUrl: String = "",
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.trace.v1.ResourceSpans = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.trace.v1.ResourceSpans> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.trace.v1.ResourceSpans> {
        public val defaultInstance: io.opentelemetry.proto.trace.v1.ResourceSpans by lazy { io.opentelemetry.proto.trace.v1.ResourceSpans() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.trace.v1.ResourceSpans = io.opentelemetry.proto.trace.v1.ResourceSpans.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.trace.v1.ResourceSpans> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.trace.v1.ResourceSpans, *>>(3)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "resource",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.resource.v1.Resource.Companion),
                        jsonName = "resource",
                        value = io.opentelemetry.proto.trace.v1.ResourceSpans::resource
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "scope_spans",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.trace.v1.ScopeSpans>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.trace.v1.ScopeSpans.Companion)),
                        jsonName = "scopeSpans",
                        value = io.opentelemetry.proto.trace.v1.ResourceSpans::scopeSpans
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "schema_url",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(),
                        jsonName = "schemaUrl",
                        value = io.opentelemetry.proto.trace.v1.ResourceSpans::schemaUrl
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.trace.v1.ResourceSpans",
                messageClass = io.opentelemetry.proto.trace.v1.ResourceSpans::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class ScopeSpans(
    val scope: io.opentelemetry.proto.common.v1.InstrumentationScope? = null,
    val spans: List<io.opentelemetry.proto.trace.v1.Span> = emptyList(),
    val schemaUrl: String = "",
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.trace.v1.ScopeSpans = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.trace.v1.ScopeSpans> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.trace.v1.ScopeSpans> {
        public val defaultInstance: io.opentelemetry.proto.trace.v1.ScopeSpans by lazy { io.opentelemetry.proto.trace.v1.ScopeSpans() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.trace.v1.ScopeSpans = io.opentelemetry.proto.trace.v1.ScopeSpans.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.trace.v1.ScopeSpans> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.trace.v1.ScopeSpans, *>>(3)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "scope",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.common.v1.InstrumentationScope.Companion),
                        jsonName = "scope",
                        value = io.opentelemetry.proto.trace.v1.ScopeSpans::scope
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "spans",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.trace.v1.Span>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.trace.v1.Span.Companion)),
                        jsonName = "spans",
                        value = io.opentelemetry.proto.trace.v1.ScopeSpans::spans
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "schema_url",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(),
                        jsonName = "schemaUrl",
                        value = io.opentelemetry.proto.trace.v1.ScopeSpans::schemaUrl
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.trace.v1.ScopeSpans",
                messageClass = io.opentelemetry.proto.trace.v1.ScopeSpans::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class Span(
    val traceId: pbandk.ByteArr = pbandk.ByteArr.empty,
    val spanId: pbandk.ByteArr = pbandk.ByteArr.empty,
    val traceState: String = "",
    val parentSpanId: pbandk.ByteArr = pbandk.ByteArr.empty,
    val name: String = "",
    val kind: io.opentelemetry.proto.trace.v1.Span.SpanKind = io.opentelemetry.proto.trace.v1.Span.SpanKind.fromValue(0),
    val startTimeUnixNano: Long = 0L,
    val endTimeUnixNano: Long = 0L,
    val attributes: List<io.opentelemetry.proto.common.v1.KeyValue> = emptyList(),
    val droppedAttributesCount: Int = 0,
    val events: List<io.opentelemetry.proto.trace.v1.Span.Event> = emptyList(),
    val droppedEventsCount: Int = 0,
    val links: List<io.opentelemetry.proto.trace.v1.Span.Link> = emptyList(),
    val droppedLinksCount: Int = 0,
    val status: io.opentelemetry.proto.trace.v1.Status? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.trace.v1.Span = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.trace.v1.Span> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.trace.v1.Span> {
        public val defaultInstance: io.opentelemetry.proto.trace.v1.Span by lazy { io.opentelemetry.proto.trace.v1.Span() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.trace.v1.Span = io.opentelemetry.proto.trace.v1.Span.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.trace.v1.Span> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.trace.v1.Span, *>>(15)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "trace_id",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.Bytes(),
                        jsonName = "traceId",
                        value = io.opentelemetry.proto.trace.v1.Span::traceId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "span_id",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.Bytes(),
                        jsonName = "spanId",
                        value = io.opentelemetry.proto.trace.v1.Span::spanId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "trace_state",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(),
                        jsonName = "traceState",
                        value = io.opentelemetry.proto.trace.v1.Span::traceState
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "parent_span_id",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Primitive.Bytes(),
                        jsonName = "parentSpanId",
                        value = io.opentelemetry.proto.trace.v1.Span::parentSpanId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "name",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(),
                        jsonName = "name",
                        value = io.opentelemetry.proto.trace.v1.Span::name
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "kind",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = io.opentelemetry.proto.trace.v1.Span.SpanKind.Companion),
                        jsonName = "kind",
                        value = io.opentelemetry.proto.trace.v1.Span::kind
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "start_time_unix_nano",
                        number = 7,
                        type = pbandk.FieldDescriptor.Type.Primitive.Fixed64(),
                        jsonName = "startTimeUnixNano",
                        value = io.opentelemetry.proto.trace.v1.Span::startTimeUnixNano
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "end_time_unix_nano",
                        number = 8,
                        type = pbandk.FieldDescriptor.Type.Primitive.Fixed64(),
                        jsonName = "endTimeUnixNano",
                        value = io.opentelemetry.proto.trace.v1.Span::endTimeUnixNano
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "attributes",
                        number = 9,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.common.v1.KeyValue>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.common.v1.KeyValue.Companion)),
                        jsonName = "attributes",
                        value = io.opentelemetry.proto.trace.v1.Span::attributes
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "dropped_attributes_count",
                        number = 10,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(),
                        jsonName = "droppedAttributesCount",
                        value = io.opentelemetry.proto.trace.v1.Span::droppedAttributesCount
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "events",
                        number = 11,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.trace.v1.Span.Event>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.trace.v1.Span.Event.Companion)),
                        jsonName = "events",
                        value = io.opentelemetry.proto.trace.v1.Span::events
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "dropped_events_count",
                        number = 12,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(),
                        jsonName = "droppedEventsCount",
                        value = io.opentelemetry.proto.trace.v1.Span::droppedEventsCount
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "links",
                        number = 13,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.trace.v1.Span.Link>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.trace.v1.Span.Link.Companion)),
                        jsonName = "links",
                        value = io.opentelemetry.proto.trace.v1.Span::links
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "dropped_links_count",
                        number = 14,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(),
                        jsonName = "droppedLinksCount",
                        value = io.opentelemetry.proto.trace.v1.Span::droppedLinksCount
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "status",
                        number = 15,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.trace.v1.Status.Companion),
                        jsonName = "status",
                        value = io.opentelemetry.proto.trace.v1.Span::status
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.trace.v1.Span",
                messageClass = io.opentelemetry.proto.trace.v1.Span::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }

    public sealed class SpanKind(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
        override fun equals(other: kotlin.Any?): Boolean = other is Span.SpanKind && other.value == value
        override fun hashCode(): Int = value.hashCode()
        override fun toString(): String = "Span.SpanKind.${name ?: "UNRECOGNIZED"}(value=$value)"

        public object UNSPECIFIED : SpanKind(0, "SPAN_KIND_UNSPECIFIED")
        public object INTERNAL : SpanKind(1, "SPAN_KIND_INTERNAL")
        public object SERVER : SpanKind(2, "SPAN_KIND_SERVER")
        public object CLIENT : SpanKind(3, "SPAN_KIND_CLIENT")
        public object PRODUCER : SpanKind(4, "SPAN_KIND_PRODUCER")
        public object CONSUMER : SpanKind(5, "SPAN_KIND_CONSUMER")
        public class UNRECOGNIZED(value: Int) : SpanKind(value)

        public companion object : pbandk.Message.Enum.Companion<Span.SpanKind> {
            public val values: List<Span.SpanKind> by lazy { listOf(UNSPECIFIED, INTERNAL, SERVER, CLIENT, PRODUCER, CONSUMER) }
            override fun fromValue(value: Int): Span.SpanKind = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
            override fun fromName(name: String): Span.SpanKind = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No SpanKind with name: $name")
        }
    }

    public data class Event(
        val timeUnixNano: Long = 0L,
        val name: String = "",
        val attributes: List<io.opentelemetry.proto.common.v1.KeyValue> = emptyList(),
        val droppedAttributesCount: Int = 0,
        override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
    ) : pbandk.Message {
        override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.trace.v1.Span.Event = protoMergeImpl(other)
        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.trace.v1.Span.Event> get() = Companion.descriptor
        override val protoSize: Int by lazy { super.protoSize }
        public companion object : pbandk.Message.Companion<io.opentelemetry.proto.trace.v1.Span.Event> {
            public val defaultInstance: io.opentelemetry.proto.trace.v1.Span.Event by lazy { io.opentelemetry.proto.trace.v1.Span.Event() }
            override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.trace.v1.Span.Event = io.opentelemetry.proto.trace.v1.Span.Event.decodeWithImpl(u)

            override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.trace.v1.Span.Event> by lazy {
                val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.trace.v1.Span.Event, *>>(4)
                fieldsList.apply {
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "time_unix_nano",
                            number = 1,
                            type = pbandk.FieldDescriptor.Type.Primitive.Fixed64(),
                            jsonName = "timeUnixNano",
                            value = io.opentelemetry.proto.trace.v1.Span.Event::timeUnixNano
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "name",
                            number = 2,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(),
                            jsonName = "name",
                            value = io.opentelemetry.proto.trace.v1.Span.Event::name
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "attributes",
                            number = 3,
                            type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.common.v1.KeyValue>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.common.v1.KeyValue.Companion)),
                            jsonName = "attributes",
                            value = io.opentelemetry.proto.trace.v1.Span.Event::attributes
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "dropped_attributes_count",
                            number = 4,
                            type = pbandk.FieldDescriptor.Type.Primitive.UInt32(),
                            jsonName = "droppedAttributesCount",
                            value = io.opentelemetry.proto.trace.v1.Span.Event::droppedAttributesCount
                        )
                    )
                }
                pbandk.MessageDescriptor(
                    fullName = "opentelemetry.proto.trace.v1.Span.Event",
                    messageClass = io.opentelemetry.proto.trace.v1.Span.Event::class,
                    messageCompanion = this,
                    fields = fieldsList
                )
            }
        }
    }

    public data class Link(
        val traceId: pbandk.ByteArr = pbandk.ByteArr.empty,
        val spanId: pbandk.ByteArr = pbandk.ByteArr.empty,
        val traceState: String = "",
        val attributes: List<io.opentelemetry.proto.common.v1.KeyValue> = emptyList(),
        val droppedAttributesCount: Int = 0,
        override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
    ) : pbandk.Message {
        override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.trace.v1.Span.Link = protoMergeImpl(other)
        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.trace.v1.Span.Link> get() = Companion.descriptor
        override val protoSize: Int by lazy { super.protoSize }
        public companion object : pbandk.Message.Companion<io.opentelemetry.proto.trace.v1.Span.Link> {
            public val defaultInstance: io.opentelemetry.proto.trace.v1.Span.Link by lazy { io.opentelemetry.proto.trace.v1.Span.Link() }
            override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.trace.v1.Span.Link = io.opentelemetry.proto.trace.v1.Span.Link.decodeWithImpl(u)

            override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.trace.v1.Span.Link> by lazy {
                val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.trace.v1.Span.Link, *>>(5)
                fieldsList.apply {
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "trace_id",
                            number = 1,
                            type = pbandk.FieldDescriptor.Type.Primitive.Bytes(),
                            jsonName = "traceId",
                            value = io.opentelemetry.proto.trace.v1.Span.Link::traceId
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "span_id",
                            number = 2,
                            type = pbandk.FieldDescriptor.Type.Primitive.Bytes(),
                            jsonName = "spanId",
                            value = io.opentelemetry.proto.trace.v1.Span.Link::spanId
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "trace_state",
                            number = 3,
                            type = pbandk.FieldDescriptor.Type.Primitive.String(),
                            jsonName = "traceState",
                            value = io.opentelemetry.proto.trace.v1.Span.Link::traceState
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "attributes",
                            number = 4,
                            type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.common.v1.KeyValue>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.common.v1.KeyValue.Companion)),
                            jsonName = "attributes",
                            value = io.opentelemetry.proto.trace.v1.Span.Link::attributes
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "dropped_attributes_count",
                            number = 5,
                            type = pbandk.FieldDescriptor.Type.Primitive.UInt32(),
                            jsonName = "droppedAttributesCount",
                            value = io.opentelemetry.proto.trace.v1.Span.Link::droppedAttributesCount
                        )
                    )
                }
                pbandk.MessageDescriptor(
                    fullName = "opentelemetry.proto.trace.v1.Span.Link",
                    messageClass = io.opentelemetry.proto.trace.v1.Span.Link::class,
                    messageCompanion = this,
                    fields = fieldsList
                )
            }
        }
    }
}

@pbandk.Export
public data class Status(
    val message: String = "",
    val code: io.opentelemetry.proto.trace.v1.Status.StatusCode = io.opentelemetry.proto.trace.v1.Status.StatusCode.fromValue(0),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.trace.v1.Status = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.trace.v1.Status> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.trace.v1.Status> {
        public val defaultInstance: io.opentelemetry.proto.trace.v1.Status by lazy { io.opentelemetry.proto.trace.v1.Status() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.trace.v1.Status = io.opentelemetry.proto.trace.v1.Status.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.trace.v1.Status> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.trace.v1.Status, *>>(2)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "message",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(),
                        jsonName = "message",
                        value = io.opentelemetry.proto.trace.v1.Status::message
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "code",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = io.opentelemetry.proto.trace.v1.Status.StatusCode.Companion),
                        jsonName = "code",
                        value = io.opentelemetry.proto.trace.v1.Status::code
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.trace.v1.Status",
                messageClass = io.opentelemetry.proto.trace.v1.Status::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }

    public sealed class StatusCode(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
        override fun equals(other: kotlin.Any?): Boolean = other is Status.StatusCode && other.value == value
        override fun hashCode(): Int = value.hashCode()
        override fun toString(): String = "Status.StatusCode.${name ?: "UNRECOGNIZED"}(value=$value)"

        public object UNSET : StatusCode(0, "STATUS_CODE_UNSET")
        public object OK : StatusCode(1, "STATUS_CODE_OK")
        public object ERROR : StatusCode(2, "STATUS_CODE_ERROR")
        public class UNRECOGNIZED(value: Int) : StatusCode(value)

        public companion object : pbandk.Message.Enum.Companion<Status.StatusCode> {
            public val values: List<Status.StatusCode> by lazy { listOf(UNSET, OK, ERROR) }
            override fun fromValue(value: Int): Status.StatusCode = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
            override fun fromName(name: String): Status.StatusCode = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No StatusCode with name: $name")
        }
    }
}

@pbandk.Export
@pbandk.JsName("orDefaultForTracesData")
public fun TracesData?.orDefault(): io.opentelemetry.proto.trace.v1.TracesData = this ?: TracesData.defaultInstance

private fun TracesData.protoMergeImpl(plus: pbandk.Message?): TracesData = (plus as? TracesData)?.let {
    it.copy(
        resourceSpans = resourceSpans + plus.resourceSpans,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun TracesData.Companion.decodeWithImpl(u: pbandk.MessageDecoder): TracesData {
    var resourceSpans: pbandk.ListWithSize.Builder<io.opentelemetry.proto.trace.v1.ResourceSpans>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> resourceSpans = (resourceSpans ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.trace.v1.ResourceSpans> }
        }
    }

    return TracesData(pbandk.ListWithSize.Builder.fixed(resourceSpans), unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForResourceSpans")
public fun ResourceSpans?.orDefault(): io.opentelemetry.proto.trace.v1.ResourceSpans = this ?: ResourceSpans.defaultInstance

private fun ResourceSpans.protoMergeImpl(plus: pbandk.Message?): ResourceSpans = (plus as? ResourceSpans)?.let {
    it.copy(
        resource = resource?.plus(plus.resource) ?: plus.resource,
        scopeSpans = scopeSpans + plus.scopeSpans,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ResourceSpans.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ResourceSpans {
    var resource: io.opentelemetry.proto.resource.v1.Resource? = null
    var scopeSpans: pbandk.ListWithSize.Builder<io.opentelemetry.proto.trace.v1.ScopeSpans>? = null
    var schemaUrl = ""

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> resource = _fieldValue as io.opentelemetry.proto.resource.v1.Resource
            2 -> scopeSpans = (scopeSpans ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.trace.v1.ScopeSpans> }
            3 -> schemaUrl = _fieldValue as String
        }
    }

    return ResourceSpans(resource, pbandk.ListWithSize.Builder.fixed(scopeSpans), schemaUrl, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForScopeSpans")
public fun ScopeSpans?.orDefault(): io.opentelemetry.proto.trace.v1.ScopeSpans = this ?: ScopeSpans.defaultInstance

private fun ScopeSpans.protoMergeImpl(plus: pbandk.Message?): ScopeSpans = (plus as? ScopeSpans)?.let {
    it.copy(
        scope = scope?.plus(plus.scope) ?: plus.scope,
        spans = spans + plus.spans,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ScopeSpans.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ScopeSpans {
    var scope: io.opentelemetry.proto.common.v1.InstrumentationScope? = null
    var spans: pbandk.ListWithSize.Builder<io.opentelemetry.proto.trace.v1.Span>? = null
    var schemaUrl = ""

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> scope = _fieldValue as io.opentelemetry.proto.common.v1.InstrumentationScope
            2 -> spans = (spans ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.trace.v1.Span> }
            3 -> schemaUrl = _fieldValue as String
        }
    }

    return ScopeSpans(scope, pbandk.ListWithSize.Builder.fixed(spans), schemaUrl, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForSpan")
public fun Span?.orDefault(): io.opentelemetry.proto.trace.v1.Span = this ?: Span.defaultInstance

private fun Span.protoMergeImpl(plus: pbandk.Message?): Span = (plus as? Span)?.let {
    it.copy(
        attributes = attributes + plus.attributes,
        events = events + plus.events,
        links = links + plus.links,
        status = status?.plus(plus.status) ?: plus.status,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Span.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Span {
    var traceId: pbandk.ByteArr = pbandk.ByteArr.empty
    var spanId: pbandk.ByteArr = pbandk.ByteArr.empty
    var traceState = ""
    var parentSpanId: pbandk.ByteArr = pbandk.ByteArr.empty
    var name = ""
    var kind: io.opentelemetry.proto.trace.v1.Span.SpanKind = io.opentelemetry.proto.trace.v1.Span.SpanKind.fromValue(0)
    var startTimeUnixNano = 0L
    var endTimeUnixNano = 0L
    var attributes: pbandk.ListWithSize.Builder<io.opentelemetry.proto.common.v1.KeyValue>? = null
    var droppedAttributesCount = 0
    var events: pbandk.ListWithSize.Builder<io.opentelemetry.proto.trace.v1.Span.Event>? = null
    var droppedEventsCount = 0
    var links: pbandk.ListWithSize.Builder<io.opentelemetry.proto.trace.v1.Span.Link>? = null
    var droppedLinksCount = 0
    var status: io.opentelemetry.proto.trace.v1.Status? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> traceId = _fieldValue as pbandk.ByteArr
            2 -> spanId = _fieldValue as pbandk.ByteArr
            3 -> traceState = _fieldValue as String
            4 -> parentSpanId = _fieldValue as pbandk.ByteArr
            5 -> name = _fieldValue as String
            6 -> kind = _fieldValue as io.opentelemetry.proto.trace.v1.Span.SpanKind
            7 -> startTimeUnixNano = _fieldValue as Long
            8 -> endTimeUnixNano = _fieldValue as Long
            9 -> attributes = (attributes ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.common.v1.KeyValue> }
            10 -> droppedAttributesCount = _fieldValue as Int
            11 -> events = (events ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.trace.v1.Span.Event> }
            12 -> droppedEventsCount = _fieldValue as Int
            13 -> links = (links ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.trace.v1.Span.Link> }
            14 -> droppedLinksCount = _fieldValue as Int
            15 -> status = _fieldValue as io.opentelemetry.proto.trace.v1.Status
        }
    }

    return Span(traceId, spanId, traceState, parentSpanId,
        name, kind, startTimeUnixNano, endTimeUnixNano,
        pbandk.ListWithSize.Builder.fixed(attributes), droppedAttributesCount, pbandk.ListWithSize.Builder.fixed(events), droppedEventsCount,
        pbandk.ListWithSize.Builder.fixed(links), droppedLinksCount, status, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForSpanEvent")
public fun Span.Event?.orDefault(): io.opentelemetry.proto.trace.v1.Span.Event = this ?: Span.Event.defaultInstance

private fun Span.Event.protoMergeImpl(plus: pbandk.Message?): Span.Event = (plus as? Span.Event)?.let {
    it.copy(
        attributes = attributes + plus.attributes,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Span.Event.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Span.Event {
    var timeUnixNano = 0L
    var name = ""
    var attributes: pbandk.ListWithSize.Builder<io.opentelemetry.proto.common.v1.KeyValue>? = null
    var droppedAttributesCount = 0

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> timeUnixNano = _fieldValue as Long
            2 -> name = _fieldValue as String
            3 -> attributes = (attributes ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.common.v1.KeyValue> }
            4 -> droppedAttributesCount = _fieldValue as Int
        }
    }

    return Span.Event(timeUnixNano, name, pbandk.ListWithSize.Builder.fixed(attributes), droppedAttributesCount, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForSpanLink")
public fun Span.Link?.orDefault(): io.opentelemetry.proto.trace.v1.Span.Link = this ?: Span.Link.defaultInstance

private fun Span.Link.protoMergeImpl(plus: pbandk.Message?): Span.Link = (plus as? Span.Link)?.let {
    it.copy(
        attributes = attributes + plus.attributes,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Span.Link.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Span.Link {
    var traceId: pbandk.ByteArr = pbandk.ByteArr.empty
    var spanId: pbandk.ByteArr = pbandk.ByteArr.empty
    var traceState = ""
    var attributes: pbandk.ListWithSize.Builder<io.opentelemetry.proto.common.v1.KeyValue>? = null
    var droppedAttributesCount = 0

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> traceId = _fieldValue as pbandk.ByteArr
            2 -> spanId = _fieldValue as pbandk.ByteArr
            3 -> traceState = _fieldValue as String
            4 -> attributes = (attributes ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.common.v1.KeyValue> }
            5 -> droppedAttributesCount = _fieldValue as Int
        }
    }

    return Span.Link(traceId, spanId, traceState, pbandk.ListWithSize.Builder.fixed(attributes),
        droppedAttributesCount, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForStatus")
public fun Status?.orDefault(): io.opentelemetry.proto.trace.v1.Status = this ?: Status.defaultInstance

private fun Status.protoMergeImpl(plus: pbandk.Message?): Status = (plus as? Status)?.let {
    it.copy(
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Status.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Status {
    var message = ""
    var code: io.opentelemetry.proto.trace.v1.Status.StatusCode = io.opentelemetry.proto.trace.v1.Status.StatusCode.fromValue(0)

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            2 -> message = _fieldValue as String
            3 -> code = _fieldValue as io.opentelemetry.proto.trace.v1.Status.StatusCode
        }
    }

    return Status(message, code, unknownFields)
}
