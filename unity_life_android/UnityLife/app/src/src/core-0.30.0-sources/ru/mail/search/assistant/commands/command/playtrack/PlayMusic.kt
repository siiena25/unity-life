package ru.mail.search.assistant.commands.command.playtrack

import ru.mail.search.assistant.commands.factory.CommandsFactory
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.rtlog.RtLogDevicePhraseExtraDataEvent
import ru.mail.search.assistant.entities.audio.AudioTrack
import ru.mail.search.assistant.entities.message.MessageData

internal class PlayMusic(
    phraseId: String,
    private val playlist: List<AudioTrack>,
    autoPlay: Boolean,
    trackNumber: Int,
    trackPosition: Float,
    commandsFactory: CommandsFactory,
    rtLogDevicePhraseExtraDataEvent: RtLogDevicePhraseExtraDataEvent,
    logger: Logger?
) : PlayMessage<MessageData.Player.TracksMsg>(
    phraseId,
    autoPlay,
    trackNumber,
    trackPosition,
    commandsFactory,
    rtLogDevicePhraseExtraDataEvent,
    logger
) {

    override fun getMessageData(): MessageData.Player.TracksMsg {
        return MessageData.Player.TracksMsg(playlist)
    }
}