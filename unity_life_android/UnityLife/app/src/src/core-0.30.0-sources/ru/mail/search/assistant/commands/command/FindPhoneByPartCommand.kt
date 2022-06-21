package ru.mail.search.assistant.commands.command

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import ru.mail.search.assistant.commands.factory.PublicCommandsFactory
import ru.mail.search.assistant.commands.factory.UserInputCommandsFactory
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.data.ContactsRepository

internal class FindPhoneByPartCommand(
    private val contactId: Int,
    private val phonePart: String,
    private val callbackEvent: String,
    private val contactsRepository: ContactsRepository,
    private val userInputCommandsFactory: UserInputCommandsFactory,
    private val publicCommandsFactory: PublicCommandsFactory,
) : ExecutableCommand<Unit> {

    override suspend fun execute(context: ExecutionContext) {
        val numbersElement = JsonArray()
        contactsRepository.getContact(contactId)
            .numbers
            .mapIndexedNotNull { index, phoneNumber -> index.takeIf { phoneNumber.number.contains(phonePart) } }
            .forEach(numbersElement::add)

        val clientData = JsonObject()
            .apply { add("phones", numbersElement) }
            .toString()

        val event = userInputCommandsFactory.sendEvent(callbackEvent, clientData = clientData, callbackData = null)
        context.sync(publicCommandsFactory.executePhraseCommand(event)).await()
    }
}