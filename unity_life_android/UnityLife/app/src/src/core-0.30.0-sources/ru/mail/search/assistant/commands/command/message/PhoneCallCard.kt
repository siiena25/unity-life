package ru.mail.search.assistant.commands.command.message

import ru.mail.search.assistant.commands.factory.CommandsFactory
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.data.ContactsRepository
import ru.mail.search.assistant.entities.message.MessageData

internal class PhoneCallCard(
    private val phraseId: String,
    private val contactId: Int,
    private val numberId: Int,
    private val contactsRepository: ContactsRepository,
    private val commandsFactory: CommandsFactory,
) : ExecutableCommand<Unit> {

    override suspend fun execute(context: ExecutionContext) {
        val contact = contactsRepository.getContact(contactId)
        val phoneNumber = contact.numbers.first { it.id == numberId }.number
        val message = MessageData.ContactCard(
            contactId,
            contact.firstName,
            contact.lastName,
            contact.photoUri,
            phoneNumber
        )
        context.sync(commandsFactory.addMessage(phraseId, message)).await()
    }
}