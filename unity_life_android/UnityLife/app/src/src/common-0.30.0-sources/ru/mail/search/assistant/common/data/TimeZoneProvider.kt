package ru.mail.search.assistant.common.data

import java.util.*

class TimeZoneProvider {
    fun getCurrentTimezone(): String {
        return TimeZone.getDefault().id
    }
}