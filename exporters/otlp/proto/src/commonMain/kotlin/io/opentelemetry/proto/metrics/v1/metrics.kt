@file:OptIn(pbandk.PublicForGeneratedCode::class)

package io.opentelemetry.proto.metrics.v1

@pbandk.Export
public sealed class AggregationTemporality(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
    override fun equals(other: kotlin.Any?): Boolean = other is AggregationTemporality && other.value == value
    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = "AggregationTemporality.${name ?: "UNRECOGNIZED"}(value=$value)"

    public object UNSPECIFIED : AggregationTemporality(0, "AGGREGATION_TEMPORALITY_UNSPECIFIED")
    public object DELTA : AggregationTemporality(1, "AGGREGATION_TEMPORALITY_DELTA")
    public object CUMULATIVE : AggregationTemporality(2, "AGGREGATION_TEMPORALITY_CUMULATIVE")
    public class UNRECOGNIZED(value: Int) : AggregationTemporality(value)

    public companion object : pbandk.Message.Enum.Companion<AggregationTemporality> {
        public val values: List<AggregationTemporality> by lazy { listOf(UNSPECIFIED, DELTA, CUMULATIVE) }
        override fun fromValue(value: Int): AggregationTemporality = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
        override fun fromName(name: String): AggregationTemporality = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No AggregationTemporality with name: $name")
    }
}

@pbandk.Export
public sealed class DataPointFlags(override val value: Int, override val name: String? = null) : pbandk.Message.Enum {
    override fun equals(other: kotlin.Any?): Boolean = other is DataPointFlags && other.value == value
    override fun hashCode(): Int = value.hashCode()
    override fun toString(): String = "DataPointFlags.${name ?: "UNRECOGNIZED"}(value=$value)"

    public object FLAG_NONE : DataPointFlags(0, "FLAG_NONE")
    public object FLAG_NO_RECORDED_VALUE : DataPointFlags(1, "FLAG_NO_RECORDED_VALUE")
    public class UNRECOGNIZED(value: Int) : DataPointFlags(value)

    public companion object : pbandk.Message.Enum.Companion<DataPointFlags> {
        public val values: List<DataPointFlags> by lazy { listOf(FLAG_NONE, FLAG_NO_RECORDED_VALUE) }
        override fun fromValue(value: Int): DataPointFlags = values.firstOrNull { it.value == value } ?: UNRECOGNIZED(value)
        override fun fromName(name: String): DataPointFlags = values.firstOrNull { it.name == name } ?: throw IllegalArgumentException("No DataPointFlags with name: $name")
    }
}

