package ru.mail.search.assistant.commands.command

import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.data.AssistantContextRepository

internal class EmergencyCallCommand(
    private val phoneNumber: String,
    private val contextRepository: AssistantContextRepository,
) : ExecutableCommand<Unit> {

    override suspend fun execute(context: ExecutionContext) {
        contextRepository.requestPhoneCallNavigation(phoneNumber)
    }
}