package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.TaleMsgPayload
import ru.mail.search.assistant.entities.audio.Tale
import ru.mail.search.assistant.entities.message.MessageData

@Deprecated("Backward compatibility")
internal class TaleMsgPayloadConverter :
    PayloadGsonConverter<MessageData.Player.TaleMsg, TaleMsgPayload>() {

    override val type: String get() = MessageTypes.TALE

    private val kwsSkipIntervalsConverter = KwsSkipIntervalsConverter()
    private val audioSourceConverter = AudioSourceConverter()

    override fun payloadToPojo(payload: String): MessageData.Player.TaleMsg {
        return fromJson<TaleMsgPayload>(payload) {
            val tale = Tale(
                title = title,
                coverUrl = coverUrl,
                url = url,
                audioSource = audioSourceConverter.fromPayload(audioSource),
                seek = seek,
                kwsSkipIntervals = kwsSkipIntervalsConverter.fromPayload(kwsSkipIntervals)
            )
            MessageData.Player.TaleMsg(listOf(tale))
        }
    }

    override fun pojoToPayload(data: MessageData.Player.TaleMsg): String {
        return toJson(data) {
            val track = data.playlist.first()
            TaleMsgPayload(
                title = track.title,
                coverUrl = track.coverUrl,
                url = track.url,
                audioSource = audioSourceConverter.toPayload(track.audioSource),
                seek = track.seek,
                kwsSkipIntervals = kwsSkipIntervalsConverter.toPayload(track.kwsSkipIntervals)
            )
        }
    }
}