@pbandk.Export
public data class MetricsData(
    val resourceMetrics: List<io.opentelemetry.proto.metrics.v1.ResourceMetrics> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.metrics.v1.MetricsData = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.MetricsData> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.metrics.v1.MetricsData> {
        public val defaultInstance: io.opentelemetry.proto.metrics.v1.MetricsData by lazy { io.opentelemetry.proto.metrics.v1.MetricsData() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.metrics.v1.MetricsData = io.opentelemetry.proto.metrics.v1.MetricsData.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.MetricsData> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.metrics.v1.MetricsData, *>>(1)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "resource_metrics",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.metrics.v1.ResourceMetrics>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.metrics.v1.ResourceMetrics.Companion)),
                        jsonName = "resourceMetrics",
                        value = io.opentelemetry.proto.metrics.v1.MetricsData::resourceMetrics
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.metrics.v1.MetricsData",
                messageClass = io.opentelemetry.proto.metrics.v1.MetricsData::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class ResourceMetrics(
    val resource: io.opentelemetry.proto.resource.v1.Resource? = null,
    val scopeMetrics: List<io.opentelemetry.proto.metrics.v1.ScopeMetrics> = emptyList(),
    val schemaUrl: String = "",
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.metrics.v1.ResourceMetrics = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.ResourceMetrics> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.metrics.v1.ResourceMetrics> {
        public val defaultInstance: io.opentelemetry.proto.metrics.v1.ResourceMetrics by lazy { io.opentelemetry.proto.metrics.v1.ResourceMetrics() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.metrics.v1.ResourceMetrics = io.opentelemetry.proto.metrics.v1.ResourceMetrics.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.ResourceMetrics> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.metrics.v1.ResourceMetrics, *>>(3)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "resource",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.resource.v1.Resource.Companion),
                        jsonName = "resource",
                        value = io.opentelemetry.proto.metrics.v1.ResourceMetrics::resource
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "scope_metrics",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.metrics.v1.ScopeMetrics>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.metrics.v1.ScopeMetrics.Companion)),
                        jsonName = "scopeMetrics",
                        value = io.opentelemetry.proto.metrics.v1.ResourceMetrics::scopeMetrics
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "schema_url",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(),
                        jsonName = "schemaUrl",
                        value = io.opentelemetry.proto.metrics.v1.ResourceMetrics::schemaUrl
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.metrics.v1.ResourceMetrics",
                messageClass = io.opentelemetry.proto.metrics.v1.ResourceMetrics::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class ScopeMetrics(
    val scope: io.opentelemetry.proto.common.v1.InstrumentationScope? = null,
    val metrics: List<io.opentelemetry.proto.metrics.v1.Metric> = emptyList(),
    val schemaUrl: String = "",
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.metrics.v1.ScopeMetrics = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.ScopeMetrics> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.metrics.v1.ScopeMetrics> {
        public val defaultInstance: io.opentelemetry.proto.metrics.v1.ScopeMetrics by lazy { io.opentelemetry.proto.metrics.v1.ScopeMetrics() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.metrics.v1.ScopeMetrics = io.opentelemetry.proto.metrics.v1.ScopeMetrics.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.ScopeMetrics> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.metrics.v1.ScopeMetrics, *>>(3)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "scope",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.common.v1.InstrumentationScope.Companion),
                        jsonName = "scope",
                        value = io.opentelemetry.proto.metrics.v1.ScopeMetrics::scope
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "metrics",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.metrics.v1.Metric>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.metrics.v1.Metric.Companion)),
                        jsonName = "metrics",
                        value = io.opentelemetry.proto.metrics.v1.ScopeMetrics::metrics
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "schema_url",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(),
                        jsonName = "schemaUrl",
                        value = io.opentelemetry.proto.metrics.v1.ScopeMetrics::schemaUrl
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.metrics.v1.ScopeMetrics",
                messageClass = io.opentelemetry.proto.metrics.v1.ScopeMetrics::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class Metric(
    val name: String = "",
    val description: String = "",
    val unit: String = "",
    val data: Data<*>? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    public sealed class Data<V>(value: V) : pbandk.Message.OneOf<V>(value) {
        public class Gauge(gauge: io.opentelemetry.proto.metrics.v1.Gauge) : Data<io.opentelemetry.proto.metrics.v1.Gauge>(gauge)
        public class Sum(sum: io.opentelemetry.proto.metrics.v1.Sum) : Data<io.opentelemetry.proto.metrics.v1.Sum>(sum)
        public class Histogram(histogram: io.opentelemetry.proto.metrics.v1.Histogram) : Data<io.opentelemetry.proto.metrics.v1.Histogram>(histogram)
        public class ExponentialHistogram(exponentialHistogram: io.opentelemetry.proto.metrics.v1.ExponentialHistogram) : Data<io.opentelemetry.proto.metrics.v1.ExponentialHistogram>(exponentialHistogram)
        public class Summary(summary: io.opentelemetry.proto.metrics.v1.Summary) : Data<io.opentelemetry.proto.metrics.v1.Summary>(summary)
    }

    val gauge: io.opentelemetry.proto.metrics.v1.Gauge?
        get() = (data as? Data.Gauge)?.value
    val sum: io.opentelemetry.proto.metrics.v1.Sum?
        get() = (data as? Data.Sum)?.value
    val histogram: io.opentelemetry.proto.metrics.v1.Histogram?
        get() = (data as? Data.Histogram)?.value
    val exponentialHistogram: io.opentelemetry.proto.metrics.v1.ExponentialHistogram?
        get() = (data as? Data.ExponentialHistogram)?.value
    val summary: io.opentelemetry.proto.metrics.v1.Summary?
        get() = (data as? Data.Summary)?.value

    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.metrics.v1.Metric = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.Metric> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.metrics.v1.Metric> {
        public val defaultInstance: io.opentelemetry.proto.metrics.v1.Metric by lazy { io.opentelemetry.proto.metrics.v1.Metric() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.metrics.v1.Metric = io.opentelemetry.proto.metrics.v1.Metric.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.Metric> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.metrics.v1.Metric, *>>(8)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "name",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(),
                        jsonName = "name",
                        value = io.opentelemetry.proto.metrics.v1.Metric::name
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "description",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(),
                        jsonName = "description",
                        value = io.opentelemetry.proto.metrics.v1.Metric::description
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "unit",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.String(),
                        jsonName = "unit",
                        value = io.opentelemetry.proto.metrics.v1.Metric::unit
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "gauge",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.metrics.v1.Gauge.Companion),
                        oneofMember = true,
                        jsonName = "gauge",
                        value = io.opentelemetry.proto.metrics.v1.Metric::gauge
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "sum",
                        number = 7,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.metrics.v1.Sum.Companion),
                        oneofMember = true,
                        jsonName = "sum",
                        value = io.opentelemetry.proto.metrics.v1.Metric::sum
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "histogram",
                        number = 9,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.metrics.v1.Histogram.Companion),
                        oneofMember = true,
                        jsonName = "histogram",
                        value = io.opentelemetry.proto.metrics.v1.Metric::histogram
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "exponential_histogram",
                        number = 10,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.metrics.v1.ExponentialHistogram.Companion),
                        oneofMember = true,
                        jsonName = "exponentialHistogram",
                        value = io.opentelemetry.proto.metrics.v1.Metric::exponentialHistogram
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "summary",
                        number = 11,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.metrics.v1.Summary.Companion),
                        oneofMember = true,
                        jsonName = "summary",
                        value = io.opentelemetry.proto.metrics.v1.Metric::summary
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.metrics.v1.Metric",
                messageClass = io.opentelemetry.proto.metrics.v1.Metric::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class Gauge(
    val dataPoints: List<io.opentelemetry.proto.metrics.v1.NumberDataPoint> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.metrics.v1.Gauge = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.Gauge> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.metrics.v1.Gauge> {
        public val defaultInstance: io.opentelemetry.proto.metrics.v1.Gauge by lazy { io.opentelemetry.proto.metrics.v1.Gauge() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.metrics.v1.Gauge = io.opentelemetry.proto.metrics.v1.Gauge.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.Gauge> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.metrics.v1.Gauge, *>>(1)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "data_points",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.metrics.v1.NumberDataPoint>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.metrics.v1.NumberDataPoint.Companion)),
                        jsonName = "dataPoints",
                        value = io.opentelemetry.proto.metrics.v1.Gauge::dataPoints
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.metrics.v1.Gauge",
                messageClass = io.opentelemetry.proto.metrics.v1.Gauge::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class Sum(
    val dataPoints: List<io.opentelemetry.proto.metrics.v1.NumberDataPoint> = emptyList(),
    val aggregationTemporality: io.opentelemetry.proto.metrics.v1.AggregationTemporality = io.opentelemetry.proto.metrics.v1.AggregationTemporality.fromValue(0),
    val isMonotonic: Boolean = false,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.metrics.v1.Sum = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.Sum> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.metrics.v1.Sum> {
        public val defaultInstance: io.opentelemetry.proto.metrics.v1.Sum by lazy { io.opentelemetry.proto.metrics.v1.Sum() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.metrics.v1.Sum = io.opentelemetry.proto.metrics.v1.Sum.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.Sum> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.metrics.v1.Sum, *>>(3)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "data_points",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.metrics.v1.NumberDataPoint>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.metrics.v1.NumberDataPoint.Companion)),
                        jsonName = "dataPoints",
                        value = io.opentelemetry.proto.metrics.v1.Sum::dataPoints
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "aggregation_temporality",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = io.opentelemetry.proto.metrics.v1.AggregationTemporality.Companion),
                        jsonName = "aggregationTemporality",
                        value = io.opentelemetry.proto.metrics.v1.Sum::aggregationTemporality
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "is_monotonic",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.Bool(),
                        jsonName = "isMonotonic",
                        value = io.opentelemetry.proto.metrics.v1.Sum::isMonotonic
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.metrics.v1.Sum",
                messageClass = io.opentelemetry.proto.metrics.v1.Sum::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class Histogram(
    val dataPoints: List<io.opentelemetry.proto.metrics.v1.HistogramDataPoint> = emptyList(),
    val aggregationTemporality: io.opentelemetry.proto.metrics.v1.AggregationTemporality = io.opentelemetry.proto.metrics.v1.AggregationTemporality.fromValue(0),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.metrics.v1.Histogram = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.Histogram> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.metrics.v1.Histogram> {
        public val defaultInstance: io.opentelemetry.proto.metrics.v1.Histogram by lazy { io.opentelemetry.proto.metrics.v1.Histogram() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.metrics.v1.Histogram = io.opentelemetry.proto.metrics.v1.Histogram.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.Histogram> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.metrics.v1.Histogram, *>>(2)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "data_points",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.metrics.v1.HistogramDataPoint>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.metrics.v1.HistogramDataPoint.Companion)),
                        jsonName = "dataPoints",
                        value = io.opentelemetry.proto.metrics.v1.Histogram::dataPoints
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "aggregation_temporality",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = io.opentelemetry.proto.metrics.v1.AggregationTemporality.Companion),
                        jsonName = "aggregationTemporality",
                        value = io.opentelemetry.proto.metrics.v1.Histogram::aggregationTemporality
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.metrics.v1.Histogram",
                messageClass = io.opentelemetry.proto.metrics.v1.Histogram::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class ExponentialHistogram(
    val dataPoints: List<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint> = emptyList(),
    val aggregationTemporality: io.opentelemetry.proto.metrics.v1.AggregationTemporality = io.opentelemetry.proto.metrics.v1.AggregationTemporality.fromValue(0),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.metrics.v1.ExponentialHistogram = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.ExponentialHistogram> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.metrics.v1.ExponentialHistogram> {
        public val defaultInstance: io.opentelemetry.proto.metrics.v1.ExponentialHistogram by lazy { io.opentelemetry.proto.metrics.v1.ExponentialHistogram() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.metrics.v1.ExponentialHistogram = io.opentelemetry.proto.metrics.v1.ExponentialHistogram.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.ExponentialHistogram> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.metrics.v1.ExponentialHistogram, *>>(2)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "data_points",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Companion)),
                        jsonName = "dataPoints",
                        value = io.opentelemetry.proto.metrics.v1.ExponentialHistogram::dataPoints
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "aggregation_temporality",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Enum(enumCompanion = io.opentelemetry.proto.metrics.v1.AggregationTemporality.Companion),
                        jsonName = "aggregationTemporality",
                        value = io.opentelemetry.proto.metrics.v1.ExponentialHistogram::aggregationTemporality
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.metrics.v1.ExponentialHistogram",
                messageClass = io.opentelemetry.proto.metrics.v1.ExponentialHistogram::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class Summary(
    val dataPoints: List<io.opentelemetry.proto.metrics.v1.SummaryDataPoint> = emptyList(),
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.metrics.v1.Summary = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.Summary> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.metrics.v1.Summary> {
        public val defaultInstance: io.opentelemetry.proto.metrics.v1.Summary by lazy { io.opentelemetry.proto.metrics.v1.Summary() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.metrics.v1.Summary = io.opentelemetry.proto.metrics.v1.Summary.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.Summary> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.metrics.v1.Summary, *>>(1)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "data_points",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.metrics.v1.SummaryDataPoint>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.metrics.v1.SummaryDataPoint.Companion)),
                        jsonName = "dataPoints",
                        value = io.opentelemetry.proto.metrics.v1.Summary::dataPoints
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.metrics.v1.Summary",
                messageClass = io.opentelemetry.proto.metrics.v1.Summary::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class NumberDataPoint(
    val attributes: List<io.opentelemetry.proto.common.v1.KeyValue> = emptyList(),
    val startTimeUnixNano: Long = 0L,
    val timeUnixNano: Long = 0L,
    val exemplars: List<io.opentelemetry.proto.metrics.v1.Exemplar> = emptyList(),
    val flags: Int = 0,
    val value: Value<*>? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    public sealed class Value<V>(value: V) : pbandk.Message.OneOf<V>(value) {
        public class AsDouble(asDouble: Double = 0.0) : Value<Double>(asDouble)
        public class AsInt(asInt: Long = 0L) : Value<Long>(asInt)
    }

    val asDouble: Double?
        get() = (value as? Value.AsDouble)?.value
    val asInt: Long?
        get() = (value as? Value.AsInt)?.value

    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.metrics.v1.NumberDataPoint = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.NumberDataPoint> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.metrics.v1.NumberDataPoint> {
        public val defaultInstance: io.opentelemetry.proto.metrics.v1.NumberDataPoint by lazy { io.opentelemetry.proto.metrics.v1.NumberDataPoint() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.metrics.v1.NumberDataPoint = io.opentelemetry.proto.metrics.v1.NumberDataPoint.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.NumberDataPoint> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.metrics.v1.NumberDataPoint, *>>(7)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "start_time_unix_nano",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.Fixed64(),
                        jsonName = "startTimeUnixNano",
                        value = io.opentelemetry.proto.metrics.v1.NumberDataPoint::startTimeUnixNano
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "time_unix_nano",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.Fixed64(),
                        jsonName = "timeUnixNano",
                        value = io.opentelemetry.proto.metrics.v1.NumberDataPoint::timeUnixNano
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "as_double",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Primitive.Double(hasPresence = true),
                        oneofMember = true,
                        jsonName = "asDouble",
                        value = io.opentelemetry.proto.metrics.v1.NumberDataPoint::asDouble
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "exemplars",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.metrics.v1.Exemplar>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.metrics.v1.Exemplar.Companion)),
                        jsonName = "exemplars",
                        value = io.opentelemetry.proto.metrics.v1.NumberDataPoint::exemplars
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "as_int",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Primitive.SFixed64(hasPresence = true),
                        oneofMember = true,
                        jsonName = "asInt",
                        value = io.opentelemetry.proto.metrics.v1.NumberDataPoint::asInt
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "attributes",
                        number = 7,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.common.v1.KeyValue>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.common.v1.KeyValue.Companion)),
                        jsonName = "attributes",
                        value = io.opentelemetry.proto.metrics.v1.NumberDataPoint::attributes
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "flags",
                        number = 8,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(),
                        jsonName = "flags",
                        value = io.opentelemetry.proto.metrics.v1.NumberDataPoint::flags
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.metrics.v1.NumberDataPoint",
                messageClass = io.opentelemetry.proto.metrics.v1.NumberDataPoint::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class HistogramDataPoint(
    val attributes: List<io.opentelemetry.proto.common.v1.KeyValue> = emptyList(),
    val startTimeUnixNano: Long = 0L,
    val timeUnixNano: Long = 0L,
    val count: Long = 0L,
    val bucketCounts: List<Long> = emptyList(),
    val explicitBounds: List<Double> = emptyList(),
    val exemplars: List<io.opentelemetry.proto.metrics.v1.Exemplar> = emptyList(),
    val flags: Int = 0,
    val sum: Double? = null,
    val min: Double? = null,
    val max: Double? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.metrics.v1.HistogramDataPoint = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.HistogramDataPoint> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.metrics.v1.HistogramDataPoint> {
        public val defaultInstance: io.opentelemetry.proto.metrics.v1.HistogramDataPoint by lazy { io.opentelemetry.proto.metrics.v1.HistogramDataPoint() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.metrics.v1.HistogramDataPoint = io.opentelemetry.proto.metrics.v1.HistogramDataPoint.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.HistogramDataPoint> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.metrics.v1.HistogramDataPoint, *>>(11)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "start_time_unix_nano",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.Fixed64(),
                        jsonName = "startTimeUnixNano",
                        value = io.opentelemetry.proto.metrics.v1.HistogramDataPoint::startTimeUnixNano
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "time_unix_nano",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.Fixed64(),
                        jsonName = "timeUnixNano",
                        value = io.opentelemetry.proto.metrics.v1.HistogramDataPoint::timeUnixNano
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "count",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Primitive.Fixed64(),
                        jsonName = "count",
                        value = io.opentelemetry.proto.metrics.v1.HistogramDataPoint::count
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "sum",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Primitive.Double(hasPresence = true),
                        jsonName = "sum",
                        value = io.opentelemetry.proto.metrics.v1.HistogramDataPoint::sum
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "bucket_counts",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Repeated<Long>(valueType = pbandk.FieldDescriptor.Type.Primitive.Fixed64(), packed = true),
                        jsonName = "bucketCounts",
                        value = io.opentelemetry.proto.metrics.v1.HistogramDataPoint::bucketCounts
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "explicit_bounds",
                        number = 7,
                        type = pbandk.FieldDescriptor.Type.Repeated<Double>(valueType = pbandk.FieldDescriptor.Type.Primitive.Double(), packed = true),
                        jsonName = "explicitBounds",
                        value = io.opentelemetry.proto.metrics.v1.HistogramDataPoint::explicitBounds
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "exemplars",
                        number = 8,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.metrics.v1.Exemplar>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.metrics.v1.Exemplar.Companion)),
                        jsonName = "exemplars",
                        value = io.opentelemetry.proto.metrics.v1.HistogramDataPoint::exemplars
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "attributes",
                        number = 9,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.common.v1.KeyValue>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.common.v1.KeyValue.Companion)),
                        jsonName = "attributes",
                        value = io.opentelemetry.proto.metrics.v1.HistogramDataPoint::attributes
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "flags",
                        number = 10,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(),
                        jsonName = "flags",
                        value = io.opentelemetry.proto.metrics.v1.HistogramDataPoint::flags
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "min",
                        number = 11,
                        type = pbandk.FieldDescriptor.Type.Primitive.Double(hasPresence = true),
                        jsonName = "min",
                        value = io.opentelemetry.proto.metrics.v1.HistogramDataPoint::min
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "max",
                        number = 12,
                        type = pbandk.FieldDescriptor.Type.Primitive.Double(hasPresence = true),
                        jsonName = "max",
                        value = io.opentelemetry.proto.metrics.v1.HistogramDataPoint::max
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.metrics.v1.HistogramDataPoint",
                messageClass = io.opentelemetry.proto.metrics.v1.HistogramDataPoint::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
public data class ExponentialHistogramDataPoint(
    val attributes: List<io.opentelemetry.proto.common.v1.KeyValue> = emptyList(),
    val startTimeUnixNano: Long = 0L,
    val timeUnixNano: Long = 0L,
    val count: Long = 0L,
    val scale: Int = 0,
    val zeroCount: Long = 0L,
    val positive: io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets? = null,
    val negative: io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets? = null,
    val flags: Int = 0,
    val exemplars: List<io.opentelemetry.proto.metrics.v1.Exemplar> = emptyList(),
    val sum: Double? = null,
    val min: Double? = null,
    val max: Double? = null,
    val zeroThreshold: Double? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint> {
        public val defaultInstance: io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint by lazy { io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint, *>>(14)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "attributes",
                        number = 1,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.common.v1.KeyValue>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.common.v1.KeyValue.Companion)),
                        jsonName = "attributes",
                        value = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint::attributes
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "start_time_unix_nano",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.Fixed64(),
                        jsonName = "startTimeUnixNano",
                        value = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint::startTimeUnixNano
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "time_unix_nano",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.Fixed64(),
                        jsonName = "timeUnixNano",
                        value = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint::timeUnixNano
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "count",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Primitive.Fixed64(),
                        jsonName = "count",
                        value = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint::count
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "sum",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Primitive.Double(hasPresence = true),
                        jsonName = "sum",
                        value = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint::sum
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "scale",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Primitive.SInt32(),
                        jsonName = "scale",
                        value = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint::scale
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "zero_count",
                        number = 7,
                        type = pbandk.FieldDescriptor.Type.Primitive.Fixed64(),
                        jsonName = "zeroCount",
                        value = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint::zeroCount
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "positive",
                        number = 8,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets.Companion),
                        jsonName = "positive",
                        value = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint::positive
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "negative",
                        number = 9,
                        type = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets.Companion),
                        jsonName = "negative",
                        value = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint::negative
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "flags",
                        number = 10,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(),
                        jsonName = "flags",
                        value = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint::flags
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "exemplars",
                        number = 11,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.metrics.v1.Exemplar>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.metrics.v1.Exemplar.Companion)),
                        jsonName = "exemplars",
                        value = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint::exemplars
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "min",
                        number = 12,
                        type = pbandk.FieldDescriptor.Type.Primitive.Double(hasPresence = true),
                        jsonName = "min",
                        value = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint::min
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "max",
                        number = 13,
                        type = pbandk.FieldDescriptor.Type.Primitive.Double(hasPresence = true),
                        jsonName = "max",
                        value = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint::max
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "zero_threshold",
                        number = 14,
                        type = pbandk.FieldDescriptor.Type.Primitive.Double(hasPresence = true),
                        jsonName = "zeroThreshold",
                        value = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint::zeroThreshold
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint",
                messageClass = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }

    public data class Buckets(
        val offset: Int = 0,
        val bucketCounts: List<Long> = emptyList(),
        override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
    ) : pbandk.Message {
        override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets = protoMergeImpl(other)
        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets> get() = Companion.descriptor
        override val protoSize: Int by lazy { super.protoSize }
        public companion object : pbandk.Message.Companion<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets> {
            public val defaultInstance: io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets by lazy { io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets() }
            override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets.decodeWithImpl(u)

            override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets> by lazy {
                val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets, *>>(2)
                fieldsList.apply {
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "offset",
                            number = 1,
                            type = pbandk.FieldDescriptor.Type.Primitive.SInt32(),
                            jsonName = "offset",
                            value = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets::offset
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "bucket_counts",
                            number = 2,
                            type = pbandk.FieldDescriptor.Type.Repeated<Long>(valueType = pbandk.FieldDescriptor.Type.Primitive.UInt64(), packed = true),
                            jsonName = "bucketCounts",
                            value = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets::bucketCounts
                        )
                    )
                }
                pbandk.MessageDescriptor(
                    fullName = "opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets",
                    messageClass = io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets::class,
                    messageCompanion = this,
                    fields = fieldsList
                )
            }
        }
    }
}

