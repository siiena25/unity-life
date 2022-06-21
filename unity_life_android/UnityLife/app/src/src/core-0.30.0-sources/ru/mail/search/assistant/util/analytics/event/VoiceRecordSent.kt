package ru.mail.search.assistant.util.analytics.event

import ru.mail.search.assistant.common.util.analytics.AnalyticsEvent

class VoiceRecordSent(processingTime: Long) : AnalyticsEvent("voice_record_sent") {

    init {
        longParams["request_processing_time"] = processingTime
    }
}