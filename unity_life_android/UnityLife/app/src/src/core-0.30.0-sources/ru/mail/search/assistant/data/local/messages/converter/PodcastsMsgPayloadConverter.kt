package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.AudioTrackPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.PodcastsMsgPayload
import ru.mail.search.assistant.entities.audio.AudioTrack
import ru.mail.search.assistant.entities.message.MessageData

internal class PodcastsMsgPayloadConverter :
    PayloadGsonConverter<MessageData.Player.PodcastsMsg, PodcastsMsgPayload>() {

    override val type: String get() = MessageTypes.PODCASTS

    private val kwsSkipIntervalsConverter = KwsSkipIntervalsConverter()
    private val audioSourceConverter = AudioSourceConverter()

    override fun payloadToPojo(payload: String): MessageData.Player.PodcastsMsg {
        return fromJson<PodcastsMsgPayload>(payload) {
            MessageData.Player.PodcastsMsg(podcasts.map { track ->
                with(track) {
                    AudioTrack(
                        id = id,
                        artistName = artistName,
                        trackName = trackName,
                        coverUrl = coverUrl,
                        url = url,
                        audioSource = audioSource?.let { audioSource ->
                            audioSourceConverter.fromPayload(
                                audioSource,
                                sourceType // backward compatibility
                            )
                        },
                        seek = seek,
                        kwsSkipIntervals = kwsSkipIntervalsConverter.fromPayload(track.kwsSkipIntervals),
                        duration = duration,
                        isHq = isHq,
                        statFlags = statFlags
                    )
                }
            })
        }
    }

    override fun pojoToPayload(data: MessageData.Player.PodcastsMsg): String {
        return toJson(data) {
            PodcastsMsgPayload(playlist.map { track ->
                with(track) {
                    AudioTrackPayload(
                        id = id,
                        artistName = artistName,
                        trackName = trackName,
                        coverUrl = coverUrl,
                        seek = seek,
                        url = url,
                        audioSource = audioSource?.let(audioSourceConverter::toPayload),
                        kwsSkipIntervals = kwsSkipIntervalsConverter.toPayload(kwsSkipIntervals),
                        duration = duration,
                        isHq = isHq,
                        statFlags = statFlags
                    )
                }
            })
        }
    }
}