@pbandk.Export
public data class SummaryDataPoint(
    val attributes: List<io.opentelemetry.proto.common.v1.KeyValue> = emptyList(),
    val startTimeUnixNano: Long = 0L,
    val timeUnixNano: Long = 0L,
    val count: Long = 0L,
    val sum: Double = 0.0,
    val quantileValues: List<io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile> = emptyList(),
    val flags: Int = 0,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.metrics.v1.SummaryDataPoint = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.SummaryDataPoint> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.metrics.v1.SummaryDataPoint> {
        public val defaultInstance: io.opentelemetry.proto.metrics.v1.SummaryDataPoint by lazy { io.opentelemetry.proto.metrics.v1.SummaryDataPoint() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.metrics.v1.SummaryDataPoint = io.opentelemetry.proto.metrics.v1.SummaryDataPoint.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.SummaryDataPoint> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.metrics.v1.SummaryDataPoint, *>>(7)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "start_time_unix_nano",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.Fixed64(),
                        jsonName = "startTimeUnixNano",
                        value = io.opentelemetry.proto.metrics.v1.SummaryDataPoint::startTimeUnixNano
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "time_unix_nano",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.Fixed64(),
                        jsonName = "timeUnixNano",
                        value = io.opentelemetry.proto.metrics.v1.SummaryDataPoint::timeUnixNano
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "count",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Primitive.Fixed64(),
                        jsonName = "count",
                        value = io.opentelemetry.proto.metrics.v1.SummaryDataPoint::count
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "sum",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Primitive.Double(),
                        jsonName = "sum",
                        value = io.opentelemetry.proto.metrics.v1.SummaryDataPoint::sum
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "quantile_values",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile.Companion)),
                        jsonName = "quantileValues",
                        value = io.opentelemetry.proto.metrics.v1.SummaryDataPoint::quantileValues
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "attributes",
                        number = 7,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.common.v1.KeyValue>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.common.v1.KeyValue.Companion)),
                        jsonName = "attributes",
                        value = io.opentelemetry.proto.metrics.v1.SummaryDataPoint::attributes
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "flags",
                        number = 8,
                        type = pbandk.FieldDescriptor.Type.Primitive.UInt32(),
                        jsonName = "flags",
                        value = io.opentelemetry.proto.metrics.v1.SummaryDataPoint::flags
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.metrics.v1.SummaryDataPoint",
                messageClass = io.opentelemetry.proto.metrics.v1.SummaryDataPoint::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }

    public data class ValueAtQuantile(
        val quantile: Double = 0.0,
        val value: Double = 0.0,
        override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
    ) : pbandk.Message {
        override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile = protoMergeImpl(other)
        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile> get() = Companion.descriptor
        override val protoSize: Int by lazy { super.protoSize }
        public companion object : pbandk.Message.Companion<io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile> {
            public val defaultInstance: io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile by lazy { io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile() }
            override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile = io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile.decodeWithImpl(u)

            override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile> by lazy {
                val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile, *>>(2)
                fieldsList.apply {
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "quantile",
                            number = 1,
                            type = pbandk.FieldDescriptor.Type.Primitive.Double(),
                            jsonName = "quantile",
                            value = io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile::quantile
                        )
                    )
                    add(
                        pbandk.FieldDescriptor(
                            messageDescriptor = this@Companion::descriptor,
                            name = "value",
                            number = 2,
                            type = pbandk.FieldDescriptor.Type.Primitive.Double(),
                            jsonName = "value",
                            value = io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile::value
                        )
                    )
                }
                pbandk.MessageDescriptor(
                    fullName = "opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile",
                    messageClass = io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile::class,
                    messageCompanion = this,
                    fields = fieldsList
                )
            }
        }
    }
}

