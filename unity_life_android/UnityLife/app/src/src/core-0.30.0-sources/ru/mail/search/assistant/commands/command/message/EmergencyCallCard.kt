package ru.mail.search.assistant.commands.command.message

import ru.mail.search.assistant.commands.factory.CommandsFactory
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.entities.message.MessageData

internal class EmergencyCallCard(
    private val phraseId: String,
    private val phoneNumber: String,
    private val commandsFactory: CommandsFactory,
) : ExecutableCommand<Unit> {

    override suspend fun execute(context: ExecutionContext) {
        val message = MessageData.EmergencyCallCard(phoneNumber)
        context.sync(commandsFactory.addMessage(phraseId, message)).await()
    }
}