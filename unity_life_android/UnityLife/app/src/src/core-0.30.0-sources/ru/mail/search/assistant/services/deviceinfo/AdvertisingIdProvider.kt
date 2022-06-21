package ru.mail.search.assistant.services.deviceinfo

interface AdvertisingIdProvider {

    fun getAdvertisingId(): String?
}