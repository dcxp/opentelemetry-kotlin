/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package io.opentelemetry.context
/*
import org.assertj.core.api.Assertions.assertThat


internal class ContextTest {
    @RegisterExtension
    var logs: LogCapturer = LogCapturer.create().captureForType(ThreadLocalContextStorage::class.java, Level.DEBUG)

    // Make sure all tests clean up
    @AfterEach
    fun tearDown() {
        assertThat(Context.current()).isEqualTo(Context.root())
    }

    @Test
    fun startsWithRoot() {
        assertThat(Context.current()).isEqualTo(Context.root())
    }

    @Test
    fun canBeAttached() {
        val context = Context.current().with(ANIMAL, "cat")
        assertThat(Context.current()[ANIMAL]).isNull()
        context.makeCurrent().use { ignored ->
            assertThat(Context.current()[ANIMAL]).isEqualTo("cat")
            Context.root().makeCurrent().use { ignored2 -> assertThat(Context.current()[ANIMAL]).isNull() }
            assertThat(Context.current()[ANIMAL]).isEqualTo("cat")
        }
        assertThat(Context.current()[ANIMAL]).isNull()
    }

    @Test
    fun attachSameTwice() {
        val context = Context.current().with(ANIMAL, "cat")
        assertThat(Context.current()[ANIMAL]).isNull()
        context.makeCurrent().use { ignored ->
            assertThat(Context.current()[ANIMAL]).isEqualTo("cat")
            context.makeCurrent().use { ignored2 -> assertThat(Context.current()[ANIMAL]).isEqualTo("cat") }
            assertThat(Context.current()[ANIMAL]).isEqualTo("cat")
        }
        assertThat(Context.current()[ANIMAL]).isNull()
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun newThreadStartsWithRoot() {
        val context = Context.current().with(ANIMAL, "cat")
        context.makeCurrent().use { ignored ->
            assertThat(Context.current()[ANIMAL]).isEqualTo("cat")
            val current: java.util.concurrent.atomic.AtomicReference<Context> =
                java.util.concurrent.atomic.AtomicReference<Context>()
            val thread: java.lang.Thread = java.lang.Thread(Runnable { current.set(Context.current()) })
            thread.start()
            thread.join()
            assertThat(current.get()).isEqualTo(Context.root())
        }
    }

    @Test
    fun closingScopeWhenNotActiveIsLogged() {
        val initial = Context.current()
        val context = initial.with(ANIMAL, "cat")
        context.makeCurrent().use { scope ->
            val context2 = context.with(ANIMAL, "dog")
            context2.makeCurrent().use { ignored ->
                assertThat(Context.current()[ANIMAL]).isEqualTo("dog")
                scope.close()
            }
        }
        assertThat(Context.current()).isEqualTo(initial)
        val log: LoggingEvent = logs.assertContains("Context in storage not the expected context")
        assertThat(log.getLevel()).isEqualTo(Level.DEBUG)
    }

    @Test
    fun withValues() {
        val context1 = Context.current().with(ANIMAL, "cat")
        assertThat(context1[ANIMAL]).isEqualTo("cat")
        val context2 = context1.with(BAG, 100)
        // Old unaffected
        assertThat(context1[ANIMAL]).isEqualTo("cat")
        assertThat(context1[BAG]).isNull()
        assertThat(context2[ANIMAL]).isEqualTo("cat")
        assertThat(context2[BAG]).isEqualTo(100)
        val context3 = context2.with(ANIMAL, "dog")
        // Old unaffected
        assertThat(context2[ANIMAL]).isEqualTo("cat")
        assertThat(context2[BAG]).isEqualTo(100)
        assertThat(context3[ANIMAL]).isEqualTo("dog")
        assertThat(context3[BAG]).isEqualTo(100)
        val context4 = context3.with(BAG, null)
        // Old unaffected
        assertThat(context3[ANIMAL]).isEqualTo("dog")
        assertThat(context3[BAG]).isEqualTo(100)
        assertThat(context4[ANIMAL]).isEqualTo("dog")
        assertThat(context4[BAG]).isNull()
        val context5 = context4.with(ANIMAL, "dog")
        assertThat(context5[ANIMAL]).isEqualTo("dog")
        assertThat(context5).isSameAs(context4)
        val dog = String("dog")
        assertThat(dog).isEqualTo("dog")
        assertThat(dog).isNotSameAs("dog")
        val context6 = context5.with(ANIMAL, dog)
        assertThat(context6[ANIMAL]).isEqualTo("dog")
        // We reuse context object when values match by reference, not value.
        assertThat(context6).isNotSameAs(context5)
    }

    @Test
    fun wrapRunnable() {
        val value: java.util.concurrent.atomic.AtomicReference<String> =
            java.util.concurrent.atomic.AtomicReference<String>()
        val callback = Runnable { value.set(Context.current()[ANIMAL]) }
        callback.run()
        assertThat(value).hasValue(null)
        CAT.wrap(callback).run()
        assertThat(value).hasValue("cat")
        callback.run()
        assertThat(value).hasValue(null)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun wrapCallable() {
        val value: java.util.concurrent.atomic.AtomicReference<String> =
            java.util.concurrent.atomic.AtomicReference<String>()
        val callback: java.util.concurrent.Callable<String> = java.util.concurrent.Callable<String> {
            value.set(Context.current()[ANIMAL])
            "foo"
        }
        assertThat(callback.call()).isEqualTo("foo")
        assertThat(value).hasValue(null)
        assertThat(CAT.wrap(callback).call()).isEqualTo("foo")
        assertThat(value).hasValue("cat")
        assertThat(callback.call()).isEqualTo("foo")
        assertThat(value).hasValue(null)
    }

    @Test
    fun wrapExecutor() {
        val value: java.util.concurrent.atomic.AtomicReference<String> =
            java.util.concurrent.atomic.AtomicReference<String>()
        val executor: Executor = MoreExecutors.directExecutor()
        val callback = Runnable { value.set(Context.current()[ANIMAL]) }
        executor.execute(callback)
        assertThat(value).hasValue(null)
        CAT.wrap(executor).execute(callback)
        assertThat(value).hasValue("cat")
        executor.execute(callback)
        assertThat(value).hasValue(null)
        CAT.makeCurrent().use { ignored ->
            Context.taskWrapping(executor).execute(callback)
            assertThat(value).hasValue("cat")
        }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    internal inner class WrapExecutorService {
        protected var executor: ScheduledExecutorService? = null
        protected var wrapped: ExecutorService? = null
        protected var value: java.util.concurrent.atomic.AtomicReference<String>? = null
        protected fun wrap(executorService: ExecutorService?): ExecutorService {
            return CAT.wrap(executorService)
        }

        @BeforeAll
        fun initExecutor() {
            executor = Executors.newSingleThreadScheduledExecutor()
            wrapped = wrap(executor)
        }

        @AfterAll
        fun stopExecutor() {
            executor.shutdown()
        }

        @BeforeEach
        fun setUp() {
            value = java.util.concurrent.atomic.AtomicReference<String>()
        }

        @Test
        fun execute() {
            val runnable = Runnable { value.set(Context.current()[ANIMAL]) }
            wrapped.execute(runnable)
            await().untilAsserted { assertThat(value).hasValue("cat") }
        }

        @Test
        fun submitRunnable() {
            val runnable = Runnable { value.set(Context.current()[ANIMAL]) }
            Futures.getUnchecked(wrapped.submit(runnable))
            assertThat(value).hasValue("cat")
        }

        @Test
        fun submitRunnableResult() {
            val runnable = Runnable { value.set(Context.current()[ANIMAL]) }
            assertThat(Futures.getUnchecked(wrapped.submit(runnable, "foo"))).isEqualTo("foo")
            assertThat(value).hasValue("cat")
        }

        @Test
        fun submitCallable() {
            val callable: java.util.concurrent.Callable<String> = java.util.concurrent.Callable<String> {
                value.set(Context.current()[ANIMAL])
                "foo"
            }
            assertThat(Futures.getUnchecked(wrapped.submit(callable))).isEqualTo("foo")
            assertThat(value).hasValue("cat")
        }

        @Test
        @Throws(java.lang.Exception::class)
        fun invokeAll() {
            val value1: java.util.concurrent.atomic.AtomicReference<String> =
                java.util.concurrent.atomic.AtomicReference<String>()
            val value2: java.util.concurrent.atomic.AtomicReference<String> =
                java.util.concurrent.atomic.AtomicReference<String>()
            val callable1: java.util.concurrent.Callable<String> = java.util.concurrent.Callable<String> {
                value1.set(Context.current()[ANIMAL])
                "foo"
            }
            val callable2: java.util.concurrent.Callable<String> = java.util.concurrent.Callable<String> {
                value2.set(Context.current()[ANIMAL])
                "bar"
            }
            val futures: List<Future<String>> =
                wrapped.invokeAll<String>(listOf<java.util.concurrent.Callable<String>>(callable1, callable2))
            assertThat(futures[0].get()).isEqualTo("foo")
            assertThat(futures[1].get()).isEqualTo("bar")
            assertThat(value1).hasValue("cat")
            assertThat(value2).hasValue("cat")
        }

        @Test
        @Throws(java.lang.Exception::class)
        fun invokeAllTimeout() {
            val value1: java.util.concurrent.atomic.AtomicReference<String> =
                java.util.concurrent.atomic.AtomicReference<String>()
            val value2: java.util.concurrent.atomic.AtomicReference<String> =
                java.util.concurrent.atomic.AtomicReference<String>()
            val callable1: java.util.concurrent.Callable<String> = java.util.concurrent.Callable<String> {
                value1.set(Context.current()[ANIMAL])
                "foo"
            }
            val callable2: java.util.concurrent.Callable<String> = java.util.concurrent.Callable<String> {
                value2.set(Context.current()[ANIMAL])
                "bar"
            }
            val futures: List<Future<String>> = wrapped.invokeAll<String>(
                listOf<java.util.concurrent.Callable<String>>(callable1, callable2),
                10,
                TimeUnit.SECONDS
            )
            assertThat(futures[0].get()).isEqualTo("foo")
            assertThat(futures[1].get()).isEqualTo("bar")
            assertThat(value1).hasValue("cat")
            assertThat(value2).hasValue("cat")
        }

        @Test
        @Throws(java.lang.Exception::class)
        fun invokeAny() {
            val value1: java.util.concurrent.atomic.AtomicReference<String> =
                java.util.concurrent.atomic.AtomicReference<String>()
            val value2: java.util.concurrent.atomic.AtomicReference<String> =
                java.util.concurrent.atomic.AtomicReference<String>()
            val callable1: java.util.concurrent.Callable<String> = java.util.concurrent.Callable<String> {
                value1.set(Context.current()[ANIMAL])
                throw IllegalStateException("callable2 wins")
            }
            val callable2: java.util.concurrent.Callable<String> = java.util.concurrent.Callable<String> {
                value2.set(Context.current()[ANIMAL])
                "bar"
            }
            assertThat(wrapped.invokeAny(listOf(callable1, callable2))).isEqualTo("bar")
            assertThat(value1).hasValue("cat")
            assertThat(value2).hasValue("cat")
        }

        @Test
        @Throws(java.lang.Exception::class)
        fun invokeAnyTimeout() {
            val value1: java.util.concurrent.atomic.AtomicReference<String> =
                java.util.concurrent.atomic.AtomicReference<String>()
            val value2: java.util.concurrent.atomic.AtomicReference<String> =
                java.util.concurrent.atomic.AtomicReference<String>()
            val callable1: java.util.concurrent.Callable<String> = java.util.concurrent.Callable<String> {
                value1.set(Context.current()[ANIMAL])
                throw IllegalStateException("callable2 wins")
            }
            val callable2: java.util.concurrent.Callable<String> = java.util.concurrent.Callable<String> {
                value2.set(Context.current()[ANIMAL])
                "bar"
            }
            assertThat(wrapped.invokeAny(listOf(callable1, callable2), 10, TimeUnit.SECONDS))
                .isEqualTo("bar")
            assertThat(value1).hasValue("cat")
            assertThat(value2).hasValue("cat")
        }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    internal inner class CurrentContextWrappingExecutorService : WrapExecutorService() {
        override fun wrap(executorService: ExecutorService?): ExecutorService {
            return Context.taskWrapping(executorService)
        }

        private var scope: Scope? = null

        @BeforeEach // Closed in AfterEach
        fun makeCurrent() {
            scope = CAT.makeCurrent()
        }

        @AfterEach
        fun close() {
            scope!!.close()
            scope = null
        }
    }

    @Test
    fun keyToString() {
        assertThat(ANIMAL.toString()).isEqualTo("animal")
    }

    @Test
    fun attachSameContext() {
        val context = Context.current().with(ANIMAL, "cat")
        context.makeCurrent().use { scope1 ->
            assertThat(scope1).isNotSameAs(Scope.noop())
            context.makeCurrent().use { scope2 -> assertThat(scope2).isSameAs(Scope.noop()) }
        }
    }

    // We test real context-related above but should test cleanup gets delegated, which is best with
    // a mock.
    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    internal inner class DelegatesToExecutorService {
        @Mock
        private val executor: ExecutorService? = null
        @Test
        @Throws(java.lang.Exception::class)
        fun delegatesCleanupMethods() {
            val wrapped: ExecutorService = CAT.wrap(executor)
            wrapped.shutdown()
            verify(executor).shutdown()
            verifyNoMoreInteractions(executor)
            wrapped.shutdownNow()
            verify(executor).shutdownNow()
            verifyNoMoreInteractions(executor)
            `when`(executor.isShutdown()).thenReturn(true)
            assertThat(wrapped.isShutdown()).isTrue()
            verify(executor).isShutdown()
            verifyNoMoreInteractions(executor)
            `when`(wrapped.isTerminated()).thenReturn(true)
            assertThat(wrapped.isTerminated()).isTrue()
            verify(executor).isTerminated()
            verifyNoMoreInteractions(executor)
            `when`(executor.awaitTermination(anyLong(), any())).thenReturn(true)
            assertThat(wrapped.awaitTermination(1, TimeUnit.SECONDS)).isTrue()
            verify(executor).awaitTermination(1, TimeUnit.SECONDS)
            verifyNoMoreInteractions(executor)
        }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    internal inner class WrapScheduledExecutorService : WrapExecutorService() {
        private var wrapScheduled: ScheduledExecutorService? = null
        @BeforeEach
        fun wrapScheduled() {
            wrapScheduled = CAT.wrap(executor)
        }

        @Test
        @Throws(java.lang.Exception::class)
        fun scheduleRunnable() {
            val runnable = Runnable { value.set(Context.current()[ANIMAL]) }
            wrapScheduled.schedule(runnable, 0, TimeUnit.SECONDS).get()
            assertThat(value).hasValue("cat")
        }

        @Test
        @Throws(java.lang.Exception::class)
        fun scheduleCallable() {
            val callable: java.util.concurrent.Callable<String> = java.util.concurrent.Callable<String> {
                value.set(Context.current()[ANIMAL])
                "foo"
            }
            assertThat(wrapScheduled.schedule<String>(callable, 0, TimeUnit.SECONDS).get()).isEqualTo("foo")
            assertThat(value).hasValue("cat")
        }

        @Test
        fun scheduleAtFixedRate() {
            val runnable = Runnable { value.set(Context.current()[ANIMAL]) }
            val future: ScheduledFuture<*> = wrapScheduled.scheduleAtFixedRate(runnable, 0, 10, TimeUnit.SECONDS)
            await().untilAsserted { assertThat(value).hasValue("cat") }
            future.cancel(true)
        }

        @Test
        fun scheduleWithFixedDelay() {
            val runnable = Runnable { value.set(Context.current()[ANIMAL]) }
            val future: ScheduledFuture<*> = wrapScheduled.scheduleWithFixedDelay(runnable, 0, 10, TimeUnit.SECONDS)
            await().untilAsserted { assertThat(value).hasValue("cat") }
            future.cancel(true)
        }
    }

    @Test
    fun emptyContext() {
        assertThat(Context.root()[HashCollidingKey()]).isEqualTo(null)
    }

    @Test
    fun string() {
        assertThat(Context.root()).hasToString("{}")
        assertThat(Context.root().with(ANIMAL, "cat")).hasToString("{animal=cat}")
        assertThat(Context.root().with(ANIMAL, "cat").with(BAG, 10))
            .hasToString("{animal=cat, bag=10}")
    }

    @Test
    fun hashcodeCollidingKeys() {
        val context = Context.root()
        val cheese = HashCollidingKey()
        val wine = HashCollidingKey()
        val twoKeys = context.with<String>(cheese, "whiz").with<String>(
            wine, "boone's farm"
        )
        assertThat(twoKeys[wine]).isEqualTo("boone's farm")
        assertThat(twoKeys[cheese]).isEqualTo("whiz")
    }

    private class HashCollidingKey : ContextKey<String?> {
        override fun hashCode(): Int {
            return 1
        }
    }

    companion object {
        private val ANIMAL: ContextKey<String> = ContextKey.named("animal")
        private val BAG: ContextKey<Any?> = ContextKey.named("bag")
        private val CAT = Context.current().with(ANIMAL, "cat")
    }
}*/