@pbandk.Export
public data class Exemplar(
    val filteredAttributes: List<io.opentelemetry.proto.common.v1.KeyValue> = emptyList(),
    val timeUnixNano: Long = 0L,
    val spanId: pbandk.ByteArr = pbandk.ByteArr.empty,
    val traceId: pbandk.ByteArr = pbandk.ByteArr.empty,
    val value: Value<*>? = null,
    override val unknownFields: Map<Int, pbandk.UnknownField> = emptyMap()
) : pbandk.Message {
    public sealed class Value<V>(value: V) : pbandk.Message.OneOf<V>(value) {
        public class AsDouble(asDouble: Double = 0.0) : Value<Double>(asDouble)
        public class AsInt(asInt: Long = 0L) : Value<Long>(asInt)
    }

    val asDouble: Double?
        get() = (value as? Value.AsDouble)?.value
    val asInt: Long?
        get() = (value as? Value.AsInt)?.value

    override operator fun plus(other: pbandk.Message?): io.opentelemetry.proto.metrics.v1.Exemplar = protoMergeImpl(other)
    override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.Exemplar> get() = Companion.descriptor
    override val protoSize: Int by lazy { super.protoSize }
    public companion object : pbandk.Message.Companion<io.opentelemetry.proto.metrics.v1.Exemplar> {
        public val defaultInstance: io.opentelemetry.proto.metrics.v1.Exemplar by lazy { io.opentelemetry.proto.metrics.v1.Exemplar() }
        override fun decodeWith(u: pbandk.MessageDecoder): io.opentelemetry.proto.metrics.v1.Exemplar = io.opentelemetry.proto.metrics.v1.Exemplar.decodeWithImpl(u)

        override val descriptor: pbandk.MessageDescriptor<io.opentelemetry.proto.metrics.v1.Exemplar> by lazy {
            val fieldsList = ArrayList<pbandk.FieldDescriptor<io.opentelemetry.proto.metrics.v1.Exemplar, *>>(6)
            fieldsList.apply {
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "time_unix_nano",
                        number = 2,
                        type = pbandk.FieldDescriptor.Type.Primitive.Fixed64(),
                        jsonName = "timeUnixNano",
                        value = io.opentelemetry.proto.metrics.v1.Exemplar::timeUnixNano
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "as_double",
                        number = 3,
                        type = pbandk.FieldDescriptor.Type.Primitive.Double(hasPresence = true),
                        oneofMember = true,
                        jsonName = "asDouble",
                        value = io.opentelemetry.proto.metrics.v1.Exemplar::asDouble
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "span_id",
                        number = 4,
                        type = pbandk.FieldDescriptor.Type.Primitive.Bytes(),
                        jsonName = "spanId",
                        value = io.opentelemetry.proto.metrics.v1.Exemplar::spanId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "trace_id",
                        number = 5,
                        type = pbandk.FieldDescriptor.Type.Primitive.Bytes(),
                        jsonName = "traceId",
                        value = io.opentelemetry.proto.metrics.v1.Exemplar::traceId
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "as_int",
                        number = 6,
                        type = pbandk.FieldDescriptor.Type.Primitive.SFixed64(hasPresence = true),
                        oneofMember = true,
                        jsonName = "asInt",
                        value = io.opentelemetry.proto.metrics.v1.Exemplar::asInt
                    )
                )
                add(
                    pbandk.FieldDescriptor(
                        messageDescriptor = this@Companion::descriptor,
                        name = "filtered_attributes",
                        number = 7,
                        type = pbandk.FieldDescriptor.Type.Repeated<io.opentelemetry.proto.common.v1.KeyValue>(valueType = pbandk.FieldDescriptor.Type.Message(messageCompanion = io.opentelemetry.proto.common.v1.KeyValue.Companion)),
                        jsonName = "filteredAttributes",
                        value = io.opentelemetry.proto.metrics.v1.Exemplar::filteredAttributes
                    )
                )
            }
            pbandk.MessageDescriptor(
                fullName = "opentelemetry.proto.metrics.v1.Exemplar",
                messageClass = io.opentelemetry.proto.metrics.v1.Exemplar::class,
                messageCompanion = this,
                fields = fieldsList
            )
        }
    }
}

