/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
// Includes work from:
/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright 2020 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.opentelemetry.context

// Lazy-loaded storage. Delaying storage initialization until after class initialization makes it
// much easier to avoid circular loading since there can still be references to Context as long as
// they don't depend on storage, like key() and currentContextExecutor(). It also makes it easier
// to handle exceptions.
internal object LazyStorage {
    // Used by auto-instrumentation agent. Check with auto-instrumentation before making changes to
    // this method.
    //
    // Ideally auto-instrumentation would hijack the public ContextStorage.get() instead of this
    // method, but auto-instrumentation also needs to inject its own implementation of
    // ContextStorage
    // into the class loader at the same time, which causes a problem because injecting a class into
    // the class loader automatically resolves its super classes (interfaces), which in this case is
    // ContextStorage, which would be the same class (interface) being instrumented at that time,
    // which would lead to the JVM throwing a LinkageError "attempted duplicate interface
    // definition"
    fun get(): io.opentelemetry.context.ContextStorage {
        return storage
    }

    private val storage: ContextStorage

    init {
        storage = createStorage()
    }

    fun createStorage(): ContextStorage {
        return ContextStorage.defaultStorage()
    }
}
