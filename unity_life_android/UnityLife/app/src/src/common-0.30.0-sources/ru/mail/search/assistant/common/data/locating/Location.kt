package ru.mail.search.assistant.common.data.locating

import java.util.*

data class Location(
    val date: Date,
    val accuracy: Float,
    val latitude: Double,
    val longitude: Double,
    val extra: String
)