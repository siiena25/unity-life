package ru.mail.search.assistant.commands.command.playtrack

import ru.mail.search.assistant.commands.factory.CommandsFactory
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.rtlog.RtLogDevicePhraseExtraDataEvent
import ru.mail.search.assistant.entities.audio.Tale
import ru.mail.search.assistant.entities.message.MessageData

internal class PlayTale(
    phraseId: String,
    private val playlist: List<Tale>,
    autoPlay: Boolean,
    trackNumber: Int,
    trackPosition: Float,
    commandsFactory: CommandsFactory,
    rtLogDevicePhraseExtraDataEvent: RtLogDevicePhraseExtraDataEvent,
    logger: Logger?
) : PlayMessage<MessageData.Player.TaleMsg>(
    phraseId,
    autoPlay,
    trackNumber,
    trackPosition,
    commandsFactory,
    rtLogDevicePhraseExtraDataEvent,
    logger
) {

    override fun getMessageData(): MessageData.Player.TaleMsg {
        return MessageData.Player.TaleMsg(playlist)
    }
}