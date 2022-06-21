package ru.mail.search.assistant.commands.command.message

import ru.mail.search.assistant.commands.factory.CommandsFactory
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.data.ContactsRepository
import ru.mail.search.assistant.entities.message.DialogMessage
import ru.mail.search.assistant.entities.message.MessageData

internal class AddContactNumbersCard(
    private val contactId: Int,
    private val callbackEvent: String,
    private val phraseId: String,
    private val contactsRepository: ContactsRepository,
    private val commandsFactory: CommandsFactory,
) : ExecutableCommand<DialogMessage> {

    override suspend fun execute(context: ExecutionContext): DialogMessage {
        val contact = contactsRepository.getContact(contactId)
        val numbers = contact.numbers.map {
            MessageData.ContactNumbersCard.PhoneNumber(
                id = it.id,
                number = it.number,
                name = it.name.orEmpty(),
            )
        }
        val data = MessageData.ContactNumbersCard(
            callbackEvent,
            contact.id,
            contact.firstName,
            contact.lastName,
            contact.photoUri,
            numbers,
        )

        val command = commandsFactory.addMessage(phraseId, data)
        return context.sync(command).await()
    }
}