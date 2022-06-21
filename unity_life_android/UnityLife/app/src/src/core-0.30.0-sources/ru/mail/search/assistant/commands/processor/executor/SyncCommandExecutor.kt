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

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext

internal class SyncCommandExecutor(
    parentContext: CoroutineContext?,
    poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) : BaseCommandExecutor(parentContext, poolDispatcher, logger) {

    companion object {
        private const val TAG = "SyncCommandExecutor"
    }

    private val isCommandExecutionProcessStarted = AtomicBoolean()
    private val commandsQueue = ConcurrentLinkedQueue<ExecutorCommand<*>>()
    private val currentCommand: AtomicReference<ExecutorCommand<*>> = AtomicReference()

    override suspend fun <T> execute(
        command: ExecutableCommand<T>,
        context: ExecutionContext
    ): Deferred<T> {
        val executableCommand = getExecutorCommand(command, context)
        addCommandInQueue(executableCommand)
        launchExecution()
        return executableCommand.deferredResult
    }

    override suspend fun notify(notification: CommandNotification) {
        withQueueLock {
            getCurrentCommand()?.notifySafe { notify(notification) }
            commandsQueue.forEach { command -> command.notifySafe { notify(notification) } }
        }
    }

    override suspend fun silence() {
        forEachCommand { command -> command.notifySafe { silence() } }
    }

    override suspend fun revoke() {
        forEachCommand { command -> command.notifySafe { revoke() } }
    }

    override suspend fun cancel(cause: Throwable) {
        withQueueLock {
            getCurrentCommand()?.let { command -> cancelCommand(command, cause) }
            currentCommand.set(null)
            commandsQueue.map { command -> launch { cancelCommand(command, cause) } }.joinAll()
            commandsQueue.clear()
            cancel()
        }
    }

    private suspend fun cancelCommand(command: ExecutorCommand<*>, cause: Throwable) {
        command.notifySafe { revoke() }
        command.flushResult(cause)
    }

    private suspend fun launchExecution() {
        if (enterCommandExecution()) {
            launch {
                while (true) {
                    val nextCommand = getNextCommandFromQueue()
                    if (nextCommand != null) {
                        nextCommand.execute()
                        removeCommandFromQueue(nextCommand)
                        nextCommand.flushResult()
                    } else {
                        if (exitCommandExecution()) return@launch
                    }
                }
            }
        }
    }

    private suspend inline fun forEachCommand(
        crossinline block: suspend (command: ExecutorCommand<*>) -> Unit
    ) {
        withQueueLock {
            getCurrentCommand()?.also { command ->
                runCatching { block(command) }
                    .onFailure { error -> onNotifyingError(error) }
            }
            commandsQueue.forEach { command ->
                runCatching { block(command) }
                    .onFailure { error -> onNotifyingError(error) }
            }
        }
    }

    private fun getCurrentCommand(): ExecutorCommand<*>? {
        return currentCommand.get()
    }

    private suspend fun addCommandInQueue(command: ExecutorCommand<*>) = withQueueLock {
        commandsQueue.add(command)
    }

    private suspend fun removeCommandFromQueue(command: ExecutorCommand<*>) = withQueueLock {
        currentCommand.compareAndSet(command, null)
    }

    private suspend fun getNextCommandFromQueue(): ExecutorCommand<*>? = withQueueLock {
        val next = commandsQueue.poll()
        currentCommand.set(next)
        next
    }

    private suspend fun enterCommandExecution(): Boolean = withQueueLock {
        isCommandExecutionProcessStarted.compareAndSet(false, true)
    }

    private suspend fun exitCommandExecution(): Boolean = withQueueLock {
        if (commandsQueue.isEmpty()) {
            isCommandExecutionProcessStarted.set(false)
            true
        } else {
            false
        }
    }

    private inline fun ExecutorCommand<*>.notifySafe(block: ExecutorCommand<*>.() -> Unit) {
        runCatching { block() }
            .onFailure { error -> onNotifyingError(error) }
    }

    private fun onNotifyingError(error: Throwable) {
        logger?.e(TAG, error, "Sync executor notifying error")
    }
}