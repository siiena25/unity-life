package ru.mail.search.assistant.common.util.analytics.event

import ru.mail.search.assistant.common.util.analytics.AnalyticsEvent

class ServerError(route: String, code: Int, message: String) : AnalyticsEvent("backend_error") {

    init {
        stringParams["route"] = route
        intParams["code"] = code
        stringParams["message"] = message
    }
}