package ru.mail.search.assistant.entities

import java.util.*

data class RemindNotification(
    val id: String,
    val text: String,
    val timestamp: Date,
    val requestCode: Int
)