/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.kotlin.sdk.trace.samplers

/** A builder for creating ParentBased sampler instances. */
class ParentBasedSamplerBuilder internal constructor(private val root: Sampler) {
    private var remoteParentSampled: Sampler? = null
    private var remoteParentNotSampled: Sampler? = null
    private var localParentSampled: Sampler? = null
    private var localParentNotSampled: Sampler? = null

    /**
     * Sets the [Sampler] to use when there is a remote parent that was sampled. If not set,
     * defaults to always sampling if the remote parent was sampled.
     *
     * @return this Builder
     */
    fun setRemoteParentSampled(remoteParentSampled: Sampler?): ParentBasedSamplerBuilder {
        this.remoteParentSampled = remoteParentSampled
        return this
    }

    /**
     * Sets the [Sampler] to use when there is a remote parent that was not sampled. If not set,
     * defaults to never sampling when the remote parent isn't sampled.
     *
     * @return this Builder
     */
    fun setRemoteParentNotSampled(remoteParentNotSampled: Sampler?): ParentBasedSamplerBuilder {
        this.remoteParentNotSampled = remoteParentNotSampled
        return this
    }

    /**
     * Sets the [Sampler] to use when there is a local parent that was sampled. If not set, defaults
     * to always sampling if the local parent was sampled.
     *
     * @return this Builder
     */
    fun setLocalParentSampled(localParentSampled: Sampler?): ParentBasedSamplerBuilder {
        this.localParentSampled = localParentSampled
        return this
    }

    /**
     * Sets the [Sampler] to use when there is a local parent that was not sampled. If not set,
     * defaults to never sampling when the local parent isn't sampled.
     *
     * @return this Builder
     */
    fun setLocalParentNotSampled(localParentNotSampled: Sampler?): ParentBasedSamplerBuilder {
        this.localParentNotSampled = localParentNotSampled
        return this
    }

    /**
     * Builds the [ParentBasedSampler].
     *
     * @return the ParentBased sampler.
     */
    fun build(): Sampler {
        return ParentBasedSampler(
            root,
            remoteParentSampled,
            remoteParentNotSampled,
            localParentSampled,
            localParentNotSampled
        )
    }
}
