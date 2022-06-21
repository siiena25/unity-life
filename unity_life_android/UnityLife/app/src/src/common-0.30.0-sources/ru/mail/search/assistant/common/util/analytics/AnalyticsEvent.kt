package ru.mail.search.assistant.common.util.analytics

abstract class AnalyticsEvent(val name: String) {

    val stringParams = mutableMapOf<String, String>()
    val intParams = mutableMapOf<String, Int>()
    val longParams = mutableMapOf<String, Long>()

    fun isEmptyParams(): Boolean {
        return stringParams.isEmpty() && intParams.isEmpty() && longParams.isEmpty()
    }
}