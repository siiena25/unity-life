package ru.mail.search.assistant.data

import ru.mail.search.assistant.entities.message.DialogMessage

sealed class MessageOps {

    data class Add(val message: DialogMessage) : MessageOps()

    object Clear : MessageOps()
}