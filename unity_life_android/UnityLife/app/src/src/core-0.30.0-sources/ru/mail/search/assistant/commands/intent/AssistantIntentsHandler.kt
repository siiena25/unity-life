package ru.mail.search.assistant.commands.intent

import ru.mail.search.assistant.commands.processor.ExecutionContext

interface AssistantIntentsHandler {

    suspend fun handle(context: ExecutionContext, intent: AssistantIntent): Boolean
}