package io.opentelemetry.kotlin.sdk.metrics.mock

import io.opentelemetry.kotlin.sdk.common.CompletableResultCode
import io.opentelemetry.kotlin.sdk.metrics.data.AggregationTemporality
import io.opentelemetry.kotlin.sdk.metrics.export.MetricReader

class MetricReaderMock : MetricReader {
    override val supportedTemporality: Set<AggregationTemporality>
        get() = AggregationTemporality.values().toSet()
    override val preferredTemporality: AggregationTemporality?
        get() = null

    override fun flush(): CompletableResultCode {
        return CompletableResultCode.ofSuccess()
    }

    override fun shutdown(): CompletableResultCode {
        return CompletableResultCode.ofSuccess()
    }
}
