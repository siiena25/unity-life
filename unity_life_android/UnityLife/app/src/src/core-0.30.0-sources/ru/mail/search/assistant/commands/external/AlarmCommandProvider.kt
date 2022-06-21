package ru.mail.search.assistant.commands.external

import ru.mail.search.assistant.commands.command.external.CancelAlarm
import ru.mail.search.assistant.commands.command.external.SetAlarm
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutableCommandData
import ru.mail.search.assistant.commands.processor.QueueType
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.device.PlatformAction
import ru.mail.search.assistant.data.remote.parser.external.AlarmDataParser
import ru.mail.search.assistant.entities.ServerCommand
import ru.mail.search.assistant.entities.external.CancelAlarmServerCommand
import ru.mail.search.assistant.entities.external.SetAlarmServerCommand

internal class AlarmCommandProvider(
    private val platformAction: PlatformAction,
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) : ExternalCommandDataProvider {

    override fun provideExecutableCommandData(
        serverCommand: ServerCommand.ExternalUserDefined
    ): ExecutableCommandData? {
        return when (serverCommand) {
            is SetAlarmServerCommand -> sync(
                SetAlarm(
                    serverCommand.timestamp,
                    platformAction,
                    poolDispatcher,
                    logger
                )
            )

            is CancelAlarmServerCommand -> sync(
                CancelAlarm(
                    serverCommand.timestamp,
                    platformAction,
                    poolDispatcher,
                    logger
                )
            )
            else -> null
        }
    }

    val parser = AlarmDataParser()

    private fun sync(command: ExecutableCommand<*>) = ExecutableCommandData(QueueType.SYNC, command)
}