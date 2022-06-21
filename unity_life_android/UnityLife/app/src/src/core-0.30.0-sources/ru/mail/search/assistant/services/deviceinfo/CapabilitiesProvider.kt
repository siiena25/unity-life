package ru.mail.search.assistant.services.deviceinfo

interface CapabilitiesProvider {

    val capabilities: Map<String, Boolean>
}