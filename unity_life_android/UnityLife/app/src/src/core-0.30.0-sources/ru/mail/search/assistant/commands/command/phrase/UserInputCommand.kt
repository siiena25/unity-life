package ru.mail.search.assistant.commands.command.phrase

import ru.mail.search.assistant.commands.CommandsStatisticAdapter
import ru.mail.search.assistant.commands.command.CancelableContextCommand
import ru.mail.search.assistant.commands.command.CommandErrorHandler
import ru.mail.search.assistant.commands.command.userinput.PhraseResult
import ru.mail.search.assistant.commands.factory.PublicCommandsFactory
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.commands.processor.NotificationContext
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.entities.PhraseMetadata
import ru.mail.search.assistant.util.Tag

internal class UserInputCommand(
    private val sourceCommand: ExecutableCommand<PhraseResult>,
    private val publicCommandsFactory: PublicCommandsFactory,
    private val commandErrorHandler: CommandErrorHandler,
    private val statisticAdapter: CommandsStatisticAdapter,
    private val logger: Logger?
) : CancelableContextCommand<PhraseMetadata>(logger) {

    override suspend fun executeInContext(context: ExecutionContext): PhraseMetadata {
        val phraseString = context.phrase.toShortString()
        logger?.i(Tag.ASSISTANT_COMMAND, "start executing UserInputCommand $phraseString")
        val userInputContext = context.overridePhraseRequestId()
        statisticAdapter.initializePhrase(userInputContext)
        return runCatching { executeNewUserRequest(userInputContext) }
            .also { statisticAdapter.finishPhrase(userInputContext) }
            .getOrThrow()
    }

    override suspend fun notify(context: NotificationContext) {
        sourceCommand.notify(context)
        super.notify(context)
    }

    private suspend fun executeNewUserRequest(context: ExecutionContext): PhraseMetadata {
        val result = runCatching { sourceCommand.execute(context) }
            .onFailure { error -> commandErrorHandler.onError(error) }
            .getOrThrow()
        checkForCancellation()
        executeCommands(context, result)
        return result.metadata
    }

    private suspend fun executeCommands(context: ExecutionContext, phraseResult: PhraseResult) {
        context.async(publicCommandsFactory.executeNestedCommands(phraseResult.commands)).await()
    }
}