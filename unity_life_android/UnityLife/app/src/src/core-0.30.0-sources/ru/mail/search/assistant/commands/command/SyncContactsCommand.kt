package ru.mail.search.assistant.commands.command

import com.google.gson.Gson
import com.google.gson.JsonObject
import ru.mail.search.assistant.commands.factory.PublicCommandsFactory
import ru.mail.search.assistant.commands.factory.UserInputCommandsFactory
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.data.ContactsManager
import ru.mail.search.assistant.data.ContactsRepository
import ru.mail.search.assistant.data.remote.dto.contacts.ContactDto

internal class SyncContactsCommand(
    private val event: String,
    private val contactsManager: ContactsManager,
    private val userInputCommandsFactory: UserInputCommandsFactory,
    private val publicCommandsFactory: PublicCommandsFactory,
    private val contactsRepository: ContactsRepository,
) : ExecutableCommand<Unit> {

    override suspend fun execute(context: ExecutionContext) {
        val contacts = contactsManager.getContacts()
        val contactsDto = contacts.map(ContactDto::fromDomain)
        val contactData = Gson().toJsonTree(contactsDto)
        val clientData = JsonObject()
            .apply { add("contacts", contactData) }
            .toString()
        val event = userInputCommandsFactory.sendEvent(event, clientData = clientData, callbackData = null)
        contactsRepository.updateContacts(contacts)
        context.sync(publicCommandsFactory.executePhraseCommand(event)).await()
    }
}