package ru.mail.search.assistant

import com.google.gson.JsonObject
import ru.mail.search.assistant.commands.CommandsAdapter
import ru.mail.search.assistant.commands.external.ExternalCommandDataProvider
import ru.mail.search.assistant.commands.factory.PublicCommandsFactory
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.data.remote.parser.ExternalDataParser
import ru.mail.search.assistant.entities.ServerCommand
import ru.mail.search.assistant.interactor.PhraseInteractor

class ServerCommandModification(
    private val handler: ServerCommandHandler
) : Modification {

    override fun getDataParsers(): List<ExternalDataParser> {
        return listOf(DataParser())
    }

    override fun getCommandDataProvider(
        commandsFactory: PublicCommandsFactory,
        commandsAdapter: CommandsAdapter,
        phraseInteractor: PhraseInteractor
    ): ExternalCommandDataProvider {
        return CommandDataProvider(commandsAdapter)
    }

    private inner class DataParser : ExternalDataParser {

        override fun getCommandName(): String = handler.getCommandType()

        override fun parse(json: JsonObject): ServerCommand.ExternalUserDefined {
            return CommonServerCommand(json)
        }
    }

    private class CommonServerCommand(val json: JsonObject) : ServerCommand.ExternalUserDefined()

    private inner class CommandDataProvider(
        private val commandsAdapter: CommandsAdapter
    ) : ExternalCommandDataProvider {

        override fun provideExecutableCommand(
            serverCommand: ServerCommand.ExternalUserDefined
        ): ExecutableCommand<*>? {
            return if (serverCommand is CommonServerCommand) {
                return handler.createCommand(commandsAdapter, serverCommand.json)
            } else {
                null
            }
        }
    }
}