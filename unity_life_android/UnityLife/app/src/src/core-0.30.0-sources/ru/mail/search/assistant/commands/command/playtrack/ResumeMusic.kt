package ru.mail.search.assistant.commands.command.playtrack

import kotlinx.coroutines.withContext
import ru.mail.search.assistant.commands.command.CancelableExecutableCommand
import ru.mail.search.assistant.commands.command.media.CommandsMusicController
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.MessagesRepository
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.util.Tag

internal class ResumeMusic(
    private val musicController: CommandsMusicController,
    private val messagesRepository: MessagesRepository,
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) : CancelableExecutableCommand<Unit>(logger = logger) {

    override suspend fun execute(context: ExecutionContext) {
        logger?.i(Tag.ASSISTANT_COMMAND, "Resume media")
        val messageId = getCurrentMessageId() ?: findLatestMessageId()
        messageId ?: return
        musicController.play(messageId)
    }

    private suspend fun getCurrentMessageId(): Long? {
        return withContext(poolDispatcher.main) {
            musicController.getPlayedMessageId()
        }
    }

    private fun findLatestMessageId(): Long? {
        return messagesRepository.findLast { message -> message.data is MessageData.Player }?.id
    }
}