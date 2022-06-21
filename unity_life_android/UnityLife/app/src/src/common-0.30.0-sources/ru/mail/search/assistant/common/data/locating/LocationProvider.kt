package ru.mail.search.assistant.common.data.locating

interface LocationProvider {
    fun getLocation(): Location?
}