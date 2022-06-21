package ru.mail.search.assistant.commands.command.sms

import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.data.AssistantContextRepository
import ru.mail.search.assistant.data.ContactsRepository

internal class SendSmsCommand(
    private val numberId: Int,
    private val text: String,
    private val contactsRepository: ContactsRepository,
    private val contextRepository: AssistantContextRepository,
) : ExecutableCommand<Unit> {

    override suspend fun execute(context: ExecutionContext) {
        val number = contactsRepository.getPhoneNumber(numberId).number
        contextRepository.requestSmsNavigation(number, text)
    }
}