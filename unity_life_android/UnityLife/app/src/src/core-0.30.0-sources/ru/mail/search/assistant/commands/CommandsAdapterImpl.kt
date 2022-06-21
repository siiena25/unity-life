package ru.mail.search.assistant.commands

import ru.mail.search.assistant.commands.factory.PublicCommandsFactory

internal class CommandsAdapterImpl(
    override val phraseAdapter: CommandsPhraseAdapter,
    override val stateAdapter: CommandsStateAdapter,
    override val messagesAdapter: CommandsMessagesAdapter,
    override val statisticAdapter: CommandsStatisticAdapter,
    override val commandsFactory: PublicCommandsFactory
) : CommandsAdapter