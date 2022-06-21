package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.entities.message.MessageData

interface MessageDataPayloadConverter<T : MessageData> {

    val type: String

    fun payloadToPojo(payload: String): T

    fun pojoToPayload(data: T): String
}