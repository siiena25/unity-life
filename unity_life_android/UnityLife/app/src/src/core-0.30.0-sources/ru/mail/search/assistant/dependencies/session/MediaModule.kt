package ru.mail.search.assistant.dependencies.session

import android.content.Context
import ru.mail.search.assistant.media.AssistantBandwidthMeter
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.data.StreamsRepository
import ru.mail.search.assistant.media.AudioLevelInterceptor
import ru.mail.search.assistant.media.MediaPlayerFactory
import ru.mail.search.assistant.media.wrapper.StreamPlayerAdapter
import ru.mail.search.assistant.media.wrapper.TtsPlayer
import ru.mail.search.assistant.voiceservice.player.StreamPlayService

internal class MediaModule(
    context: Context,
    analytics: Analytics?,
    audioLevelInterceptor: AudioLevelInterceptor,
    bandwidthMeter: AssistantBandwidthMeter,
    logger: Logger?
) {

    private val mediaPlayerFactory =
        MediaPlayerFactory(context, analytics, audioLevelInterceptor, bandwidthMeter, logger)
    private val streamPlayService = StreamPlayService(mediaPlayerFactory)

    private val ttsStreamsRepository = StreamsRepository(streamPlayService)
    private val ttsStreamPlayer = StreamPlayerAdapter(ttsStreamsRepository)
    val ttsPlayer = TtsPlayer(ttsStreamPlayer)

    private val soundStreamsRepository = StreamsRepository(streamPlayService)
    val soundStreamPlayer = StreamPlayerAdapter(soundStreamsRepository)
}