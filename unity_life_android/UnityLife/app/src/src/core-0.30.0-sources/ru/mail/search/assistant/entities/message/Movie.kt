package ru.mail.search.assistant.entities.message

data class Movie(
    val title: String?,
    val genres: List<String>,
    val premierDate: String?,
    val posterUrl: String?,
    val url: String?
)