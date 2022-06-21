package ru.mail.search.assistant.data.rtlog

import ru.mail.search.assistant.audition.sending.AudioRecordConfig
import ru.mail.search.assistant.common.util.TimeUtils
import ru.mail.search.assistant.data.ClientStateRepository
import ru.mail.search.assistant.voicemanager.VoiceManagerAnalytics

internal class VoiceManagerAnalyticsImpl(
    private val audioConfig: AudioRecordConfig,
    private val clientStateRepository: ClientStateRepository,
    private val rtLogDeviceChunksExtraDataEvent: RtLogDeviceChunksExtraDataEvent,
    private val rtLogDevicePhraseExtraDataEvent: RtLogDevicePhraseExtraDataEvent
) : VoiceManagerAnalytics {

    override fun onChunkSendingStarted(phraseId: String) {
        rtLogDeviceChunksExtraDataEvent.onChunkSendingStarted(phraseId)
    }

    override fun onChunkSendingFinished(phraseId: String) {
        rtLogDeviceChunksExtraDataEvent.onChunkSendingFinished(phraseId)
    }

    override fun createPhrase(phraseRequestId: Int) {
        rtLogDeviceChunksExtraDataEvent.createPhrase()
    }

    override fun onChunkRecordingStarted(time: Long?) {
        if (time == null) {
            rtLogDeviceChunksExtraDataEvent.onChunkRecordingStarted()
        } else {
            rtLogDeviceChunksExtraDataEvent.onChunkRecordingStarted(time)
        }
    }

    override fun onResultRequested(phraseRequestId: Int) {
        rtLogDevicePhraseExtraDataEvent.onResultRequested(phraseRequestId)
    }

    override fun onVoiceResultReceived(phraseRequestId: Int) {
        rtLogDevicePhraseExtraDataEvent.onVoiceResultReceived(phraseRequestId)
    }

    override fun onPhraseFinished(phraseRequestId: Int) {
        rtLogDeviceChunksExtraDataEvent.onPhraseFinished()
    }

    override fun onKeywordSpotted(keyword: ByteArray) {
        rtLogDeviceChunksExtraDataEvent.createPhrase()
        val currentTime = System.currentTimeMillis()
        val chunkStartTime = calculateChunkStartTime(currentTime, keyword.size, audioConfig)
        rtLogDeviceChunksExtraDataEvent.onKeywordChunkRecorded(chunkStartTime, currentTime)
        rtLogDeviceChunksExtraDataEvent.onChunkRecordingStarted(currentTime)
    }

    override fun onVoicePhraseCreated(phraseRequestId: Int, phraseId: String) {
        rtLogDeviceChunksExtraDataEvent.onPhraseCreated(phraseId)
        rtLogDevicePhraseExtraDataEvent.onVoicePhraseCreated(phraseRequestId, phraseId)
        clientStateRepository.onPhraseCreated(phraseRequestId, phraseId)
    }

    private fun calculateChunkStartTime(
        currentTime: Long,
        keywordSize: Int,
        audioConfig: AudioRecordConfig
    ): Long {
        val byteRate = audioConfig.getBytesPerSecond()
        val byteCount = keywordSize.toDouble()
        val keywordInSeconds = byteCount / byteRate
        val keywordInMillis = TimeUtils.secondToMillis(keywordInSeconds)
        return currentTime - keywordInMillis
    }
}