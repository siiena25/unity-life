package ru.mail.search.assistant.commands

import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.entities.assistant.AssistantStatus

interface CommandsStateAdapter {

    suspend fun setContextAssistantStatus(context: ExecutionContext, status: AssistantStatus)

    suspend fun resetContextAssistantStatus(context: ExecutionContext)

    fun notifyCommandError(error: Throwable)
}