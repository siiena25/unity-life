package ru.mail.search.assistant.util.analytics.event

import ru.mail.search.assistant.common.util.analytics.AnalyticsEvent

class VoiceRecordRecognized(processingTime: Long) : AnalyticsEvent("voice_record_recognized") {

    companion object {

        const val STORE_TAG_TIME_DIFFERENCE = "voice_record_recognized_time_difference"
    }

    init {
        longParams["request_processing_time"] = processingTime
    }
}