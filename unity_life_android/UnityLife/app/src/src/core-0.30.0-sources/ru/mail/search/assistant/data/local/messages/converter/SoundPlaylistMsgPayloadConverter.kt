package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.SoundMsgPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.SoundPlaylistMsgPayload
import ru.mail.search.assistant.entities.audio.Sound
import ru.mail.search.assistant.entities.message.MessageData

internal class SoundPlaylistMsgPayloadConverter :
    PayloadGsonConverter<MessageData.Player.SoundMsg, SoundPlaylistMsgPayload>() {

    override val type: String get() = MessageTypes.SOUND_PLAYLIST

    private val kwsSkipIntervalsConverter = KwsSkipIntervalsConverter()
    private val audioSourceConverter = AudioSourceConverter()

    override fun payloadToPojo(payload: String): MessageData.Player.SoundMsg {
        return fromJson<SoundPlaylistMsgPayload>(payload) {
            val playlist = playlist.map { track ->
                Sound(
                    name = track.name,
                    url = track.url,
                    audioSource = audioSourceConverter.fromPayload(track.audioSource),
                    kwsSkipIntervals = kwsSkipIntervalsConverter.fromPayload(track.kwsSkipIntervals)
                )
            }
            MessageData.Player.SoundMsg(playlist)
        }
    }

    override fun pojoToPayload(data: MessageData.Player.SoundMsg): String {
        return toJson(data) {
            val playlist = data.playlist.map { track ->
                SoundMsgPayload(
                    name = track.name,
                    url = track.url,
                    audioSource = audioSourceConverter.toPayload(track.audioSource),
                    kwsSkipIntervals = kwsSkipIntervalsConverter.toPayload(track.kwsSkipIntervals)
                )
            }
            SoundPlaylistMsgPayload(playlist)
        }
    }
}