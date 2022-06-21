package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.RadioMsgPayload
import ru.mail.search.assistant.entities.audio.Radiostation
import ru.mail.search.assistant.entities.message.MessageData

@Deprecated("Backward compatibility")
internal class RadioMsgPayloadConverter :
    PayloadGsonConverter<MessageData.Player.RadioMsg, RadioMsgPayload>() {

    override val type: String get() = MessageTypes.RADIO

    private val kwsSkipIntervalsConverter = KwsSkipIntervalsConverter()
    private val audioSourceConverter = AudioSourceConverter()

    override fun payloadToPojo(payload: String): MessageData.Player.RadioMsg {
        return fromJson<RadioMsgPayload>(payload) {
            val track = Radiostation(
                artist = artist,
                title = title.orEmpty(),
                coverUrl = coverUrl,
                url = url,
                audioSource = audioSourceConverter.fromPayload(audioSource),
                kwsSkipIntervals = kwsSkipIntervalsConverter.fromPayload(kwsSkipIntervals)
            )
            MessageData.Player.RadioMsg(listOf(track))
        }
    }

    override fun pojoToPayload(data: MessageData.Player.RadioMsg): String {
        return toJson(data) {
            val track = playlist.first()
            RadioMsgPayload(
                artist = track.artist,
                title = track.title,
                coverUrl = track.coverUrl,
                url = track.url,
                audioSource = audioSourceConverter.toPayload(track.audioSource),
                kwsSkipIntervals = kwsSkipIntervalsConverter.toPayload(track.kwsSkipIntervals)
            )
        }
    }
}