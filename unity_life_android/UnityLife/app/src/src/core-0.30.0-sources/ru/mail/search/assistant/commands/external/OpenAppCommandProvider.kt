package ru.mail.search.assistant.commands.external

import ru.mail.search.assistant.StartIntentController
import ru.mail.search.assistant.commands.command.external.CheckApp
import ru.mail.search.assistant.commands.command.external.OpenApp
import ru.mail.search.assistant.commands.factory.PublicCommandsFactory
import ru.mail.search.assistant.commands.processor.ExecutableCommandData
import ru.mail.search.assistant.commands.processor.QueueType
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.entities.ServerCommand
import ru.mail.search.assistant.entities.external.CheckAppServerCommand
import ru.mail.search.assistant.entities.external.OpenAppServerCommand

internal class OpenAppCommandProvider(
    private val startIntentController: StartIntentController,
    private val publicCommandsFactory: PublicCommandsFactory,
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) : ExternalCommandDataProvider {

    override fun provideExecutableCommandData(
        serverCommand: ServerCommand.ExternalUserDefined
    ): ExecutableCommandData? {
        return when (serverCommand) {
            is CheckAppServerCommand -> {
                serverCommand.run {
                    ExecutableCommandData(
                        QueueType.SYNC,
                        CheckApp(
                            serverCommand.responseYes,
                            serverCommand.responseNo,
                            serverCommand.intents,
                            startIntentController.packageManagerChecker,
                            publicCommandsFactory,
                            logger
                        )

                    )
                }
            }

            is OpenAppServerCommand -> {
                serverCommand.run {
                    ExecutableCommandData(
                        QueueType.SYNC,
                        OpenApp(
                            appId,
                            path.orEmpty(),
                            fallbackUrl,
                            intents,
                            startIntentController,
                            poolDispatcher,
                            logger
                        )
                    )
                }
            }
            else -> null
        }
    }
}