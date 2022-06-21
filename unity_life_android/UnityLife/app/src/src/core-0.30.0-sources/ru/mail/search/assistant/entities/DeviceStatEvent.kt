package ru.mail.search.assistant.entities

interface DeviceStatEvent {
    val type: String
    val timestamp: Long
    val data: Map<String, String>
}