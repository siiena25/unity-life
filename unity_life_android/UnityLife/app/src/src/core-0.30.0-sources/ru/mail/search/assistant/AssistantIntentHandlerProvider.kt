package ru.mail.search.assistant

import ru.mail.search.assistant.commands.CommandsAdapter
import ru.mail.search.assistant.commands.intent.AssistantIntentsHandler

interface AssistantIntentHandlerProvider {

    fun provide(adapter: CommandsAdapter): List<AssistantIntentsHandler>
}