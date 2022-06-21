package ru.mail.search.assistant.commands.command

import androidx.annotation.CallSuper
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.commands.processor.model.CommandNotification
import ru.mail.search.assistant.common.util.Logger

abstract class CancelableContextCommand<T>(
    logger: Logger?
) : CancelableExecutableCommand<T>(logger) {

    private val commandContext = SupervisorJob()

    final override suspend fun execute(context: ExecutionContext): T {
        return runCatching {
            withContext(commandContext) {
                executeInContext(context)
            }
        }
            .onSuccess { commandContext.complete() }
            .onFailure { error -> commandContext.completeExceptionally(error) }
            .getOrThrow()
    }

    abstract suspend fun executeInContext(context: ExecutionContext): T

    @CallSuper
    override suspend fun onCancel(cause: CommandNotification) {
        commandContext.cancel()
    }
}