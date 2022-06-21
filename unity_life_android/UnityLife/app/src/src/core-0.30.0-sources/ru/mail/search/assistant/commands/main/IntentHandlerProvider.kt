package ru.mail.search.assistant.commands.main

import ru.mail.search.assistant.commands.CommandsAdapter
import ru.mail.search.assistant.commands.intent.AssistantIntentsHandler
import ru.mail.search.assistant.AssistantIntentHandlerProvider

internal class IntentHandlerProvider(
    private val intentHandlerProvider: AssistantIntentHandlerProvider?,
    private val adapter: CommandsAdapter,
    private val mainIntentsHandler: MainIntentsHandler
) {

    val handlers: List<AssistantIntentsHandler> = createHandlers()

    private fun createHandlers(): List<AssistantIntentsHandler> {
        val externalHandlers = intentHandlerProvider?.provide(adapter).orEmpty()
        return externalHandlers + mainIntentsHandler
    }
}