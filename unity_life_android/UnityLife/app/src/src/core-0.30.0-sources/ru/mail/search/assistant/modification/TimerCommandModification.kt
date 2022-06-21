package ru.mail.search.assistant.modification

import ru.mail.search.assistant.Modification
import ru.mail.search.assistant.StartIntentController
import ru.mail.search.assistant.commands.CommandsAdapter
import ru.mail.search.assistant.commands.external.ExternalCommandDataProvider
import ru.mail.search.assistant.commands.external.TimerCommandProvider
import ru.mail.search.assistant.commands.factory.PublicCommandsFactory
import ru.mail.search.assistant.common.schedulers.PoolDispatcherFactory
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.device.AndroidDeviceAction
import ru.mail.search.assistant.data.remote.parser.ExternalDataParser
import ru.mail.search.assistant.data.remote.parser.external.TimerDataParser
import ru.mail.search.assistant.interactor.PhraseInteractor

class TimerCommandModification(
    private val startIntentController: StartIntentController,
    private val logger: Logger?
) : Modification {

    override fun getDataParsers(): List<ExternalDataParser> {
        return listOf(TimerDataParser())
    }

    override fun getCommandDataProvider(
        commandsFactory: PublicCommandsFactory,
        commandsAdapter: CommandsAdapter,
        phraseInteractor: PhraseInteractor
    ): ExternalCommandDataProvider? {
        return TimerCommandProvider(
            platformAction = AndroidDeviceAction(startIntentController),
            poolDispatcher = PoolDispatcherFactory().createPoolDispatcher(),
            logger = logger
        )
    }
}