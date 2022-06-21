package ru.mail.search.assistant.commands.processor

import kotlinx.coroutines.*
import ru.mail.search.assistant.commands.processor.executor.AsyncCommandExecutor
import ru.mail.search.assistant.commands.processor.executor.CommandExecutor
import ru.mail.search.assistant.commands.processor.executor.SyncCommandExecutor
import ru.mail.search.assistant.commands.processor.model.CommandNotification
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import kotlin.coroutines.CoroutineContext

class CommandQueue internal constructor(
    parentContext: CoroutineContext?,
    poolDispatcher: PoolDispatcher,
    logger: Logger?
) {

    private val syncCommandExecutor: CommandExecutor =
        SyncCommandExecutor(parentContext, poolDispatcher, logger)
    private val asyncCommandExecutor: CommandExecutor =
        AsyncCommandExecutor(parentContext, poolDispatcher, logger)
    private val mediaEventCommandExecutor: CommandExecutor =
        SyncCommandExecutor(parentContext, poolDispatcher, logger)

    suspend fun <T> sync(command: ExecutableCommand<T>, context: ExecutionContext): Deferred<T> {
        return syncCommandExecutor.execute(command, context)
    }

    suspend fun <T> async(command: ExecutableCommand<T>, context: ExecutionContext): Deferred<T> {
        return asyncCommandExecutor.execute(command, context)
    }

    suspend fun <T> mediaEvent(
        command: ExecutableCommand<T>,
        context: ExecutionContext
    ): Deferred<T> {
        return mediaEventCommandExecutor.execute(command, context)
    }

    suspend fun notify(notification: CommandNotification) {
        applyToExecutors { notify(notification) }
    }

    suspend fun release(cause: Throwable?) {
        val cancellationCause = if (cause !is CancellationException) {
            CancellationException("Executor released", cause)
        } else {
            cause
        }
        withContext(NonCancellable) {
            applyToExecutors { cancel(cancellationCause) }
        }
    }

    suspend fun silence() {
        applyToExecutors { silence() }
    }

    suspend fun revoke() {
        applyToExecutors { revoke() }
    }

    private suspend inline fun applyToExecutors(
        crossinline block: suspend CommandExecutor.() -> Unit
    ) {
        supervisorScope {
            getAllExecutors()
                .map { executor -> launch { executor.block() } }
                .joinAll()
        }
    }

    private fun getAllExecutors(): List<CommandExecutor> {
        return listOf(
            syncCommandExecutor,
            asyncCommandExecutor,
            mediaEventCommandExecutor
        )
    }
}