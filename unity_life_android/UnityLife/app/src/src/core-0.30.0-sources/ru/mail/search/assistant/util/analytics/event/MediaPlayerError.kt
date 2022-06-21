package ru.mail.search.assistant.util.analytics.event

import ru.mail.search.assistant.common.util.analytics.AnalyticsEvent

class MediaPlayerError(message: String?) : AnalyticsEvent("media_player_error") {

    init {
        message?.let { stringParams["error_message"] = message }
    }
}