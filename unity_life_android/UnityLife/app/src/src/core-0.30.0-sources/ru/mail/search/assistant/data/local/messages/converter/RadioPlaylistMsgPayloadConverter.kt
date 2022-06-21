package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.RadioMsgPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.RadioPlaylistMsgPayload
import ru.mail.search.assistant.entities.audio.Radiostation
import ru.mail.search.assistant.entities.message.MessageData

internal class RadioPlaylistMsgPayloadConverter :
    PayloadGsonConverter<MessageData.Player.RadioMsg, RadioPlaylistMsgPayload>() {

    override val type: String get() = MessageTypes.RADIO_PLAYLIST

    private val kwsSkipIntervalsConverter = KwsSkipIntervalsConverter()
    private val audioSourceConverter = AudioSourceConverter()

    override fun payloadToPojo(payload: String): MessageData.Player.RadioMsg {
        return fromJson<RadioPlaylistMsgPayload>(payload) {
            val playlist = playlist.map { track ->
                Radiostation(
                    artist = track.artist,
                    title = track.title.orEmpty(),
                    coverUrl = track.coverUrl,
                    url = track.url,
                    audioSource = audioSourceConverter.fromPayload(track.audioSource),
                    kwsSkipIntervals = kwsSkipIntervalsConverter.fromPayload(track.kwsSkipIntervals)
                )
            }
            MessageData.Player.RadioMsg(playlist)
        }
    }

    override fun pojoToPayload(data: MessageData.Player.RadioMsg): String {
        return toJson(data) {
            val playlist = playlist.map { track ->
                RadioMsgPayload(
                    artist = track.artist,
                    title = track.title,
                    coverUrl = track.coverUrl,
                    url = track.url,
                    audioSource = audioSourceConverter.toPayload(track.audioSource),
                    kwsSkipIntervals = kwsSkipIntervalsConverter.toPayload(track.kwsSkipIntervals)
                )
            }
            RadioPlaylistMsgPayload(playlist)
        }
    }
}