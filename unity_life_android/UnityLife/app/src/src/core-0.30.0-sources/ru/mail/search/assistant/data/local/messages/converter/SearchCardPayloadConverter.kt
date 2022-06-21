package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.SearchCardPayload
import ru.mail.search.assistant.entities.message.ActionTag
import ru.mail.search.assistant.entities.message.MessageData

@Deprecated("Backward compatibility, use ActionCardPayloadConverter")
internal class SearchCardPayloadConverter :
    PayloadGsonConverter<MessageData.ActionCard, SearchCardPayload>() {

    override val type: String get() = MessageTypes.SEARCH_CARD

    override fun payloadToPojo(payload: String): MessageData.ActionCard {
        return fromJson<SearchCardPayload>(payload) {
            MessageData.ActionCard(text, ActionTag.SEARCH, linkLabel, linkUrl)
        }
    }

    override fun pojoToPayload(data: MessageData.ActionCard): String {
        return toJson(data) {
            SearchCardPayload(text, linkLabel, linkUrl)
        }
    }
}