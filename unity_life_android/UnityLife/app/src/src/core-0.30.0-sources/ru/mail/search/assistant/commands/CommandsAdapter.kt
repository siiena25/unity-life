package ru.mail.search.assistant.commands

import ru.mail.search.assistant.commands.factory.PublicCommandsFactory

interface CommandsAdapter {

    val phraseAdapter: CommandsPhraseAdapter
    val stateAdapter: CommandsStateAdapter
    val messagesAdapter: CommandsMessagesAdapter
    val statisticAdapter: CommandsStatisticAdapter
    val commandsFactory: PublicCommandsFactory
}