package ru.mail.search.assistant.commands.command.media.control

import ru.mail.search.assistant.commands.command.media.CommandsMusicController
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.util.Tag

internal class PlayBackward(
    private val musicController: CommandsMusicController,
    private val backwardStep: Long = DEFAULT_STEP_MILLIS,
    private val logger: Logger?
) : ExecutableCommand<Unit> {

    companion object {

        const val DEFAULT_STEP_MILLIS = 15000L
    }

    override val commandName: String = "PlayBackward"

    override suspend fun execute(context: ExecutionContext) {
        logger?.i(Tag.ASSISTANT_COMMAND, "start executing $commandName")
        musicController.fastBackward(backwardStep)
    }
}