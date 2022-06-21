package ru.mail.search.assistant.commands.command.message

import kotlinx.coroutines.withContext
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.MessagesRepository
import ru.mail.search.assistant.entities.message.DialogMessage
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.util.Tag

internal class AddMessage(
    val phraseId: String,
    val data: MessageData,
    private val messagesRepository: MessagesRepository,
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) : ExecutableCommand<DialogMessage> {

    override val commandName: String = "AddMessage"

    override suspend fun execute(context: ExecutionContext): DialogMessage {
        logger?.i(Tag.ASSISTANT_COMMAND, "start executing $commandName")
        return withContext(poolDispatcher.io) {
            messagesRepository.addMessage(phraseId, data)
        }
    }
}