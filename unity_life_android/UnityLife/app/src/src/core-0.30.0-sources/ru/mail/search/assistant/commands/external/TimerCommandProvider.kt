package ru.mail.search.assistant.commands.external

import ru.mail.search.assistant.commands.command.external.CancelTimer
import ru.mail.search.assistant.commands.command.external.SetTimer
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutableCommandData
import ru.mail.search.assistant.commands.processor.QueueType
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.device.PlatformAction
import ru.mail.search.assistant.entities.ServerCommand
import ru.mail.search.assistant.entities.external.CancelTimerServerCommand
import ru.mail.search.assistant.entities.external.SetTimerServerCommand

internal class TimerCommandProvider(
    private val platformAction: PlatformAction,
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) : ExternalCommandDataProvider {

    override fun provideExecutableCommandData(
        serverCommand: ServerCommand.ExternalUserDefined
    ): ExecutableCommandData? {
        return when (serverCommand) {
            is SetTimerServerCommand -> sync(
                SetTimer(
                    serverCommand.timestampDelta,
                    platformAction,
                    poolDispatcher,
                    logger
                )
            )
            CancelTimerServerCommand -> sync(
                CancelTimer(platformAction, poolDispatcher, logger)
            )
            else -> null
        }
    }

    private fun sync(command: ExecutableCommand<*>) = ExecutableCommandData(QueueType.SYNC, command)
}