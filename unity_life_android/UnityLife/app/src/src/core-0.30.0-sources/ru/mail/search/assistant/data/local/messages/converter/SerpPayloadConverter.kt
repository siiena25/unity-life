package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.SerpItemPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.SerpPayload
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.entities.message.SerpItem

internal class SerpPayloadConverter : PayloadGsonConverter<MessageData.SerpCard, SerpPayload>() {

    override val type: String
        get() = MessageTypes.SERP_CARD

    override fun payloadToPojo(payload: String): MessageData.SerpCard {
        return fromJson<SerpPayload>(payload) {
            MessageData.SerpCard(
                items.map { SerpItem(it.title, it.text, it.shortUrl, it.fullUrl) },
                searchUrl
            )
        }
    }

    override fun pojoToPayload(data: MessageData.SerpCard): String {
        return toJson(data) {
            SerpPayload(
                items.map { SerpItemPayload(it.title, it.text, it.shortUrl, it.fullUrl) },
                searchUrl
            )
        }
    }
}