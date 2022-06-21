package ru.mail.search.assistant.modification

import com.google.gson.Gson
import ru.mail.search.assistant.Assistant
import ru.mail.search.assistant.Modification
import ru.mail.search.assistant.StartIntentController
import ru.mail.search.assistant.commands.CommandsAdapter
import ru.mail.search.assistant.commands.external.ExternalCommandDataProvider
import ru.mail.search.assistant.commands.external.OpenAppCommandProvider
import ru.mail.search.assistant.commands.factory.PublicCommandsFactory
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.remote.parser.ExternalDataParser
import ru.mail.search.assistant.data.remote.parser.external.CheckAppDataParser
import ru.mail.search.assistant.data.remote.parser.external.OpenAppDataParser
import ru.mail.search.assistant.interactor.PhraseInteractor

class OpenAppCommandModification(
    private val startIntentController: StartIntentController,
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) : Modification {

    companion object {

        val CAPABILITIES = mapOf(
            Assistant.Capability.OPEN_APP_V2 to true
        )
    }

    override fun getDataParsers(): List<ExternalDataParser> {
        return listOf(OpenAppDataParser(Gson()), CheckAppDataParser(Gson()))
    }

    override fun getCommandDataProvider(
        commandsFactory: PublicCommandsFactory,
        commandsAdapter: CommandsAdapter,
        phraseInteractor: PhraseInteractor
    ): ExternalCommandDataProvider? {
        return OpenAppCommandProvider(
            startIntentController,
            commandsFactory,
            poolDispatcher,
            logger
        )
    }
}