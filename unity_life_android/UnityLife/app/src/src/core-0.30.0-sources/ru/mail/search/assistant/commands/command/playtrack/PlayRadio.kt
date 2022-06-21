package ru.mail.search.assistant.commands.command.playtrack

import ru.mail.search.assistant.commands.factory.CommandsFactory
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.rtlog.RtLogDevicePhraseExtraDataEvent
import ru.mail.search.assistant.entities.audio.Radiostation
import ru.mail.search.assistant.entities.message.MessageData

internal class PlayRadio(
    phraseId: String,
    autoPlay: Boolean,
    private val playlist: List<Radiostation>,
    trackNumber: Int,
    commandsFactory: CommandsFactory,
    rtLogDevicePhraseExtraDataEvent: RtLogDevicePhraseExtraDataEvent,
    logger: Logger?
) : PlayMessage<MessageData.Player.RadioMsg>(
    phraseId,
    autoPlay,
    trackNumber,
    0f,
    commandsFactory,
    rtLogDevicePhraseExtraDataEvent,
    logger
) {

    override fun getMessageData(): MessageData.Player.RadioMsg {
        return MessageData.Player.RadioMsg(playlist)
    }
}