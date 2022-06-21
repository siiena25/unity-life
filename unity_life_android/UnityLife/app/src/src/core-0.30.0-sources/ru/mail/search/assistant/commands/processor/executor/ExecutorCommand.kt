package ru.mail.search.assistant.commands.processor.executor

import kotlinx.coroutines.*
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.commands.processor.NotificationContext
import ru.mail.search.assistant.commands.processor.model.CommandNotification
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.coroutines.requireJob
import ru.mail.search.assistant.util.Tag
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.CoroutineContext

internal class ExecutorCommand<T>(
    private val command: ExecutableCommand<T>,
    parentCoroutineContext: CoroutineContext,
    parentExecutionContext: ExecutionContext,
    private val logger: Logger?
) {

    val deferredResult: Deferred<T> get() = deferred

    private val commandContext = Job(parentCoroutineContext.requireJob())
    private val executionContext = parentExecutionContext.createChildContext(commandContext)
    private val deferred = CompletableDeferred<T>(parent = commandContext)
    private val result = AtomicReference<CommandResult<T>?>()

    suspend fun execute() {
        val phrase = executionContext.phrase.toShortString()
        logger?.i(Tag.ASSISTANT_COMMAND, "Execution start $phrase")
        val commandResult = runCatching { executeCommand() }
            .fold({ result ->
                logger?.i(Tag.ASSISTANT_COMMAND, "Execution successfully finished $phrase")
                CommandResult.Success(result)
            }, { error ->
                logger?.i(Tag.ASSISTANT_COMMAND, "Execution finished with error $phrase")
                CommandResult.Failure<T>(error)
            })
        result.set(commandResult)
    }

    suspend fun flushResult(default: Throwable? = null) {
        executionContext.release(default)
        when (val result = result.get()) {
            is CommandResult.Success -> {
                deferred.complete(result.value)
                commandContext.complete()
            }
            is CommandResult.Failure -> {
                deferred.completeExceptionally(result.error)
                commandContext.completeExceptionally(result.error)
            }
            else -> {
                when {
                    default is CancellationException -> {
                        deferred.cancel(default)
                        commandContext.cancel(default)
                    }
                    default != null -> {
                        deferred.completeExceptionally(default)
                        commandContext.completeExceptionally(default)
                    }
                    else -> {
                        deferred.cancel()
                        commandContext.cancel()
                    }
                }
            }
        }
    }

    suspend fun notify(notification: CommandNotification) {
        executionContext.notify(notification)
        val context = createNotificationContext(notification)
        return command.notify(context)
    }

    suspend fun silence() {
        executionContext.silence()
        val context = createNotificationContext(CommandNotification.Silence)
        command.notify(context)
    }

    suspend fun revoke() {
        executionContext.revoke()
        val context = createNotificationContext(CommandNotification.Revoke)
        command.notify(context)
    }

    private suspend fun executeCommand(): T {
        return withContext(commandContext) {
            command.execute(executionContext)
        }
    }

    private fun createNotificationContext(notification: CommandNotification): NotificationContext {
        return NotificationContext(
            executionContext.assistant,
            executionContext.phrase,
            notification
        )
    }

    private sealed class CommandResult<T> {

        class Success<T>(val value: T) : CommandResult<T>()

        class Failure<T>(val error: Throwable) : CommandResult<T>()
    }
}