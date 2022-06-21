package ru.mail.search.assistant.entities.message.images

data class Image(
    val url: String,
    val width: Int?,
    val height: Int?,
    val ext: String?,
    val size: Long?
)