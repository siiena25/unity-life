package ru.mail.search.assistant.util.analytics.event

import ru.mail.search.assistant.common.util.analytics.AnalyticsEvent

data class SuggestAction(val position: Int) : AnalyticsEvent("Suggest_Action") {
    init {
        intParams["position"] = position
    }
}