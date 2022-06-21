package ru.mail.search.assistant.commands.command.media.control

import ru.mail.search.assistant.commands.command.media.CommandsMusicController
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.util.Tag

internal class PlayRewind(
    private val musicController: CommandsMusicController,
    private val playlistPosition: Int,
    private val elapsed: Float,
    private val logger: Logger?
) : ExecutableCommand<Unit> {

    override val commandName: String = "PlayRewind"

    override suspend fun execute(context: ExecutionContext) {
        logger?.i(Tag.ASSISTANT_COMMAND, "start executing $commandName")
        musicController.rewind(playlistPosition, elapsed)
    }
}