package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.TaleMsgPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.TalePlaylistMsgPayload
import ru.mail.search.assistant.entities.audio.Tale
import ru.mail.search.assistant.entities.message.MessageData

internal class TalePlaylistMsgPayloadConverter :
    PayloadGsonConverter<MessageData.Player.TaleMsg, TalePlaylistMsgPayload>() {

    override val type: String get() = MessageTypes.TALE_PLAYLIST

    private val kwsSkipIntervalsConverter = KwsSkipIntervalsConverter()
    private val audioSourceConverter = AudioSourceConverter()

    override fun payloadToPojo(payload: String): MessageData.Player.TaleMsg {
        return fromJson<TalePlaylistMsgPayload>(payload) {
            val playlist = playlist.map { track ->
                Tale(
                    title = track.title,
                    coverUrl = track.coverUrl,
                    url = track.url,
                    audioSource = audioSourceConverter.fromPayload(track.audioSource),
                    seek = track.seek,
                    kwsSkipIntervals = kwsSkipIntervalsConverter.fromPayload(track.kwsSkipIntervals)
                )
            }
            MessageData.Player.TaleMsg(playlist)
        }
    }

    override fun pojoToPayload(data: MessageData.Player.TaleMsg): String {
        return toJson(data) {
            val playlist = data.playlist.map { track ->
                TaleMsgPayload(
                    title = track.title,
                    coverUrl = track.coverUrl,
                    url = track.url,
                    audioSource = audioSourceConverter.toPayload(track.audioSource),
                    seek = track.seek,
                    kwsSkipIntervals = kwsSkipIntervalsConverter.toPayload(track.kwsSkipIntervals)
                )
            }
            TalePlaylistMsgPayload(playlist)
        }
    }
}