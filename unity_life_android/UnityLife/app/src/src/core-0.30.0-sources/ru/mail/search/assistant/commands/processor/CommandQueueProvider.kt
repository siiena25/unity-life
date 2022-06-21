package ru.mail.search.assistant.commands.processor

interface CommandQueueProvider {

    suspend fun requireQueue(): CommandQueue

    suspend fun getQueue(): CommandQueue?

    suspend fun release(cause: Throwable? = null)
}