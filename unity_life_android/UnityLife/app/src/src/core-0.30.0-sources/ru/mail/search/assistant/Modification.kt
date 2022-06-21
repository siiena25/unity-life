package ru.mail.search.assistant

import ru.mail.search.assistant.commands.CommandsAdapter
import ru.mail.search.assistant.commands.external.ExternalCommandDataProvider
import ru.mail.search.assistant.commands.factory.PublicCommandsFactory
import ru.mail.search.assistant.data.remote.parser.ExternalDataParser
import ru.mail.search.assistant.interactor.PhraseInteractor

/**
 * For common external command handling cases, use [ServerCommandModification] instead
 */
interface Modification {

    fun getDataParsers(): List<ExternalDataParser> {
        return emptyList()
    }

    fun getCommandDataProvider(
        commandsFactory: PublicCommandsFactory,
        commandsAdapter: CommandsAdapter,
        phraseInteractor: PhraseInteractor
    ): ExternalCommandDataProvider? {
        return null
    }
}