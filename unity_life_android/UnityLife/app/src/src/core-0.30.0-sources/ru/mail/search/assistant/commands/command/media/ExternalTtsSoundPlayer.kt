package ru.mail.search.assistant.commands.command.media

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.mail.search.assistant.common.http.common.HttpMethod
import ru.mail.search.assistant.common.http.common.HttpRequest
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.data.rtlog.RtLogDevicePhraseExtraDataEvent
import ru.mail.search.assistant.media.wrapper.TtsPlayer

internal class ExternalTtsSoundPlayer(
    private val streamUrl: String,
    private val ttsPlayer: TtsPlayer,
    private val rtLogDevicePhraseExtraDataEvent: RtLogDevicePhraseExtraDataEvent,
    private val poolDispatcher: PoolDispatcher
) : SoundPlayer {

    override suspend fun play(requestId: Int): Flow<SoundPlayer.State> {
        rtLogDevicePhraseExtraDataEvent.onStartTts(requestId)
        val request = HttpRequest(streamUrl, HttpMethod.GET)
        return ttsPlayer.playStream(request).flowOn(poolDispatcher.main)
    }
}