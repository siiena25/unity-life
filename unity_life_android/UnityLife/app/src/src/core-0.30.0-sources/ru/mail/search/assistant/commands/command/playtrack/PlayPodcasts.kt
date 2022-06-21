package ru.mail.search.assistant.commands.command.playtrack

import ru.mail.search.assistant.commands.factory.CommandsFactory
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.rtlog.RtLogDevicePhraseExtraDataEvent
import ru.mail.search.assistant.entities.audio.AudioTrack
import ru.mail.search.assistant.entities.message.MessageData

internal class PlayPodcasts(
    phraseId: String,
    autoPlay: Boolean,
    private val playlist: List<AudioTrack>,
    trackNumber: Int,
    trackPosition: Float,
    commandsFactory: CommandsFactory,
    rtLogDevicePhraseExtraDataEvent: RtLogDevicePhraseExtraDataEvent,
    logger: Logger?
) : PlayMessage<MessageData.Player.PodcastsMsg>(
    phraseId,
    autoPlay,
    trackNumber,
    trackPosition,
    commandsFactory,
    rtLogDevicePhraseExtraDataEvent,
    logger
) {

    override fun getMessageData(): MessageData.Player.PodcastsMsg {
        return MessageData.Player.PodcastsMsg(playlist)
    }
}