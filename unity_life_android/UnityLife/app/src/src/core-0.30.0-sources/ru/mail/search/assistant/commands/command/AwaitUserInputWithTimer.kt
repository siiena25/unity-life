package ru.mail.search.assistant.commands.command

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.commands.processor.model.CommandNotification
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.util.Tag

internal class AwaitUserInputWithTimer(
    private val time: Long,
    private val logger: Logger?
) : ExecutableCommand<Unit> {

    private val timerContext = Job()

    override suspend fun execute(context: ExecutionContext) {
        val phraseInfo = context.phrase.toShortString()
        logger?.i(Tag.ASSISTANT_COMMAND, "start executing AwaitUserInputWithTimer $phraseInfo")
        runCatching {
            withContext(timerContext) {
                delay(time)
            }
        }
    }

    override suspend fun notify(notification: CommandNotification): CommandNotification {
        when (notification) {
            CommandNotification.Silence,
            CommandNotification.Revoke,
            is CommandNotification.UserInput.Processed -> {
                timerContext.cancel()
            }
        }
        return notification
    }
}