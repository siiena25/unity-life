package ru.mail.search.assistant.common.util

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.mail.search.assistant.common.data.exception.AuthException

class SingleLoadOperation<T>(private val operation: Operation<T>) {

    private val mutex = Mutex()
    private val notInitialized = CachedValue.NotInitialized<T>()

    @Volatile
    private var cachedData: CachedValue<T> = notInitialized

    @Volatile
    private var lastResult: CompletableDeferred<T>? = null

    suspend fun getData(): T {
        return getCachedData() ?: loadData()
    }

    suspend fun compareAndClear(compare: (value: T) -> Boolean) {
        mutex.withLock {
            val data = cachedData as? CachedValue.Initialized ?: return
            if (compare(data.data)) {
                cachedData = notInitialized
                lastResult = null
            }
        }
    }

    private fun getCachedData(): T? {
        return cachedData.let { data -> data as? CachedValue.Initialized }?.data
            ?.takeIf(operation::isCacheValid)
    }

    private suspend fun loadData(): T {
        return if (mutex.tryLock()) {
            val cachedSession = getCachedData()
            if (cachedSession != null) {
                mutex.unlock()
                cachedSession
            } else {
                val result = CompletableDeferred<T>()
                lastResult = result
                runCatching { operation.load() }
                    .onSuccess { data ->
                        this.cachedData = CachedValue.Initialized(data)
                        result.complete(data)
                    }
                    .onFailure { error -> result.completeExceptionally(error) }
                    .also { mutex.unlock() }
                    .getOrThrow()
            }
        } else {
            mutex.withLock {
                getCachedData() ?: requireLastResult()
            }
        }
    }

    private suspend fun requireLastResult(): T {
        val result = lastResult ?: throw AuthException("Missing session")
        return result.await()
    }

    interface Operation<T> {

        suspend fun load(): T

        fun isCacheValid(data: T): Boolean = true
    }

    private sealed class CachedValue<T> {

        abstract val data: T?

        class Initialized<T>(override val data: T) : CachedValue<T>()

        class NotInitialized<T> : CachedValue<T>() {

            override val data: T? = null
        }
    }
}