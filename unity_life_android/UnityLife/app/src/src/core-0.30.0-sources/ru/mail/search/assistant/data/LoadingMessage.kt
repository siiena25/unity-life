package ru.mail.search.assistant.data

import java.util.*

sealed class LoadingMessage {

    data class Show(val textSoFar: String, val uuid: UUID) : LoadingMessage()

    data class Hide(val uuid: UUID) : LoadingMessage()
}