package ru.mail.search.assistant.commands.command.message

import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.MessagesRepository
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.util.Tag

internal class ShowWelcomeMessage(
    private val messageData: MessageData,
    private val messagesRepository: MessagesRepository,
    private val logger: Logger?
) : ExecutableCommand<Unit> {

    override val commandName: String = "ShowWelcomeMessage"

    override suspend fun execute(context: ExecutionContext) {
        logger?.i(Tag.ASSISTANT_COMMAND, "start executing $commandName")
        messagesRepository.addWelcomeMessage(messageData)
    }
}