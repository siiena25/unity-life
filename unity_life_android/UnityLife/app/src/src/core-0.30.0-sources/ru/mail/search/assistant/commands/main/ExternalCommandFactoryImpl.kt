package ru.mail.search.assistant.commands.main

import ru.mail.search.assistant.commands.CommandsAdapter
import ru.mail.search.assistant.commands.command.UnknownCommand
import ru.mail.search.assistant.commands.factory.PublicCommandsFactory
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutableCommandData
import ru.mail.search.assistant.commands.processor.QueueType
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.entities.ServerCommand
import ru.mail.search.assistant.interactor.PhraseInteractor

internal class ExternalCommandFactoryImpl(
    externalCommandDataProvidersProvider: ExternalCommandDataProvidersProvider,
    commandsAdapter: CommandsAdapter,
    phraseInteractor: PhraseInteractor,
    commandsFactory: PublicCommandsFactory,
    private val logger: Logger?
) : ExternalCommandFactory {

    private val providers by lazy {
        externalCommandDataProvidersProvider
            .getProviders(commandsFactory, commandsAdapter, phraseInteractor)
    }

    override fun provideCommandData(
        queueType: QueueType,
        serverCommand: ServerCommand.ExternalUserDefined
    ): ExecutableCommandData {
        for (provider in providers) {
            val data: ExecutableCommand<*>? = provider.provideExecutableCommand(serverCommand)
            if (data != null) {
                return ExecutableCommandData(queueType, data)
            }
        }
        return ExecutableCommandData(queueType, UnknownCommand(logger))
    }
}