package ru.mail.search.assistant.commands

import ru.mail.search.assistant.data.MessagesRepository

internal class CommandsMessagesAdapterImpl(
    private val messagesRepository: MessagesRepository
) : CommandsMessagesAdapter {

    override suspend fun clearSuggests() {
        messagesRepository.clearSuggests()
    }
}