package ru.mail.search.assistant.commands.command.playtrack

import ru.mail.search.assistant.commands.factory.CommandsFactory
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.rtlog.RtLogDevicePhraseExtraDataEvent
import ru.mail.search.assistant.entities.audio.Sound
import ru.mail.search.assistant.entities.message.MessageData

internal class PlaySound(
    phraseId: String,
    autoPlay: Boolean,
    private val playlist: List<Sound>,
    trackNumber: Int,
    trackPosition: Float,
    commandsFactory: CommandsFactory,
    rtLogDevicePhraseExtraDataEvent: RtLogDevicePhraseExtraDataEvent,
    logger: Logger?
) : PlayMessage<MessageData.Player.SoundMsg>(
    phraseId,
    autoPlay,
    trackNumber,
    trackPosition,
    commandsFactory,
    rtLogDevicePhraseExtraDataEvent,
    logger
) {

    override fun getMessageData(): MessageData.Player.SoundMsg {
        return MessageData.Player.SoundMsg(playlist)
    }
}