package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.MailcountCardPayload
import ru.mail.search.assistant.entities.message.ActionTag
import ru.mail.search.assistant.entities.message.MessageData

@Deprecated("Backward compatibility, use ActionCardPayloadConverter")
internal class MailcountPayloadConverter :
    PayloadGsonConverter<MessageData.ActionCard, MailcountCardPayload>() {

    override val type: String get() = MessageTypes.MAILCOUNT_CARD

    override fun payloadToPojo(payload: String): MessageData.ActionCard {
        return fromJson<MailcountCardPayload>(payload) {
            MessageData.ActionCard(
                text = text,
                tag = ActionTag.MAIL_COUNT,
                linkLabel = linkLabel,
                linkUrl = linkUrl
            )
        }
    }

    override fun pojoToPayload(data: MessageData.ActionCard): String {
        return toJson(data) {
            MailcountCardPayload(text, linkLabel, linkUrl)
        }
    }
}