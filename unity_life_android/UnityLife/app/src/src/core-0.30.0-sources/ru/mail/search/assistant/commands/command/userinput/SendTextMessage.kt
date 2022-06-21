package ru.mail.search.assistant.commands.command.userinput

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import ru.mail.search.assistant.commands.CommandsMessagesAdapter
import ru.mail.search.assistant.commands.CommandsPhraseAdapter
import ru.mail.search.assistant.commands.CommandsStateAdapter
import ru.mail.search.assistant.commands.command.CancelableExecutableCommand
import ru.mail.search.assistant.commands.factory.PublicCommandsFactory
import ru.mail.search.assistant.commands.processor.ExecutableCommandData
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.commands.processor.QueueType
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.entities.assistant.AssistantStatus
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.util.Tag

internal class SendTextMessage(
    private val text: String,
    private val callbackData: String?,
    private val clientData: String?,
    private val appendOutgoingMessage: Boolean,
    private val phraseAdapter: CommandsPhraseAdapter,
    private val stateAdapter: CommandsStateAdapter,
    private val messagesAdapter: CommandsMessagesAdapter,
    private val commandsFactory: PublicCommandsFactory,
    private val logger: Logger?
) : CancelableExecutableCommand<PhraseResult>(logger = logger) {

    override suspend fun execute(context: ExecutionContext): PhraseResult {
        val phraseString = context.phrase.toShortString()
        logger?.i(Tag.ASSISTANT_COMMAND, "start executing SendTextMessage $phraseString")

        setStatus(context, AssistantStatus.LOADING)
        return runCatching { sendText(context) }
            .also { withContext(NonCancellable) { resetStatus(context) } }
            .getOrThrow()
            .also { checkForCancellation() }
            .also { messagesAdapter.clearSuggests() }
            .let(::appendOutgoingMessageIfNeeded)
    }

    private suspend fun sendText(context: ExecutionContext): PhraseResult {
        return phraseAdapter.sendText(
            context,
            text = text,
            callbackData = callbackData,
            clientData = clientData
        )
    }

    private fun appendOutgoingMessageIfNeeded(phraseResult: PhraseResult): PhraseResult {
        val commands = getOutgoingMessageCommandsIfNeeded() + phraseResult.commands
        return PhraseResult(phraseResult.metadata, commands)
    }

    private fun getOutgoingMessageCommandsIfNeeded(): List<ExecutableCommandData> {
        return if (appendOutgoingMessage) {
            val addMessage = commandsFactory.showMessage(MessageData.OutgoingData(text))
            listOf(ExecutableCommandData(QueueType.SYNC, addMessage))
        } else {
            emptyList()
        }
    }

    private suspend fun setStatus(context: ExecutionContext, status: AssistantStatus) {
        stateAdapter.setContextAssistantStatus(context, status)
    }

    private suspend fun resetStatus(context: ExecutionContext) {
        stateAdapter.resetContextAssistantStatus(context)
    }
}