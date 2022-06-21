package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.ActionCardPayload
import ru.mail.search.assistant.entities.message.MessageData

internal class ActionCardPayloadConverter :
    PayloadGsonConverter<MessageData.ActionCard, ActionCardPayload>() {

    override val type: String get() = MessageTypes.ACTION_CARD

    override fun payloadToPojo(payload: String): MessageData.ActionCard {
        return fromJson<ActionCardPayload>(payload) {
            MessageData.ActionCard(
                text = text,
                tag = tag,
                linkLabel = linkLabel,
                linkUrl = linkUrl
            )
        }
    }

    override fun pojoToPayload(data: MessageData.ActionCard): String {
        return toJson(data) {
            ActionCardPayload(
                text = text,
                tag = tag,
                linkLabel = linkLabel,
                linkUrl = linkUrl
            )
        }
    }
}