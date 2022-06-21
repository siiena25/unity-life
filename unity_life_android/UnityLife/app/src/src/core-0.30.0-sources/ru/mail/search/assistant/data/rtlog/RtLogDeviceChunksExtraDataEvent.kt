package ru.mail.search.assistant.data.rtlog

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.mail.search.assistant.api.statistics.rtlog.RtLogRepository
import ru.mail.search.assistant.common.data.exception.AuthException
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.isCausedByPoorNetworkConnection
import ru.mail.search.assistant.services.deviceinfo.FeatureProvider
import ru.mail.search.assistant.util.Tag

internal class RtLogDeviceChunksExtraDataEvent(
    private val repository: RtLogRepository,
    private val featureProvider: FeatureProvider?,
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) {

    private companion object {

        private const val EVENT_CODE = 1011
    }

    private var currentPhrase: PhraseData? = null

    @Synchronized
    fun createPhrase() {
        currentPhrase = PhraseData()
    }

    @Synchronized
    fun onPhraseCreated(phraseId: String) {
        getCurrentPhrase()?.phraseId = phraseId
    }

    @Synchronized
    fun onKeywordChunkRecorded(startRecording: Long, finishRecording: Long) {
        val phrase = getCurrentPhrase() ?: return
        val chunk = ChunkData(
            startRecording = startRecording,
            finishRecording = finishRecording,
            phase = Phase.READY
        )
        phrase.queue.add(0, chunk)

        val message = "Keyword chunk recorded: " +
                "start = $startRecording, " +
                "finish = $finishRecording, " +
                "total = ${finishRecording - startRecording}ms"
        logger?.d(Tag.RT_LOG, message)
    }

    @Synchronized
    fun onChunkRecordingStarted(time: Long = repository.getCurrentTime()) {
        val phrase = getCurrentPhrase() ?: return
        val chunk = ChunkData()
        chunk.startRecording = time
        phrase.queue.add(0, chunk)
    }

    @Synchronized
    fun onChunkRecordingFinished() {
        val chunk = findNextChunk(Phase.RECORDING) ?: return
        chunk.finishRecording = repository.getCurrentTime()
        chunk.phase = Phase.READY
    }

    @Synchronized
    fun onChunkSendingStarted(phraseId: String) {
        val phrase = getCurrentPhrase() ?: return
        if (phrase.phraseId != phraseId) return
        val chunk = findNextChunk(phrase, Phase.READY) ?: return
        chunk.startSending = repository.getCurrentTime()
        chunk.phase = Phase.SENDING
    }

    @Synchronized
    fun onChunkSendingFinished(phraseId: String) {
        val phrase = getCurrentPhrase() ?: return
        if (phrase.phraseId != phraseId) return
        val chunk = findNextChunk(phrase, Phase.SENDING) ?: return
        phrase.queue.remove(chunk)
        chunk.finishSending = repository.getCurrentTime()
        phrase.chunks.add(chunk)
    }

    fun onPhraseFinished() {
        synchronized(this) { getCurrentPhraseAndReset() }
            ?.let { phrase -> sendPhrase(phrase) }
    }

    private fun findNextChunk(phase: Phase): ChunkData? {
        val phrase = getCurrentPhrase() ?: return null
        return findNextChunk(phrase, phase)
    }

    private fun findNextChunk(phrase: PhraseData, phase: Phase): ChunkData? {
        val chunk = phrase.queue.firstOrNull { chunk -> chunk.phase == phase }
        if (chunk == null) {
            logger?.e(Tag.RT_LOG, IllegalStateException("Missing chunk with phase=${phase.name}"))
        }
        return chunk
    }

    private fun sendPhrase(phrase: PhraseData) {
        val phraseId = phrase.phraseId ?: run {
            logger?.w(Tag.RT_LOG, "Missing phrase id")
            return
        }
        val phraseJson = JsonObject()
        phrase.chunks.forEachIndexed { index, chunk ->
            val chunkJson = JsonObject()
            val metaJson = JsonObject().apply {
                addProperty("chunk_start_ts", repository.formatTime(chunk.startRecording))
                addProperty("chunk_finish_ts", repository.formatTime(chunk.finishRecording))
                addProperty("chunk_start_sending_ts", repository.formatTime(chunk.startSending))
                addProperty("chunk_sent_ts", repository.formatTime(chunk.finishSending))
            }
            chunkJson.add("meta", metaJson)
            phraseJson.add(index.toString(), chunkJson)
        }
        if (featureProvider?.isRtLogDeviceChunksEnabled == true) {
            sendEvent(phraseId, phraseJson)
        }
    }

    private fun getCurrentPhrase(): PhraseData? {
        return currentPhrase
            ?: run {
                logger?.w(Tag.RT_LOG, IllegalStateException("Missing current phrase"))
                null
            }
    }

    private fun getCurrentPhraseAndReset(): PhraseData? {
        return getCurrentPhrase()?.also { currentPhrase = null }
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

    private class PhraseData(
        var phraseId: String? = null,
        val chunks: ArrayList<ChunkData> = ArrayList(),
        val queue: ArrayList<ChunkData> = ArrayList()
    )

    private class ChunkData(
        var phase: Phase = Phase.RECORDING,
        var startRecording: Long = 0,
        var finishRecording: Long = 0,
        var startSending: Long = 0,
        var finishSending: Long = 0
    )

    private enum class Phase {

        RECORDING, READY, SENDING
    }
}