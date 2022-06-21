package ru.mail.search.assistant.commands.processor.executor

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.cancel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.commands.processor.model.CommandNotification
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger

import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.CoroutineContext

internal class AsyncCommandExecutor(
    parentContext: CoroutineContext?,
    poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) : BaseCommandExecutor(parentContext, poolDispatcher, logger) {

    companion object {
        private const val TAG = "AsyncCommandExecutor"
    }

    private val executingCommands = CopyOnWriteArrayList<ExecutorCommand<*>>()

    override suspend fun <T> execute(
        command: ExecutableCommand<T>,
        context: ExecutionContext
    ): Deferred<T> {
        val executorCommand = getExecutorCommand(command, context)
        launch {
            addCommandToExecution(executorCommand)
            executorCommand.execute()
            removeCommandFromExecution(executorCommand)
            executorCommand.flushResult()
        }
        return executorCommand.deferredResult
    }

    override suspend fun notify(notification: CommandNotification) {
        forEachCommand { command -> command.notifySafe { notify(notification) } }
    }

    override suspend fun silence() {
        forEachCommand { command -> command.notifySafe { silence() } }
    }

    override suspend fun revoke() {
        forEachCommand { command -> command.notifySafe { revoke() } }
    }

    override suspend fun cancel(cause: Throwable) {
        withQueueLock {
            executingCommands.map { command ->
                launch {
                    command.notifySafe { revoke() }
                    command.flushResult(cause)
                }
            }.joinAll()
            executingCommands.clear()
            cancel()
        }
    }

    private suspend fun addCommandToExecution(command: ExecutorCommand<*>) = withQueueLock {
        executingCommands.add(command)
    }

    private suspend fun removeCommandFromExecution(command: ExecutorCommand<*>) = withQueueLock {
        executingCommands.remove(command)
    }

    private suspend inline fun forEachCommand(
        crossinline block: suspend (command: ExecutorCommand<*>) -> Unit
    ) {
        withQueueLock {
            executingCommands.map { command -> launch { block(command) } }.joinAll()
        }
    }

    private inline fun ExecutorCommand<*>.notifySafe(block: ExecutorCommand<*>.() -> Unit) {
        runCatching { block() }
            .onFailure { error -> onNotifyingError(error) }
    }

    private fun onNotifyingError(error: Throwable) {
        logger?.e(TAG, error, "Async executor notifying error")
    }
}