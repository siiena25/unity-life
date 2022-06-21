package ru.mail.search.assistant.commands.command.playtrack

import ru.mail.search.assistant.commands.command.CancelableExecutableCommand
import ru.mail.search.assistant.commands.factory.CommandsFactory
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.rtlog.RtLogDevicePhraseExtraDataEvent
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.util.Tag

internal abstract class PlayMessage<T : MessageData.Player>(
    private val phraseId: String,
    private val autoPlay: Boolean,
    private val trackNumber: Int,
    private val trackPosition: Float,
    private val commandsFactory: CommandsFactory,
    private val rtLogDevicePhraseExtraDataEvent: RtLogDevicePhraseExtraDataEvent,
    private val logger: Logger?
) : CancelableExecutableCommand<Unit>(logger = logger) {

    open val isBlocking: Boolean get() = false

    override suspend fun execute(context: ExecutionContext) {
        logger?.i(Tag.ASSISTANT_COMMAND, "start playing message")
        val messageData = getMessageData()
        val addCommand = commandsFactory.addMessage(phraseId, messageData)
        val message = context.async(addCommand).await()
        if (!isCancelled) {
            rtLogDevicePhraseExtraDataEvent.onStartMedia(context.phrase.requestId)
            val playCommand =
                commandsFactory.playMediaMessage(message.id, autoPlay, trackNumber, trackPosition)
            val execution = context.async(playCommand)
            if (isBlocking) {
                execution.await()
            }
        }
    }

    protected abstract fun getMessageData(): T
}