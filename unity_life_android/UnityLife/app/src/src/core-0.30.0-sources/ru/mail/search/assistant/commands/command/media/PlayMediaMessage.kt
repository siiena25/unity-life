package ru.mail.search.assistant.commands.command.media

import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.util.Tag

internal class PlayMediaMessage(
    private val messageId: Long,
    private val autoPlay: Boolean,
    private val trackNumber: Int,
    private val trackPosition: Float,
    private val musicController: CommandsMusicController,
    private val logger: Logger?
) : ExecutableCommand<Unit> {

    override suspend fun execute(context: ExecutionContext) {
        logger?.i(Tag.ASSISTANT_COMMAND, "start playing media message")
        musicController.play(messageId, autoPlay, trackNumber, trackPosition)
    }
}