package ru.mail.search.assistant.entities.message

data class MovieSession(
    val title: String?,
    val genres: List<String>,
    val posterUrl: String?,
    val url: String?,
    val showType: MovieShowType,
    val sessions: List<String>
)