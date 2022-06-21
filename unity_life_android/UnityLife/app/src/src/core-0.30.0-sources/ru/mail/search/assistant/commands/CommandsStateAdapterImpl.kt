package ru.mail.search.assistant.commands

import ru.mail.search.assistant.commands.command.CommandErrorHandler
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.data.AssistantContextRepository
import ru.mail.search.assistant.entities.assistant.AssistantStatus

internal class CommandsStateAdapterImpl(
    private val errorHandler: CommandErrorHandler,
    private val contextRepository: AssistantContextRepository
) : CommandsStateAdapter {

    override suspend fun setContextAssistantStatus(
        context: ExecutionContext,
        status: AssistantStatus
    ) {
        contextRepository.setRecordingStatus(status, getOwnerId(context))
    }

    override suspend fun resetContextAssistantStatus(context: ExecutionContext) {
        contextRepository.setRecordingStatus(AssistantStatus.DEFAULT, getOwnerId(context))
    }

    override fun notifyCommandError(error: Throwable) {
        errorHandler.onError(error)
    }

    private fun getOwnerId(context: ExecutionContext): String {
        return "${context.phrase.requestId}/${context.phrase.contextId}"
    }
}