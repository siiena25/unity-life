package ru.mail.search.assistant.commands.command

import ru.mail.search.assistant.api.suggests.Suggest
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.commands.processor.NotificationContext
import ru.mail.search.assistant.commands.processor.model.CommandNotification
import ru.mail.search.assistant.commands.processor.model.InteractionMethod
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.MessagesRepository
import ru.mail.search.assistant.entities.SuggestsEntity
import ru.mail.search.assistant.util.Tag

internal class ShowSuggests(
    private val phraseId: String,
    private val suggests: List<Suggest>,
    private val messagesRepository: MessagesRepository,
    private val logger: Logger?
) : ExecutableCommand<Unit> {

    override suspend fun execute(context: ExecutionContext) {
        if (context.assistant.isRevoked) return
        val phraseText = context.phrase.toShortString()
        logger?.i(Tag.ASSISTANT_COMMAND, "start executing suggests $phraseText")
        messagesRepository.setSuggests(SuggestsEntity(phraseId, suggests))
    }

    override suspend fun notify(context: NotificationContext) {
        if (isCancellation(context.cause)) {
            context.assistant.revoke()
        }
    }

    private fun isCancellation(notification: CommandNotification): Boolean {
        return notification == CommandNotification.Revoke || isUserInputHandled(notification)
    }

    private fun isUserInputHandled(notification: CommandNotification): Boolean {
        return notification is CommandNotification.UserInput.Processed
                || (notification is CommandNotification.UserInput.Initiated
                && notification.phrase.interactionMethod != InteractionMethod.VOICE)
    }
}