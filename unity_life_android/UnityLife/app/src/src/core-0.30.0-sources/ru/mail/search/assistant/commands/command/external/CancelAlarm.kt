package ru.mail.search.assistant.commands.command.external

import android.provider.AlarmClock
import kotlinx.coroutines.withContext
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.device.Payload
import ru.mail.search.assistant.data.device.PlatformAction
import ru.mail.search.assistant.util.Tag

internal class CancelAlarm(
    private val timestamp: Long,
    private val platformAction: PlatformAction,
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) : ExecutableCommand<Unit> {

    override val commandName: String = "CancelAlarm"

    override suspend fun execute(context: ExecutionContext) {
        logger?.i(Tag.ASSISTANT_COMMAND, "start executing $commandName")
        withContext(poolDispatcher.main) {
            platformAction.doAction(
                AlarmClock.ACTION_SHOW_ALARMS,
                mapOf(
                    AlarmClock.EXTRA_SKIP_UI to Payload(false)
                )
            )
        }
    }
}