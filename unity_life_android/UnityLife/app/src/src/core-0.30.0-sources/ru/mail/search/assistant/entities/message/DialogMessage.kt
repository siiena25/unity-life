package ru.mail.search.assistant.entities.message

import java.util.*

data class DialogMessage(
    val id: Long,
    val phraseId: String,
    val date: Date,
    val data: MessageData
)