package ru.mail.search.assistant.commands.command.sms

import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.data.AssistantContextRepository

internal class EditSmsCommand(
    private val text: String,
    private val contextRepository: AssistantContextRepository,
) : ExecutableCommand<Unit> {

    override suspend fun execute(context: ExecutionContext) {
        contextRepository.setTextToInput(text)
    }
}