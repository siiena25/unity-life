package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.AudioTrackPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.PlaybackLimitPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.TracksMsgPayload
import ru.mail.search.assistant.entities.audio.AudioTrack
import ru.mail.search.assistant.entities.audio.PlaybackLimit
import ru.mail.search.assistant.entities.message.MessageData

internal class TracksMsgPayloadConverter :
    PayloadGsonConverter<MessageData.Player.TracksMsg, TracksMsgPayload>() {

    override val type: String get() = MessageTypes.TRACKS

    private val kwsSkipIntervalsConverter = KwsSkipIntervalsConverter()
    private val audioSourceConverter = AudioSourceConverter()

    override fun payloadToPojo(payload: String): MessageData.Player.TracksMsg {
        return fromJson<TracksMsgPayload>(payload) {
            MessageData.Player.TracksMsg(
                tracks.map { track ->
                    val limit = track.playbackLimit?.let {
                        PlaybackLimit(it.type, it.count)
                    }
                    with(track) {
                        AudioTrack(
                            id = id,
                            artistName = artistName,
                            trackName = trackName,
                            coverUrl = coverUrl,
                            url = url,
                            seek = seek,
                            audioSource = audioSource?.let { audioSource ->
                                audioSourceConverter.fromPayload(
                                    audioSource,
                                    sourceType // backward compatibility
                                )
                            },
                            kwsSkipIntervals = kwsSkipIntervalsConverter.fromPayload(
                                kwsSkipIntervals
                            ),
                            duration = duration,
                            isHq = isHq,
                            playbackLimit = limit,
                            statFlags = statFlags
                        )
                    }
                }
            )
        }
    }

    override fun pojoToPayload(data: MessageData.Player.TracksMsg): String {
        return toJson(data) {
            TracksMsgPayload(
                playlist.map { track ->
                    val limitPayload =
                        track.playbackLimit?.let { PlaybackLimitPayload(it.type, it.limit) }
                    with(track) {
                        AudioTrackPayload(
                            id = id,
                            artistName = artistName,
                            trackName = trackName,
                            coverUrl = coverUrl,
                            url = url,
                            audioSource = audioSource?.let(audioSourceConverter::toPayload),
                            kwsSkipIntervals = kwsSkipIntervalsConverter.toPayload(kwsSkipIntervals),
                            duration = duration,
                            isHq = isHq,
                            playbackLimit = limitPayload,
                            seek = seek,
                            statFlags = statFlags
                        )
                    }
                }
            )
        }
    }
}