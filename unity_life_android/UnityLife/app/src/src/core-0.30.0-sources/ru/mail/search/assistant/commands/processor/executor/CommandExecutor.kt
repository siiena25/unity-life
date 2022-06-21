package ru.mail.search.assistant.commands.processor.executor

import kotlinx.coroutines.Deferred
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.commands.processor.model.CommandNotification

interface CommandExecutor {

    suspend fun <T> execute(command: ExecutableCommand<T>, context: ExecutionContext): Deferred<T>

    suspend fun notify(notification: CommandNotification)

    suspend fun silence()

    suspend fun revoke()

    suspend fun cancel(cause: Throwable)
}