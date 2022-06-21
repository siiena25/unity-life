package ru.mail.search.assistant.data.rtlog

import ru.mail.search.assistant.audition.AuditionAnalytics

internal class AuditionAnalyticsImpl(
    private val rtLogDeviceChunksExtraDataEvent: RtLogDeviceChunksExtraDataEvent
) : AuditionAnalytics {

    override fun onChunkRecordingFinished() {
        rtLogDeviceChunksExtraDataEvent.onChunkRecordingFinished()
        rtLogDeviceChunksExtraDataEvent.onChunkRecordingStarted()
    }
}