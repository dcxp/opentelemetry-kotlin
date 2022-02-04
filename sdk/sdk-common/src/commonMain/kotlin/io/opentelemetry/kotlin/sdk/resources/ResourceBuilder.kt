/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.resources

import io.opentelemetry.kotlin.api.common.AttributeKey
import io.opentelemetry.kotlin.api.common.Attributes

/**
 * A builder of [Resource] that allows to add key-value pairs and copy attributes from other
 * [Attributes] or [Resource].
 *
 * @since 1.1.0
 */
class ResourceBuilder {
    private val attributesBuilder = Attributes.builder()

    private var schemaUrl: String? = null

    /**
     * Puts a String attribute into this.
     *
     * Note: It is strongly recommended to use [.put], and pre-allocate your keys, if possible.
     *
     * @return this Builder
     */
    fun put(key: String, value: String): ResourceBuilder {
        attributesBuilder.put(key, value)
        return this
    }

    /**
     * Puts a long attribute into this.
     *
     * Note: It is strongly recommended to use [.put], and pre-allocate your keys, if possible.
     *
     * @return this Builder
     */
    fun put(key: String, value: Long): ResourceBuilder {
        attributesBuilder.put(key, value)
        return this
    }

    /**
     * Puts a double attribute into this.
     *
     * Note: It is strongly recommended to use [.put], and pre-allocate your keys, if possible.
     *
     * @return this Builder
     */
    fun put(key: String, value: Double): ResourceBuilder {
        attributesBuilder.put(key, value)
        return this
    }

    /**
     * Puts a boolean attribute into this.
     *
     * Note: It is strongly recommended to use [.put], and pre-allocate your keys, if possible.
     *
     * @return this Builder
     */
    fun put(key: String, value: Boolean): ResourceBuilder {
        attributesBuilder.put(key, value)
        return this
    }

    /**
     * Puts a String array attribute into this.
     *
     * Note: It is strongly recommended to use [.put], and pre-allocate your keys, if possible.
     *
     * @return this Builder
     */
    fun put(key: String, vararg values: String): ResourceBuilder {
        attributesBuilder.put(key, *values)
        return this
    }

    /**
     * Puts a Long array attribute into this.
     *
     * Note: It is strongly recommended to use [.put], and pre-allocate your keys, if possible.
     *
     * @return this Builder
     */
    fun put(key: String, vararg values: Long): ResourceBuilder {
        attributesBuilder.put(key, *values)
        return this
    }

    /**
     * Puts a Double array attribute into this.
     *
     * Note: It is strongly recommended to use [.put], and pre-allocate your keys, if possible.
     *
     * @return this Builder
     */
    fun put(key: String, vararg values: Double): ResourceBuilder {
        attributesBuilder.put(key, *values)
        return this
    }

    /**
     * Puts a Boolean array attribute into this.
     *
     * Note: It is strongly recommended to use [.put], and pre-allocate your keys, if possible.
     *
     * @return this Builder
     */
    fun put(key: String, vararg values: Boolean): ResourceBuilder {
        attributesBuilder.put(key, *values)
        return this
    }

    /** Puts a [AttributeKey] with associated value into this. */
    fun <T : Any> put(key: AttributeKey<T>, value: T): ResourceBuilder {
        if (key.key.isNotEmpty()) {
            attributesBuilder.put(key, value)
        }
        return this
    }

    /** Puts a [AttributeKey] with associated value into this. */
    fun put(key: AttributeKey<Long>, value: Int): ResourceBuilder {
        if (key.key.isNotEmpty()) {
            attributesBuilder.put(key, value.toLong())
        }
        return this
    }

    /** Puts all [Attributes] into this. */
    fun putAll(attributes: Attributes): ResourceBuilder {
        attributesBuilder.putAll(attributes)
        return this
    }

    /** Puts all attributes from [Resource] into this. */
    fun putAll(resource: Resource): ResourceBuilder {
        attributesBuilder.putAll(resource.attributes)
        return this
    }

    /**
     * Assign an OpenTelemetry schema URL to the resulting Resource.
     *
     * @param schemaUrl The URL of the OpenTelemetry schema being used to create this Resource.
     * @return this
     * @since 1.4.0
     */
    fun setSchemaUrl(schemaUrl: String?): ResourceBuilder {
        this.schemaUrl = schemaUrl
        return this
    }

    /** Create the [Resource] from this. */
    fun build(): Resource {
        return Resource.create(attributesBuilder.build(), schemaUrl)
    }
}
