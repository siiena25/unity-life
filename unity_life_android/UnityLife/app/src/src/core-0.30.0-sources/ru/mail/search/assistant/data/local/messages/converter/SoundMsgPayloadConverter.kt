package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.SoundMsgPayload
import ru.mail.search.assistant.entities.audio.Sound
import ru.mail.search.assistant.entities.message.MessageData

@Deprecated("Backward compatibility")
internal class SoundMsgPayloadConverter :
    PayloadGsonConverter<MessageData.Player.SoundMsg, SoundMsgPayload>() {

    override val type: String get() = MessageTypes.SOUND

    private val kwsSkipIntervalsConverter = KwsSkipIntervalsConverter()
    private val audioSourceConverter = AudioSourceConverter()

    override fun payloadToPojo(payload: String): MessageData.Player.SoundMsg {
        return fromJson<SoundMsgPayload>(payload) {
            val sound = Sound(
                name = name,
                url = url,
                audioSource = audioSourceConverter.fromPayload(audioSource),
                kwsSkipIntervals = kwsSkipIntervalsConverter.fromPayload(kwsSkipIntervals)
            )
            MessageData.Player.SoundMsg(listOf(sound))
        }
    }

    override fun pojoToPayload(data: MessageData.Player.SoundMsg): String {
        val track = data.playlist.first()
        return toJson(data) {
            SoundMsgPayload(
                name = track.name,
                url = track.url,
                audioSource = audioSourceConverter.toPayload(track.audioSource),
                kwsSkipIntervals = kwsSkipIntervalsConverter.toPayload(track.kwsSkipIntervals)
            )
        }
    }
}