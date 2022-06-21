package ru.mail.search.assistant.commands.command.userinput

import ru.mail.search.assistant.commands.command.CancelableExecutableCommand
import ru.mail.search.assistant.commands.factory.CommandsFactory
import ru.mail.search.assistant.commands.factory.UserInputCommandsFactory
import ru.mail.search.assistant.commands.processor.ExecutableCommandData
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.commands.processor.QueueType
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.MessagesRepository
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.util.Tag

internal class SendEventWithText(
    private val event: String,
    private val text: String?,
    private val callbackData: String?,
    private val clientData: String?,
    private val messagesRepository: MessagesRepository,
    private val userInputCommandsFactory: UserInputCommandsFactory,
    private val commandsFactory: CommandsFactory,
    private val logger: Logger?
) : CancelableExecutableCommand<PhraseResult>(logger = logger) {

    override suspend fun execute(context: ExecutionContext): PhraseResult {
        val phraseString = context.phrase.toShortString()
        logger?.i(Tag.ASSISTANT_COMMAND, "start executing SendEventWithText $phraseString")
        return sendEvent(context)
            .also { checkForCancellation() }
            .also { messagesRepository.clearSuggests() }
            .let(::appendOutgoingMessage)
    }

    private suspend fun sendEvent(context: ExecutionContext): PhraseResult {
        val sendEvent = userInputCommandsFactory.sendEvent(event, callbackData, clientData)
        return context.async(sendEvent).await()
    }

    private fun appendOutgoingMessage(phraseResult: PhraseResult): PhraseResult {
        text ?: return phraseResult
        val phraseId = phraseResult.metadata.phraseId
        val addMessage = commandsFactory.addMessage(phraseId, MessageData.OutgoingData(text))
        val commands =
            listOf(ExecutableCommandData(QueueType.SYNC, addMessage)) + phraseResult.commands
        return PhraseResult(phraseResult.metadata, commands)
    }
}