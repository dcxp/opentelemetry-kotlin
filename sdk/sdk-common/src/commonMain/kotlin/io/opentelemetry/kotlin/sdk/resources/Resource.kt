/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.resources

import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.common.Attributes
import io.opentelemetry.kotlin.semconv.resource.attributes.ResourceAttributes.SERVICE_NAME
import io.opentelemetry.kotlin.semconv.resource.attributes.ResourceAttributes.TELEMETRY_SDK_LANGUAGE
import io.opentelemetry.kotlin.semconv.resource.attributes.ResourceAttributes.TELEMETRY_SDK_NAME
import io.opentelemetry.kotlin.semconv.resource.attributes.ResourceAttributes.TELEMETRY_SDK_VERSION

/**
 * [Resource] represents a resource, which capture identifying information about the entities for
 * which signals (stats or traces) are reported.
 */
abstract class Resource internal constructor() {
    /**
     * Returns the URL of the OpenTelemetry schema used by this resource. May be null.
     *
     * @return An OpenTelemetry schema URL.
     * @since 1.4.0
     */
    abstract val schemaUrl: String?

    /**
     * Returns a map of attributes that describe the resource.
     *
     * @return a map of attributes.
     */
    abstract val attributes: Attributes

    /**
     * Returns the value for a given resource attribute key.
     *
     * @return the value of the attribute with the given key
     */
    fun <T : Any> getAttribute(key: AttributeKey<T>): T? {
        return attributes[key]
    }

    /**
     * Returns a new, merged [Resource] by merging the current `Resource` with the `other`
     * `Resource`. In case of a collision, the "other" `Resource` takes precedence.
     *
     * @param other the `Resource` that will be merged with `this`.
     * @return the newly merged `Resource`.
     */
    fun merge(other: Resource): Resource {
        val attrBuilder = Attributes.builder()
        attrBuilder.putAll(attributes)
        attrBuilder.putAll(other.attributes)
        if (other.schemaUrl == null) {
            return create(attrBuilder.build(), schemaUrl)
        }
        if (schemaUrl == null) {
            return create(attrBuilder.build(), other.schemaUrl)
        }
        if (other.schemaUrl != schemaUrl) {
            /*logger.info(
                "Attempting to merge Resources with different schemaUrls. "
                        + "The resulting Resource will have no schemaUrl assigned. Schema 1: "
                        + schemaUrl
                        + " Schema 2: "
                        + other.schemaUrl
            )*/
            // currently, behavior is undefined if schema URLs don't match. In the future, we may
            // apply schema transformations if possible.
            return create(attrBuilder.build(), null)
        }
        return create(attrBuilder.build(), schemaUrl)
    }

    /**
     * Returns a new [ResourceBuilder] instance populated with the data of this [ ].
     *
     * @since 1.1.0
     */
    fun toBuilder(): io.opentelemetry.kotlin.sdk.resources.ResourceBuilder {
        return builder().putAll(this)
    }

    companion object {
        private const val MAX_LENGTH = 255
        private const val ERROR_MESSAGE_INVALID_CHARS =
            (" should be a ASCII string with a length greater than 0 and not exceed " +
                MAX_LENGTH +
                " characters.")
        private const val ERROR_MESSAGE_INVALID_VALUE =
            " should be a ASCII string with a length not exceed " + MAX_LENGTH + " characters."
        private val EMPTY = create(Attributes.empty())
        private val TELEMETRY_SDK: Resource =
            create(
                Attributes.builder()
                    .put(TELEMETRY_SDK_NAME, "opentelemetry")
                    .put(TELEMETRY_SDK_LANGUAGE, "java")
                    .put(TELEMETRY_SDK_VERSION, readVersion())
                    .build()
            )

        /**
         * The MANDATORY Resource instance contains the mandatory attributes that must be used if
         * they are not provided by the Resource that is given to an SDK signal provider.
         */
        private val MANDATORY: Resource =
            create(Attributes.of(SERVICE_NAME, "unknown_service:java"))

        /**
         * Returns the default [Resource]. This resource contains the default attributes provided by
         * the SDK.
         *
         * @return a `Resource`.
         */
        val default: Resource = MANDATORY.merge(TELEMETRY_SDK)

        /**
         * Returns an empty [Resource]. When creating a [Resource], it is strongly recommended to
         * start with [Resource.getDefault] instead of this method to include SDK required
         * attributes.
         *
         * @return an empty `Resource`.
         */
        fun empty(): Resource {
            return EMPTY
        }
        /**
         * Returns a [Resource].
         *
         * @param attributes a map of [Attributes] that describe the resource.
         * @param schemaUrl The URL of the OpenTelemetry schema used to create this Resource.
         * @return a `Resource`.
         * @throws NullPointerException if `attributes` is null.
         * @throws IllegalArgumentException if attribute key or attribute value is not a valid
         * printable ASCII string or exceed [.MAX_LENGTH] characters.
         */
        /**
         * Returns a [Resource].
         *
         * @param attributes a map of attributes that describe the resource.
         * @return a `Resource`.
         * @throws NullPointerException if `attributes` is null.
         * @throws IllegalArgumentException if attribute key or attribute value is not a valid
         * printable ASCII string or exceed [.MAX_LENGTH] characters.
         */
        fun create(attributes: Attributes, schemaUrl: String? = null): Resource {
            return Instance(schemaUrl, attributes)
        }

        private class Instance(
            override val schemaUrl: String?,
            override val attributes: Attributes
        ) : Resource() {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || this::class != other::class) return false

                other as Instance

                if (schemaUrl != other.schemaUrl) return false
                if (attributes != other.attributes) return false

                return true
            }

            override fun hashCode(): Int {
                var result = schemaUrl?.hashCode() ?: 0
                result = 31 * result + attributes.hashCode()
                return result
            }
        }

        private fun readVersion(): String {
            /*val properties = Properties()
            try {
                properties.load(
                    Resource::class.java.getResourceAsStream(
                        "/io/opentelemetry/sdk/common/version.properties"
                    )
                )
            } catch (e: java.lang.Exception) {
                // we left the attribute empty
                return "unknown"
            }
            properties.getProperty("sdk.version", "unknown")*/
            return "unknown"
        }
        /*
        private fun checkAttributes(attributes: Attributes) {
            attributes.forEach{ key, value ->
                Utils.checkArgument(
                    isValidAndNotEmpty(key), "Attribute key" + ERROR_MESSAGE_INVALID_CHARS
                )
                Objects.requireNonNull(value, "Attribute value" + ERROR_MESSAGE_INVALID_VALUE)
            }
        }*/

        /**
         * Determines whether the given `String` is a valid printable ASCII string with a length not
         * exceed [.MAX_LENGTH] characters.
         *
         * @param name the name to be validated.
         * @return whether the name is valid.
         */
        private fun isValid(name: String): Boolean {
            return name.length <= MAX_LENGTH
        }

        /**
         * Determines whether the given `String` is a valid printable ASCII string with a length
         * greater than 0 and not exceed [.MAX_LENGTH] characters.
         *
         * @param name the name to be validated.
         * @return whether the name is valid.
         */
        private fun isValidAndNotEmpty(name: AttributeKey<*>): Boolean {
            return name.key.isNotEmpty() && isValid(name.key)
        }

        /**
         * Returns a new [ResourceBuilder] instance for creating arbitrary [Resource].
         *
         * @since 1.1.0
         */
        fun builder(): ResourceBuilder {
            return ResourceBuilder()
        }
    }
}
