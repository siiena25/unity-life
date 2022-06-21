package ru.mail.search.assistant.commands.command.message

import ru.mail.search.assistant.commands.factory.CommandsFactory
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.data.ContactsRepository
import ru.mail.search.assistant.entities.message.DialogMessage
import ru.mail.search.assistant.entities.message.MessageData

internal class AddContactsCard(
    private val phraseId: String,
    private val hasMore: Boolean,
    private val page: Int,
    private val contactIds: List<Int>,
    private val callbackEvent: String,
    private val contactsRepository: ContactsRepository,
    private val commandsFactory: CommandsFactory,
) : ExecutableCommand<DialogMessage> {

    override suspend fun execute(context: ExecutionContext): DialogMessage {
        val data = MessageData.ContactsCard(
            hasMore = hasMore,
            page = page,
            callbackEvent = callbackEvent,
            contacts = getContacts(),
        )
        val command = commandsFactory.addMessage(phraseId, data)
        return context.sync(command).await()
    }

    private suspend fun getContacts(): List<MessageData.ContactsCard.Contact> {
        return contactsRepository.getContacts(contactIds)
            .map {
                MessageData.ContactsCard.Contact(
                    id = it.id,
                    firstName = it.firstName,
                    lastName = it.lastName,
                    photoUri = it.photoUri,
                    phoneNumber = it.numbers.first().number
                )
            }
    }
}