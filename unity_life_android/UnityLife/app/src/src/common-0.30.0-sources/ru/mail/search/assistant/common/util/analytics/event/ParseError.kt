package ru.mail.search.assistant.common.util.analytics.event

import ru.mail.search.assistant.common.util.analytics.AnalyticsEvent

class ParseError(route: String) : AnalyticsEvent("parsing_error") {

    init {
        stringParams["route"] = route
    }
}