package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.MiniAppPayload
import ru.mail.search.assistant.entities.message.MessageData

internal class MiniAppPayloadConverter :
    PayloadGsonConverter<MessageData.MiniApp, MiniAppPayload>() {

    override val type: String get() = MessageTypes.MINI_APP

    override fun payloadToPojo(payload: String): MessageData.MiniApp {
        return fromJson<MiniAppPayload>(payload) {
            MessageData.MiniApp(title, text, imageUrl, url)
        }
    }

    override fun pojoToPayload(data: MessageData.MiniApp): String {
        return toJson(data) {
            MiniAppPayload(title, text, imageUrl, url)
        }
    }
}