package ru.mail.search.assistant.commands.command.phrase

import kotlinx.coroutines.Deferred
import ru.mail.search.assistant.commands.command.CancelableContextCommand
import ru.mail.search.assistant.commands.command.CommandErrorHandler
import ru.mail.search.assistant.commands.processor.ExecutableCommandData
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.commands.processor.QueueType
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.util.Tag

internal class NestedExecutionCommand(
    private val commands: List<ExecutableCommandData>,
    private val commandErrorHandler: CommandErrorHandler,
    private val logger: Logger?
) : CancelableContextCommand<Unit>(logger) {

    override suspend fun executeInContext(context: ExecutionContext) {
        val phraseString = context.phrase.toShortString()
        logger?.i(Tag.ASSISTANT_COMMAND, "start executing NestedExecutionCommand $phraseString")
        commands
            .map { commandData -> executeAsync(context, commandData) }
            .forEach { result ->
                runCatching { result.await() }
                    .onFailure { error -> commandErrorHandler.onError(error) }
            }
    }

    private suspend fun executeAsync(
        context: ExecutionContext,
        commandData: ExecutableCommandData
    ): Deferred<*> {
        return when (commandData.queueType) {
            QueueType.SYNC -> context.sync(commandData.command)
            QueueType.MEDIA -> context.mediaEvent(commandData.command)
        }
    }
}