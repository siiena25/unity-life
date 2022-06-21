package ru.mail.search.assistant.commands.command.media

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.mail.search.assistant.common.http.common.HttpMethod
import ru.mail.search.assistant.common.http.common.HttpRequest
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.data.rtlog.RtLogDevicePhraseExtraDataEvent
import ru.mail.search.assistant.media.MediaPlayer
import ru.mail.search.assistant.media.wrapper.StreamPlayerAdapter

internal class DialogSoundPlayer(
    private val url: String,
    private val streamPlayer: StreamPlayerAdapter,
    private val rtLogDevicePhraseExtraDataEvent: RtLogDevicePhraseExtraDataEvent,
    private val poolDispatcher: PoolDispatcher
) : SoundPlayer {

    override suspend fun play(requestId: Int): Flow<SoundPlayer.State> {
        rtLogDevicePhraseExtraDataEvent.onStartMedia(requestId)
        val request = HttpRequest(url, HttpMethod.GET)
        val params = StreamPlayerAdapter.Params(request, MediaPlayer.Format.DEFAULT)
        return streamPlayer.play(params).flowOn(poolDispatcher.main)
    }
}