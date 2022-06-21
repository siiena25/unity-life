package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.PodcastMsgPayload
import ru.mail.search.assistant.entities.audio.Podcast
import ru.mail.search.assistant.entities.message.MessageData

@Deprecated("Backward compatibility")
internal class PodcastMsgPayloadConverter :
    PayloadGsonConverter<MessageData.Player.PodcastMsg, PodcastMsgPayload>() {

    override val type: String get() = MessageTypes.PODCAST

    private val kwsSkipIntervalsConverter = KwsSkipIntervalsConverter()
    private val audioSourceConverter = AudioSourceConverter()

    override fun payloadToPojo(payload: String): MessageData.Player.PodcastMsg {
        return fromJson<PodcastMsgPayload>(payload) {
            MessageData.Player.PodcastMsg(
                Podcast(
                    title = title,
                    coverUrl = coverUrl,
                    url = url,
                    audioSource = audioSourceConverter.fromPayload(audioSource),
                    kwsSkipIntervals = kwsSkipIntervalsConverter.fromPayload(kwsSkipIntervals)
                )
            )
        }
    }

    override fun pojoToPayload(data: MessageData.Player.PodcastMsg): String {
        return toJson(data) {
            with(podcast) {
                PodcastMsgPayload(
                    title = title,
                    coverUrl = coverUrl,
                    url = url,
                    audioSource = audioSourceConverter.toPayload(audioSource),
                    kwsSkipIntervals = kwsSkipIntervalsConverter.toPayload(kwsSkipIntervals)
                )
            }
        }
    }
}