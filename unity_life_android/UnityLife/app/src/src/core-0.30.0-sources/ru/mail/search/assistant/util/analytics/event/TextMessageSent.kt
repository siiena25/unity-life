package ru.mail.search.assistant.util.analytics.event

import ru.mail.search.assistant.common.util.analytics.AnalyticsEvent

internal class TextMessageSent(processingTime: Long) : AnalyticsEvent("text_message_sent") {

    init {
        longParams["request_processing_time"] = processingTime
    }
}