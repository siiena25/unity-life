package ru.mail.search.assistant.common.util.analytics

abstract class Analytics {

    val store = AnalyticsStore()

    abstract fun log(event: AnalyticsEvent)

    abstract fun getCurrentTime(): Long
}