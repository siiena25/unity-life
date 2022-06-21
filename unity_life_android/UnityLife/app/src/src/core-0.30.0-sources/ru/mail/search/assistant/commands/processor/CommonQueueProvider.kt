package ru.mail.search.assistant.commands.processor

import kotlinx.coroutines.CancellationException
import java.util.concurrent.atomic.AtomicReference

internal class CommonQueueProvider(queue: CommandQueue) : CommandQueueProvider {

    private val queueRef: AtomicReference<CommandQueue?> = AtomicReference(queue)

    override suspend fun requireQueue(): CommandQueue {
        return queueRef.get() ?: throw CancellationException("Queue already released")
    }

    override suspend fun getQueue(): CommandQueue? {
        return queueRef.get()
    }

    override suspend fun release(cause: Throwable?) {
        queueRef.getAndSet(null)?.release(cause)
    }
}