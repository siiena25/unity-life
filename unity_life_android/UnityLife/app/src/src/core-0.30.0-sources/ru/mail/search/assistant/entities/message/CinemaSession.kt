package ru.mail.search.assistant.entities.message

data class CinemaSession(
    val title: String?,
    val address: String?,
    val url: String?,
    val distance: String?,
    val sessions: List<String>
)