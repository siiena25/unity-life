package ru.mail.search.assistant.commands.command

import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.data.AssistantContextRepository

internal class RequestPermissionsCommand(
    private val permission: String,
    private val requestCode: Int,
    private val assistantContextRepository: AssistantContextRepository,
) : ExecutableCommand<Unit> {

    override suspend fun execute(context: ExecutionContext) {
        assistantContextRepository.requestPermission(listOf(permission), requestCode)
    }
}