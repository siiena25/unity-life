package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.mailru.MailPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.mailru.SenderMailsPayload
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.entities.message.mailru.Mail

internal class MailRuSendersMailsPayloadConverter :
    PayloadGsonConverter<MessageData.MailRuSenderMails, SenderMailsPayload>() {

    override val type: String
        get() = MessageTypes.MAIL_RU_SENDER_MAILS

    override fun payloadToPojo(payload: String): MessageData.MailRuSenderMails {
        return fromJson<SenderMailsPayload>(payload) {
            MessageData.MailRuSenderMails(
                mails = mails.map { mailPayload ->
                    Mail(
                        type = mailPayload.type.toMailType(),
                        avatar = mailPayload.avatar,
                        senderName = mailPayload.senderName,
                        subject = mailPayload.subject,
                        content = mailPayload.content,
                        hasAttach = mailPayload.hasAttach,
                        event = mailPayload.event,
                        payload = mailPayload.payload,
                        date = mailPayload.date
                    )
                }
            )
        }
    }

    override fun pojoToPayload(data: MessageData.MailRuSenderMails): String {
        return toJson(data) {
            SenderMailsPayload(
                mails = mails.map { mailPayload ->
                    MailPayload(
                        type = mailPayload.type.toIntType(),
                        avatar = mailPayload.avatar,
                        senderName = mailPayload.senderName,
                        subject = mailPayload.subject,
                        content = mailPayload.content,
                        hasAttach = mailPayload.hasAttach,
                        event = mailPayload.event,
                        payload = mailPayload.payload,
                        date = mailPayload.date
                    )
                }
            )
        }
    }

    private fun Mail.Type.toIntType(): Int {
        return when (this) {
            Mail.Type.MAIL -> 1
            Mail.Type.NEWSLETTER -> 2
        }
    }

    private fun Int.toMailType(): Mail.Type {
        return when (this) {
            1 -> Mail.Type.MAIL
            2 -> Mail.Type.NEWSLETTER
            else -> throw IllegalArgumentException("Missing Mail type: $this")
        }
    }
}