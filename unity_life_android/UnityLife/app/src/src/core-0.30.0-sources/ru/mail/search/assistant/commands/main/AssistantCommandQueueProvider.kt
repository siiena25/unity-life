package ru.mail.search.assistant.commands.main

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.mail.search.assistant.commands.processor.CommandQueue
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.util.Tag

class AssistantCommandQueueProvider(
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) {

    private val mutex = Mutex()

    @Volatile
    private var queueRef: CommandQueue? = null

    suspend fun requireQueue(): CommandQueue {
        return mutex.withLock {
            queueRef ?: newInstance()
        }
    }

    suspend fun release(cause: Throwable?) {
        mutex.withLock {
            queueRef?.release(cause)
            queueRef = null
        }
    }

    suspend fun silence() {
        logger?.i(Tag.ASSISTANT_COMMAND, "Request silence")
        queueRef?.silence()
    }

    suspend fun revoke() {
        logger?.i(Tag.ASSISTANT_COMMAND, "Request revoke")
        queueRef?.revoke()
    }

    private fun newInstance(): CommandQueue {
        val newInstance = CommandQueue(null, poolDispatcher, logger)
        queueRef = newInstance
        return newInstance
    }
}