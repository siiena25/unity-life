package ru.mail.search.assistant.data.rtlog

import androidx.collection.SparseArrayCompat
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.mail.search.assistant.api.statistics.rtlog.RtLogRepository
import ru.mail.search.assistant.common.data.exception.AuthException
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.isCausedByPoorNetworkConnection
import ru.mail.search.assistant.util.Tag

internal class RtLogDevicePhraseExtraDataEvent(
    private val repository: RtLogRepository,
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) {

    private companion object {

        private const val EVENT_CODE = 1013
    }

    private val entities: SparseArrayCompat<EventEntity> = SparseArrayCompat()

    @Synchronized
    fun createPhrase(phraseRequestId: Int) {
        entities.put(phraseRequestId, EventEntity())
    }

    @Synchronized
    fun onVoicePhraseCreated(phraseRequestId: Int, phraseId: String) {
        getEntity(phraseRequestId)?.let { entity ->
            entity.phraseId = phraseId
            entity.phraseCreateTime = repository.getCurrentTime()
        }
    }

    @Synchronized
    fun onVoiceResultReceived(phraseRequestId: Int) {
        getEntity(phraseRequestId)?.resultReceiveTime = repository.getCurrentTime()
    }

    @Synchronized
    fun onResultRequested(phraseRequestId: Int) {
        getEntity(phraseRequestId)
            ?.takeIf { it.resultRequestTime == 0L }
            ?.resultRequestTime = repository.getCurrentTime()
    }

    @Synchronized
    fun onCommonResultReceived(phraseRequestId: Int, phraseId: String) {
        getEntity(phraseRequestId)
            ?.takeIf { it.phraseId == null }
            ?.let { entity ->
                val currentTime = repository.getCurrentTime()
                entity.phraseId = phraseId
                entity.phraseCreateTime = currentTime
                entity.resultReceiveTime = currentTime
            }
    }

    @Synchronized
    fun onStartTts(phraseRequestId: Int, streamId: String? = null) {
        getEntity(phraseRequestId)?.let { entity ->
            val currentTime = repository.getCurrentTime()
            entity.takeIf { it.ttsPlayTime == 0L }
                ?.ttsPlayTime = currentTime

            streamId?.let {
                entity.ttsStreams += TtsStreamInfo(streamId, currentTime)
            }
        }
    }

    @Synchronized
    fun onStartMedia(phraseRequestId: Int) {
        getEntity(phraseRequestId)
            ?.takeIf { it.soundPlayTime == 0L }
            ?.soundPlayTime = repository.getCurrentTime()
    }

    @Synchronized
    fun onStopTts(phraseRequestId: Int, streamId: String) {
        getEntity(phraseRequestId)
            ?.ttsStreams
            ?.firstOrNull { it.streamId == streamId }
            ?.apply { stopTimestamp = repository.getCurrentTime() }
    }

    fun onPhraseFinished(phraseRequestId: Int) {
        synchronized(this) {
            getEntity(phraseRequestId)
                ?.also { entities.remove(phraseRequestId) }
        }
            ?.let { phrase -> sendPhrase(phrase) }
    }

    private fun sendPhrase(entity: EventEntity) {
        val phraseId = entity.phraseId ?: run {
            logger?.w(Tag.RT_LOG, "Missing phrase id")
            return
        }
        val phraseCreateTime = entity.phraseCreateTime.takeIf { it > 0 }
            ?: run {
                logger?.w(Tag.RT_LOG, "Missing phrase create time")
                return
            }
        val resultRequestTime = entity.resultRequestTime.takeIf { it > 0 }
            ?: run {
                logger?.w(Tag.RT_LOG, "Missing result request time")
                return
            }
        val resultReceiveTime = entity.resultReceiveTime.takeIf { it > 0 }
            ?: run {
                logger?.w(Tag.RT_LOG, "Missing result receive time")
                return
            }
        val dataJson = JsonObject().apply {
            addProperty("phrase_id", phraseId)
            addProperty("phrase_created_ts", repository.formatTime(phraseCreateTime))
            addProperty("phrase_result_requested_ts", repository.formatTime(resultRequestTime))
            addProperty("phrase_result_received_ts", repository.formatTime(resultReceiveTime))
            entity.ttsPlayTime.takeIf { it > 0 }
                ?.let { timestamp ->
                    addProperty("phrase_first_tts_start_ts", repository.formatTime(timestamp))
                }
            entity.soundPlayTime.takeIf { it > 0 }
                ?.let { timestamp ->
                    addProperty("phrase_first_media_start_ts", repository.formatTime(timestamp))
                }
            entity.ttsStreams
                .filter { it.stopTimestamp >= it.startTimestamp }
                .takeIf { it.isNotEmpty() }
                ?.let { streams -> addTtsUserExperience(streams) }
        }
        sendEvent(phraseId, dataJson)
    }

    private fun getEntity(phraseRequestId: Int): EventEntity? {
        return entities[phraseRequestId]
            ?: run {
                logger?.w(Tag.RT_LOG, IllegalStateException("Missing current entity"))
                null
            }
    }

    private fun sendEvent(phraseId: String, data: JsonElement) {
        GlobalScope.launch(poolDispatcher.io) {
            runCatching {
                repository.send(EVENT_CODE, phraseId, properties = mapOf("data" to data))
            }
                .onFailure { error ->
                    if (!error.isCausedByPoorNetworkConnection() && error !is AuthException) {
                        val message = "Failed to send rt_log event"
                        logger?.e(Tag.RT_LOG, error, message)
                    }
                }
        }
    }

    private fun JsonObject.addTtsUserExperience(streams: List<TtsStreamInfo>) {
        val ttsUserExperience = JsonObject().apply {
            streams.forEach { stream ->
                val listenedMs = stream.stopTimestamp - stream.startTimestamp
                val streamInfo = JsonObject().apply {
                    addProperty("listened", listenedMs)
                }
                add(stream.streamId, streamInfo)
            }
        }
        add("tts_user_experience", ttsUserExperience)
    }

    private class EventEntity(
        var phraseId: String? = null,
        var phraseCreateTime: Long = 0,
        var resultRequestTime: Long = 0,
        var resultReceiveTime: Long = 0,
        var ttsPlayTime: Long = 0,
        var soundPlayTime: Long = 0,
        var ttsStreams: List<TtsStreamInfo> = emptyList(),
    )

    private class TtsStreamInfo(
        val streamId: String,
        val startTimestamp: Long,
        var stopTimestamp: Long = 0,
    )
}