package ru.mail.search.assistant.commands.command.media

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.withContext
import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider
import ru.mail.search.assistant.common.data.SplitExperimentParamProvider
import ru.mail.search.assistant.common.http.assistant.Credentials
import ru.mail.search.assistant.common.http.common.HttpRequest
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.data.remote.RemoteDataSource
import ru.mail.search.assistant.data.rtlog.RtLogDevicePhraseExtraDataEvent
import ru.mail.search.assistant.media.wrapper.TtsPlayer

internal class InternalTtsSoundPlayer(
    private val phraseId: String,
    private val streamId: String,
    private val ttsPlayer: TtsPlayer,
    private val rtLogDevicePhraseExtraDataEvent: RtLogDevicePhraseExtraDataEvent,
    private val sessionProvider: SessionCredentialsProvider,
    private val remoteDataSource: RemoteDataSource,
    private val splitExperimentParamProvider: SplitExperimentParamProvider?,
    private val poolDispatcher: PoolDispatcher
) : SoundPlayer {

    override suspend fun play(requestId: Int): Flow<SoundPlayer.State> {
        rtLogDevicePhraseExtraDataEvent.onStartTts(requestId, streamId)
        val streamUrl = getStream(getCredentials(), phraseId, streamId)
        return ttsPlayer.playStream(streamUrl)
            .flowOn(poolDispatcher.main)
            .onCompletion { rtLogDevicePhraseExtraDataEvent.onStopTts(requestId, streamId) }
    }

    private suspend fun getCredentials(): Credentials {
        return withContext(poolDispatcher.io) {
            sessionProvider.getCredentials()
        }
    }

    private fun getStream(
        credentials: Credentials,
        phraseId: String,
        streamId: String
    ): HttpRequest {
        val params = hashMapOf(
            "stream_id" to streamId,
            "phrase_id" to phraseId
        ).apply {
            splitExperimentParamProvider?.provideSplitExperimentParam()?.also {
                put("splits", it)
            }
        }
        return remoteDataSource.getSessionUrl("stream", credentials, params)
    }
}