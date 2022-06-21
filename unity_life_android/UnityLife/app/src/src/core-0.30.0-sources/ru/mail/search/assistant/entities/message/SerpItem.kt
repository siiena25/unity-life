package ru.mail.search.assistant.entities.message

data class SerpItem(
    val title: String,
    val text: String?,
    val shortUrl: String,
    val fullUrl: String
)