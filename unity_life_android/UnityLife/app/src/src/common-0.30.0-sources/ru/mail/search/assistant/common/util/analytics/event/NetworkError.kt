package ru.mail.search.assistant.common.util.analytics.event

import ru.mail.search.assistant.common.util.analytics.AnalyticsEvent

class NetworkError(route: String, cause: String?) : AnalyticsEvent("network_error") {

    init {
        stringParams["route"] = route
        cause?.let { stringParams["cause"] = cause }
    }
}