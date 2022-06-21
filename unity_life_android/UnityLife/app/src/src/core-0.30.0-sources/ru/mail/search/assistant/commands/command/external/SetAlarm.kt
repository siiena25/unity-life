package ru.mail.search.assistant.commands.command.external

import android.provider.AlarmClock
import kotlinx.coroutines.withContext
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.device.Payload
import ru.mail.search.assistant.data.device.PlatformAction
import ru.mail.search.assistant.interactor.UnixtimeToAlarm
import ru.mail.search.assistant.util.Tag

internal class SetAlarm(
    private val timestamp: Long,
    private val platformAction: PlatformAction,
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) : ExecutableCommand<Unit> {

    override val commandName: String = "SetAlarm"

    override suspend fun execute(context: ExecutionContext) {
        logger?.i(Tag.ASSISTANT_COMMAND, "start executing $commandName")
        val alarm = UnixtimeToAlarm().invoke(timestamp)
        withContext(poolDispatcher.main) {
            platformAction.doAction(
                AlarmClock.ACTION_SET_ALARM,
                mapOf(
                    AlarmClock.EXTRA_HOUR to Payload(alarm.hour),
                    AlarmClock.EXTRA_MINUTES to Payload(alarm.minutes),
                    AlarmClock.EXTRA_SKIP_UI to Payload(alarm.skipUi)
                ).also { data ->
                    alarm.daysOfWeek.takeIf { it.isNotEmpty() }?.let {
                        data.plus(AlarmClock.EXTRA_DAYS to Payload(it))
                    }
                }
            )
        }
    }
}