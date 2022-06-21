package ru.mail.search.assistant.entities.message.mailru

data class Letter(
    val avatar: String?,
    val senderName: String?,
    val subject: String?,
    val content: String?,
    val hasAttach: Boolean,
    val date: Long
)