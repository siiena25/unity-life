package ru.mail.search.assistant.commands.processor

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import kotlin.coroutines.CoroutineContext

internal class LazyCommandQueueProvider(
    private val parentContext: CoroutineContext?,
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) : CommandQueueProvider {

    private val mutex = Mutex()

    @Volatile
    private var queueRef: CommandQueue? = null
    private var isReleased = false

    override suspend fun requireQueue(): CommandQueue {
        return queueRef
            ?: mutex.withLock {
                if (!isReleased) {
                    queueRef ?: newInstance()
                } else {
                    throw CancellationException("Queue already released")
                }
            }
    }

    override suspend fun getQueue(): CommandQueue? {
        return queueRef ?: mutex.withLock { queueRef }
    }

    override suspend fun release(cause: Throwable?) {
        mutex.withLock {
            isReleased = true
            queueRef?.release(cause)
            queueRef = null
        }
    }

    private fun newInstance(): CommandQueue {
        val newInstance = CommandQueue(parentContext, poolDispatcher, logger)
        queueRef = newInstance
        return newInstance
    }
}