@pbandk.Export
@pbandk.JsName("orDefaultForMetricsData")
public fun MetricsData?.orDefault(): io.opentelemetry.proto.metrics.v1.MetricsData = this ?: MetricsData.defaultInstance

private fun MetricsData.protoMergeImpl(plus: pbandk.Message?): MetricsData = (plus as? MetricsData)?.let {
    it.copy(
        resourceMetrics = resourceMetrics + plus.resourceMetrics,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun MetricsData.Companion.decodeWithImpl(u: pbandk.MessageDecoder): MetricsData {
    var resourceMetrics: pbandk.ListWithSize.Builder<io.opentelemetry.proto.metrics.v1.ResourceMetrics>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> resourceMetrics = (resourceMetrics ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.metrics.v1.ResourceMetrics> }
        }
    }

    return MetricsData(pbandk.ListWithSize.Builder.fixed(resourceMetrics), unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForResourceMetrics")
public fun ResourceMetrics?.orDefault(): io.opentelemetry.proto.metrics.v1.ResourceMetrics = this ?: ResourceMetrics.defaultInstance

private fun ResourceMetrics.protoMergeImpl(plus: pbandk.Message?): ResourceMetrics = (plus as? ResourceMetrics)?.let {
    it.copy(
        resource = resource?.plus(plus.resource) ?: plus.resource,
        scopeMetrics = scopeMetrics + plus.scopeMetrics,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ResourceMetrics.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ResourceMetrics {
    var resource: io.opentelemetry.proto.resource.v1.Resource? = null
    var scopeMetrics: pbandk.ListWithSize.Builder<io.opentelemetry.proto.metrics.v1.ScopeMetrics>? = null
    var schemaUrl = ""

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> resource = _fieldValue as io.opentelemetry.proto.resource.v1.Resource
            2 -> scopeMetrics = (scopeMetrics ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.metrics.v1.ScopeMetrics> }
            3 -> schemaUrl = _fieldValue as String
        }
    }

    return ResourceMetrics(resource, pbandk.ListWithSize.Builder.fixed(scopeMetrics), schemaUrl, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForScopeMetrics")
public fun ScopeMetrics?.orDefault(): io.opentelemetry.proto.metrics.v1.ScopeMetrics = this ?: ScopeMetrics.defaultInstance

private fun ScopeMetrics.protoMergeImpl(plus: pbandk.Message?): ScopeMetrics = (plus as? ScopeMetrics)?.let {
    it.copy(
        scope = scope?.plus(plus.scope) ?: plus.scope,
        metrics = metrics + plus.metrics,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ScopeMetrics.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ScopeMetrics {
    var scope: io.opentelemetry.proto.common.v1.InstrumentationScope? = null
    var metrics: pbandk.ListWithSize.Builder<io.opentelemetry.proto.metrics.v1.Metric>? = null
    var schemaUrl = ""

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> scope = _fieldValue as io.opentelemetry.proto.common.v1.InstrumentationScope
            2 -> metrics = (metrics ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.metrics.v1.Metric> }
            3 -> schemaUrl = _fieldValue as String
        }
    }

    return ScopeMetrics(scope, pbandk.ListWithSize.Builder.fixed(metrics), schemaUrl, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForMetric")
public fun Metric?.orDefault(): io.opentelemetry.proto.metrics.v1.Metric = this ?: Metric.defaultInstance

private fun Metric.protoMergeImpl(plus: pbandk.Message?): Metric = (plus as? Metric)?.let {
    it.copy(
        data = when {
            data is Metric.Data.Gauge && plus.data is Metric.Data.Gauge ->
                Metric.Data.Gauge(data.value + plus.data.value)
            data is Metric.Data.Sum && plus.data is Metric.Data.Sum ->
                Metric.Data.Sum(data.value + plus.data.value)
            data is Metric.Data.Histogram && plus.data is Metric.Data.Histogram ->
                Metric.Data.Histogram(data.value + plus.data.value)
            data is Metric.Data.ExponentialHistogram && plus.data is Metric.Data.ExponentialHistogram ->
                Metric.Data.ExponentialHistogram(data.value + plus.data.value)
            data is Metric.Data.Summary && plus.data is Metric.Data.Summary ->
                Metric.Data.Summary(data.value + plus.data.value)
            else ->
                plus.data ?: data
        },
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Metric.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Metric {
    var name = ""
    var description = ""
    var unit = ""
    var data: Metric.Data<*>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> name = _fieldValue as String
            2 -> description = _fieldValue as String
            3 -> unit = _fieldValue as String
            5 -> data = Metric.Data.Gauge(_fieldValue as io.opentelemetry.proto.metrics.v1.Gauge)
            7 -> data = Metric.Data.Sum(_fieldValue as io.opentelemetry.proto.metrics.v1.Sum)
            9 -> data = Metric.Data.Histogram(_fieldValue as io.opentelemetry.proto.metrics.v1.Histogram)
            10 -> data = Metric.Data.ExponentialHistogram(_fieldValue as io.opentelemetry.proto.metrics.v1.ExponentialHistogram)
            11 -> data = Metric.Data.Summary(_fieldValue as io.opentelemetry.proto.metrics.v1.Summary)
        }
    }

    return Metric(name, description, unit, data, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForGauge")
public fun Gauge?.orDefault(): io.opentelemetry.proto.metrics.v1.Gauge = this ?: Gauge.defaultInstance

private fun Gauge.protoMergeImpl(plus: pbandk.Message?): Gauge = (plus as? Gauge)?.let {
    it.copy(
        dataPoints = dataPoints + plus.dataPoints,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Gauge.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Gauge {
    var dataPoints: pbandk.ListWithSize.Builder<io.opentelemetry.proto.metrics.v1.NumberDataPoint>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> dataPoints = (dataPoints ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.metrics.v1.NumberDataPoint> }
        }
    }

    return Gauge(pbandk.ListWithSize.Builder.fixed(dataPoints), unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForSum")
public fun Sum?.orDefault(): io.opentelemetry.proto.metrics.v1.Sum = this ?: Sum.defaultInstance

private fun Sum.protoMergeImpl(plus: pbandk.Message?): Sum = (plus as? Sum)?.let {
    it.copy(
        dataPoints = dataPoints + plus.dataPoints,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Sum.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Sum {
    var dataPoints: pbandk.ListWithSize.Builder<io.opentelemetry.proto.metrics.v1.NumberDataPoint>? = null
    var aggregationTemporality: io.opentelemetry.proto.metrics.v1.AggregationTemporality = io.opentelemetry.proto.metrics.v1.AggregationTemporality.fromValue(0)
    var isMonotonic = false

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> dataPoints = (dataPoints ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.metrics.v1.NumberDataPoint> }
            2 -> aggregationTemporality = _fieldValue as io.opentelemetry.proto.metrics.v1.AggregationTemporality
            3 -> isMonotonic = _fieldValue as Boolean
        }
    }

    return Sum(pbandk.ListWithSize.Builder.fixed(dataPoints), aggregationTemporality, isMonotonic, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForHistogram")
public fun Histogram?.orDefault(): io.opentelemetry.proto.metrics.v1.Histogram = this ?: Histogram.defaultInstance

private fun Histogram.protoMergeImpl(plus: pbandk.Message?): Histogram = (plus as? Histogram)?.let {
    it.copy(
        dataPoints = dataPoints + plus.dataPoints,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Histogram.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Histogram {
    var dataPoints: pbandk.ListWithSize.Builder<io.opentelemetry.proto.metrics.v1.HistogramDataPoint>? = null
    var aggregationTemporality: io.opentelemetry.proto.metrics.v1.AggregationTemporality = io.opentelemetry.proto.metrics.v1.AggregationTemporality.fromValue(0)

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> dataPoints = (dataPoints ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.metrics.v1.HistogramDataPoint> }
            2 -> aggregationTemporality = _fieldValue as io.opentelemetry.proto.metrics.v1.AggregationTemporality
        }
    }

    return Histogram(pbandk.ListWithSize.Builder.fixed(dataPoints), aggregationTemporality, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForExponentialHistogram")
public fun ExponentialHistogram?.orDefault(): io.opentelemetry.proto.metrics.v1.ExponentialHistogram = this ?: ExponentialHistogram.defaultInstance

private fun ExponentialHistogram.protoMergeImpl(plus: pbandk.Message?): ExponentialHistogram = (plus as? ExponentialHistogram)?.let {
    it.copy(
        dataPoints = dataPoints + plus.dataPoints,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ExponentialHistogram.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ExponentialHistogram {
    var dataPoints: pbandk.ListWithSize.Builder<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint>? = null
    var aggregationTemporality: io.opentelemetry.proto.metrics.v1.AggregationTemporality = io.opentelemetry.proto.metrics.v1.AggregationTemporality.fromValue(0)

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> dataPoints = (dataPoints ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint> }
            2 -> aggregationTemporality = _fieldValue as io.opentelemetry.proto.metrics.v1.AggregationTemporality
        }
    }

    return ExponentialHistogram(pbandk.ListWithSize.Builder.fixed(dataPoints), aggregationTemporality, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForSummary")
public fun Summary?.orDefault(): io.opentelemetry.proto.metrics.v1.Summary = this ?: Summary.defaultInstance

private fun Summary.protoMergeImpl(plus: pbandk.Message?): Summary = (plus as? Summary)?.let {
    it.copy(
        dataPoints = dataPoints + plus.dataPoints,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Summary.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Summary {
    var dataPoints: pbandk.ListWithSize.Builder<io.opentelemetry.proto.metrics.v1.SummaryDataPoint>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> dataPoints = (dataPoints ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.metrics.v1.SummaryDataPoint> }
        }
    }

    return Summary(pbandk.ListWithSize.Builder.fixed(dataPoints), unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForNumberDataPoint")
public fun NumberDataPoint?.orDefault(): io.opentelemetry.proto.metrics.v1.NumberDataPoint = this ?: NumberDataPoint.defaultInstance

private fun NumberDataPoint.protoMergeImpl(plus: pbandk.Message?): NumberDataPoint = (plus as? NumberDataPoint)?.let {
    it.copy(
        attributes = attributes + plus.attributes,
        exemplars = exemplars + plus.exemplars,
        value = plus.value ?: value,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun NumberDataPoint.Companion.decodeWithImpl(u: pbandk.MessageDecoder): NumberDataPoint {
    var attributes: pbandk.ListWithSize.Builder<io.opentelemetry.proto.common.v1.KeyValue>? = null
    var startTimeUnixNano = 0L
    var timeUnixNano = 0L
    var exemplars: pbandk.ListWithSize.Builder<io.opentelemetry.proto.metrics.v1.Exemplar>? = null
    var flags = 0
    var value: NumberDataPoint.Value<*>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            2 -> startTimeUnixNano = _fieldValue as Long
            3 -> timeUnixNano = _fieldValue as Long
            4 -> value = NumberDataPoint.Value.AsDouble(_fieldValue as Double)
            5 -> exemplars = (exemplars ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.metrics.v1.Exemplar> }
            6 -> value = NumberDataPoint.Value.AsInt(_fieldValue as Long)
            7 -> attributes = (attributes ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.common.v1.KeyValue> }
            8 -> flags = _fieldValue as Int
        }
    }

    return NumberDataPoint(pbandk.ListWithSize.Builder.fixed(attributes), startTimeUnixNano, timeUnixNano, pbandk.ListWithSize.Builder.fixed(exemplars),
        flags, value, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForHistogramDataPoint")
public fun HistogramDataPoint?.orDefault(): io.opentelemetry.proto.metrics.v1.HistogramDataPoint = this ?: HistogramDataPoint.defaultInstance

private fun HistogramDataPoint.protoMergeImpl(plus: pbandk.Message?): HistogramDataPoint = (plus as? HistogramDataPoint)?.let {
    it.copy(
        attributes = attributes + plus.attributes,
        bucketCounts = bucketCounts + plus.bucketCounts,
        explicitBounds = explicitBounds + plus.explicitBounds,
        exemplars = exemplars + plus.exemplars,
        sum = plus.sum ?: sum,
        min = plus.min ?: min,
        max = plus.max ?: max,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun HistogramDataPoint.Companion.decodeWithImpl(u: pbandk.MessageDecoder): HistogramDataPoint {
    var attributes: pbandk.ListWithSize.Builder<io.opentelemetry.proto.common.v1.KeyValue>? = null
    var startTimeUnixNano = 0L
    var timeUnixNano = 0L
    var count = 0L
    var bucketCounts: pbandk.ListWithSize.Builder<Long>? = null
    var explicitBounds: pbandk.ListWithSize.Builder<Double>? = null
    var exemplars: pbandk.ListWithSize.Builder<io.opentelemetry.proto.metrics.v1.Exemplar>? = null
    var flags = 0
    var sum: Double? = null
    var min: Double? = null
    var max: Double? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            2 -> startTimeUnixNano = _fieldValue as Long
            3 -> timeUnixNano = _fieldValue as Long
            4 -> count = _fieldValue as Long
            5 -> sum = _fieldValue as Double
            6 -> bucketCounts = (bucketCounts ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<Long> }
            7 -> explicitBounds = (explicitBounds ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<Double> }
            8 -> exemplars = (exemplars ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.metrics.v1.Exemplar> }
            9 -> attributes = (attributes ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.common.v1.KeyValue> }
            10 -> flags = _fieldValue as Int
            11 -> min = _fieldValue as Double
            12 -> max = _fieldValue as Double
        }
    }

    return HistogramDataPoint(pbandk.ListWithSize.Builder.fixed(attributes), startTimeUnixNano, timeUnixNano, count,
        pbandk.ListWithSize.Builder.fixed(bucketCounts), pbandk.ListWithSize.Builder.fixed(explicitBounds), pbandk.ListWithSize.Builder.fixed(exemplars), flags,
        sum, min, max, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForExponentialHistogramDataPoint")
public fun ExponentialHistogramDataPoint?.orDefault(): io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint = this ?: ExponentialHistogramDataPoint.defaultInstance

private fun ExponentialHistogramDataPoint.protoMergeImpl(plus: pbandk.Message?): ExponentialHistogramDataPoint = (plus as? ExponentialHistogramDataPoint)?.let {
    it.copy(
        attributes = attributes + plus.attributes,
        positive = positive?.plus(plus.positive) ?: plus.positive,
        negative = negative?.plus(plus.negative) ?: plus.negative,
        exemplars = exemplars + plus.exemplars,
        sum = plus.sum ?: sum,
        min = plus.min ?: min,
        max = plus.max ?: max,
        zeroThreshold = plus.zeroThreshold ?: zeroThreshold,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ExponentialHistogramDataPoint.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ExponentialHistogramDataPoint {
    var attributes: pbandk.ListWithSize.Builder<io.opentelemetry.proto.common.v1.KeyValue>? = null
    var startTimeUnixNano = 0L
    var timeUnixNano = 0L
    var count = 0L
    var scale = 0
    var zeroCount = 0L
    var positive: io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets? = null
    var negative: io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets? = null
    var flags = 0
    var exemplars: pbandk.ListWithSize.Builder<io.opentelemetry.proto.metrics.v1.Exemplar>? = null
    var sum: Double? = null
    var min: Double? = null
    var max: Double? = null
    var zeroThreshold: Double? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> attributes = (attributes ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.common.v1.KeyValue> }
            2 -> startTimeUnixNano = _fieldValue as Long
            3 -> timeUnixNano = _fieldValue as Long
            4 -> count = _fieldValue as Long
            5 -> sum = _fieldValue as Double
            6 -> scale = _fieldValue as Int
            7 -> zeroCount = _fieldValue as Long
            8 -> positive = _fieldValue as io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets
            9 -> negative = _fieldValue as io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets
            10 -> flags = _fieldValue as Int
            11 -> exemplars = (exemplars ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.metrics.v1.Exemplar> }
            12 -> min = _fieldValue as Double
            13 -> max = _fieldValue as Double
            14 -> zeroThreshold = _fieldValue as Double
        }
    }

    return ExponentialHistogramDataPoint(pbandk.ListWithSize.Builder.fixed(attributes), startTimeUnixNano, timeUnixNano, count,
        scale, zeroCount, positive, negative,
        flags, pbandk.ListWithSize.Builder.fixed(exemplars), sum, min,
        max, zeroThreshold, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForExponentialHistogramDataPointBuckets")
public fun ExponentialHistogramDataPoint.Buckets?.orDefault(): io.opentelemetry.proto.metrics.v1.ExponentialHistogramDataPoint.Buckets = this ?: ExponentialHistogramDataPoint.Buckets.defaultInstance

private fun ExponentialHistogramDataPoint.Buckets.protoMergeImpl(plus: pbandk.Message?): ExponentialHistogramDataPoint.Buckets = (plus as? ExponentialHistogramDataPoint.Buckets)?.let {
    it.copy(
        bucketCounts = bucketCounts + plus.bucketCounts,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun ExponentialHistogramDataPoint.Buckets.Companion.decodeWithImpl(u: pbandk.MessageDecoder): ExponentialHistogramDataPoint.Buckets {
    var offset = 0
    var bucketCounts: pbandk.ListWithSize.Builder<Long>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> offset = _fieldValue as Int
            2 -> bucketCounts = (bucketCounts ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<Long> }
        }
    }

    return ExponentialHistogramDataPoint.Buckets(offset, pbandk.ListWithSize.Builder.fixed(bucketCounts), unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForSummaryDataPoint")
public fun SummaryDataPoint?.orDefault(): io.opentelemetry.proto.metrics.v1.SummaryDataPoint = this ?: SummaryDataPoint.defaultInstance

private fun SummaryDataPoint.protoMergeImpl(plus: pbandk.Message?): SummaryDataPoint = (plus as? SummaryDataPoint)?.let {
    it.copy(
        attributes = attributes + plus.attributes,
        quantileValues = quantileValues + plus.quantileValues,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun SummaryDataPoint.Companion.decodeWithImpl(u: pbandk.MessageDecoder): SummaryDataPoint {
    var attributes: pbandk.ListWithSize.Builder<io.opentelemetry.proto.common.v1.KeyValue>? = null
    var startTimeUnixNano = 0L
    var timeUnixNano = 0L
    var count = 0L
    var sum = 0.0
    var quantileValues: pbandk.ListWithSize.Builder<io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile>? = null
    var flags = 0

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            2 -> startTimeUnixNano = _fieldValue as Long
            3 -> timeUnixNano = _fieldValue as Long
            4 -> count = _fieldValue as Long
            5 -> sum = _fieldValue as Double
            6 -> quantileValues = (quantileValues ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile> }
            7 -> attributes = (attributes ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.common.v1.KeyValue> }
            8 -> flags = _fieldValue as Int
        }
    }

    return SummaryDataPoint(pbandk.ListWithSize.Builder.fixed(attributes), startTimeUnixNano, timeUnixNano, count,
        sum, pbandk.ListWithSize.Builder.fixed(quantileValues), flags, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForSummaryDataPointValueAtQuantile")
public fun SummaryDataPoint.ValueAtQuantile?.orDefault(): io.opentelemetry.proto.metrics.v1.SummaryDataPoint.ValueAtQuantile = this ?: SummaryDataPoint.ValueAtQuantile.defaultInstance

private fun SummaryDataPoint.ValueAtQuantile.protoMergeImpl(plus: pbandk.Message?): SummaryDataPoint.ValueAtQuantile = (plus as? SummaryDataPoint.ValueAtQuantile)?.let {
    it.copy(
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun SummaryDataPoint.ValueAtQuantile.Companion.decodeWithImpl(u: pbandk.MessageDecoder): SummaryDataPoint.ValueAtQuantile {
    var quantile = 0.0
    var value = 0.0

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            1 -> quantile = _fieldValue as Double
            2 -> value = _fieldValue as Double
        }
    }

    return SummaryDataPoint.ValueAtQuantile(quantile, value, unknownFields)
}

@pbandk.Export
@pbandk.JsName("orDefaultForExemplar")
public fun Exemplar?.orDefault(): io.opentelemetry.proto.metrics.v1.Exemplar = this ?: Exemplar.defaultInstance

private fun Exemplar.protoMergeImpl(plus: pbandk.Message?): Exemplar = (plus as? Exemplar)?.let {
    it.copy(
        filteredAttributes = filteredAttributes + plus.filteredAttributes,
        value = plus.value ?: value,
        unknownFields = unknownFields + plus.unknownFields
    )
} ?: this

@Suppress("UNCHECKED_CAST")
private fun Exemplar.Companion.decodeWithImpl(u: pbandk.MessageDecoder): Exemplar {
    var filteredAttributes: pbandk.ListWithSize.Builder<io.opentelemetry.proto.common.v1.KeyValue>? = null
    var timeUnixNano = 0L
    var spanId: pbandk.ByteArr = pbandk.ByteArr.empty
    var traceId: pbandk.ByteArr = pbandk.ByteArr.empty
    var value: Exemplar.Value<*>? = null

    val unknownFields = u.readMessage(this) { _fieldNumber, _fieldValue ->
        when (_fieldNumber) {
            2 -> timeUnixNano = _fieldValue as Long
            3 -> value = Exemplar.Value.AsDouble(_fieldValue as Double)
            4 -> spanId = _fieldValue as pbandk.ByteArr
            5 -> traceId = _fieldValue as pbandk.ByteArr
            6 -> value = Exemplar.Value.AsInt(_fieldValue as Long)
            7 -> filteredAttributes = (filteredAttributes ?: pbandk.ListWithSize.Builder()).apply { this += _fieldValue as kotlin.sequences.Sequence<io.opentelemetry.proto.common.v1.KeyValue> }
        }
    }

    return Exemplar(pbandk.ListWithSize.Builder.fixed(filteredAttributes), timeUnixNano, spanId, traceId,
        value, unknownFields)
}
