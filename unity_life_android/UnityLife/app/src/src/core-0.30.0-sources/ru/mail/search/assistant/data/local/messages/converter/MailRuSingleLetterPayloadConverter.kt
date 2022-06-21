package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.mailru.LetterPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.mailru.SingleLetterPayload
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.entities.message.mailru.Letter

internal class MailRuSingleLetterPayloadConverter :
    PayloadGsonConverter<MessageData.MailRuSingleLetter, SingleLetterPayload>() {

    override val type: String
        get() = MessageTypes.MAIL_RU_SINGLE_LETTER

    override fun payloadToPojo(payload: String): MessageData.MailRuSingleLetter {
        return fromJson<SingleLetterPayload>(payload) {
            MessageData.MailRuSingleLetter(
                letter = letter.run {
                    Letter(avatar, senderName, subject, content, hasAttach, date)
                }
            )
        }
    }

    override fun pojoToPayload(data: MessageData.MailRuSingleLetter): String {
        return toJson(data) {
            SingleLetterPayload(
                letter = letter.run {
                    LetterPayload(avatar, senderName, subject, content, hasAttach, date)
                }
            )
        }
    }
}