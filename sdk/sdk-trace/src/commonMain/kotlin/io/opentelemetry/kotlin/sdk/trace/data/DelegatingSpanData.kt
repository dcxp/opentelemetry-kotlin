/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.data

/**
 * A [SpanData] which delegates all methods to another [SpanData]. Extend this class to modify the
 * [SpanData] that will be exported, for example by creating a delegating [ ] which wraps [SpanData]
 * with a custom implementation.
 *
 * <pre>`// class SpanDataWithClientType extends DelegatingSpanData { // // private final Attributes
 * attributes; // // SpanDataWithClientType(SpanData delegate) { // super(delegate); // String
 * clientType = ClientConfig.parseUserAgent( //
 * delegate.getAttributes().get(SemanticAttributes.HTTP_USER_AGENT).getStringValue()); //
 * Attributes.Builder newAttributes = Attributes.builder(delegate.getAttributes()); //
 * newAttributes.setAttribute("client_type", clientType); // attributes = newAttributes.build(); //
 * } // // @Override // public Attributes getAttributes() { // return attributes; // } // } `</pre>
 * *
 */
abstract class DelegatingSpanData protected constructor(private val delegate: SpanData) :
    SpanData by delegate {

    override fun equals(o: Any?): Boolean {
        if (o === this) {
            return true
        }
        if (o is SpanData) {
            val that: SpanData = o
            return (spanContext == that.spanContext &&
                parentSpanContext == that.parentSpanContext &&
                resource.equals(that.resource) &&
                name == that.name &&
                kind == that.kind &&
                startEpochNanos == that.startEpochNanos &&
                attributes == that.attributes &&
                events == that.events &&
                links == that.links &&
                status == that.status &&
                endEpochNanos == that.endEpochNanos &&
                hasEnded() == that.hasEnded() &&
                totalRecordedEvents == that.totalRecordedEvents &&
                totalRecordedLinks == that.totalRecordedLinks &&
                totalAttributeCount == that.totalAttributeCount)
        }
        return false
    }

    override fun hashCode(): Int {
        var code = 1
        code *= 1000003
        code = code xor spanContext.hashCode()
        code *= 1000003
        code = code xor parentSpanContext.hashCode()
        code *= 1000003
        code = code xor resource.hashCode()
        code *= 1000003
        code = code xor name.hashCode()
        code *= 1000003
        code = code xor kind.hashCode()
        code *= 1000003
        code = code xor (startEpochNanos ushr 32 xor startEpochNanos).toInt()
        code *= 1000003
        code = code xor attributes.hashCode()
        code *= 1000003
        code = code xor events.hashCode()
        code *= 1000003
        code = code xor links.hashCode()
        code *= 1000003
        code = code xor status.hashCode()
        code *= 1000003
        code = code xor (endEpochNanos ushr 32 xor endEpochNanos).toInt()
        code *= 1000003
        code = code xor if (hasEnded()) 1231 else 1237
        code *= 1000003
        code = code xor totalRecordedEvents
        code *= 1000003
        code = code xor totalRecordedLinks
        code *= 1000003
        code = code xor totalAttributeCount
        return code
    }

    override fun toString(): String {
        return ("DelegatingSpanData{" +
            "spanContext=" +
            spanContext +
            ", " +
            "parentSpanContext=" +
            parentSpanContext +
            ", " +
            "resource=" +
            resource +
            ", " +
            "name=" +
            name +
            ", " +
            "kind=" +
            kind +
            ", " +
            "startEpochNanos=" +
            startEpochNanos +
            ", " +
            "attributes=" +
            attributes +
            ", " +
            "events=" +
            events +
            ", " +
            "links=" +
            links +
            ", " +
            "status=" +
            status +
            ", " +
            "endEpochNanos=" +
            endEpochNanos +
            ", " +
            "hasEnded=" +
            hasEnded() +
            ", " +
            "totalRecordedEvents=" +
            totalRecordedEvents +
            ", " +
            "totalRecordedLinks=" +
            totalRecordedLinks +
            ", " +
            "totalAttributeCount=" +
            totalAttributeCount +
            "}")
    }
}
