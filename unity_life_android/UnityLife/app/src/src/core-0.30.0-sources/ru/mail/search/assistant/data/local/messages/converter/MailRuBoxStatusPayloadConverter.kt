package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.mailru.SenderPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.mailru.MailBoxMoreSendersPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.mailru.MailBoxPayload
import ru.mail.search.assistant.data.local.messages.converter.payload.mailru.MailBoxStatusPayload
import ru.mail.search.assistant.entities.message.MessageData
import ru.mail.search.assistant.entities.message.mailru.MailBox
import ru.mail.search.assistant.entities.message.mailru.Sender
import ru.mail.search.assistant.entities.message.mailru.MailBoxMoreSenders

internal class MailRuBoxStatusPayloadConverter :
    PayloadGsonConverter<MessageData.MailRuBoxStatus, MailBoxStatusPayload>() {

    override val type: String get() = MessageTypes.MAIL_RU_BOX_STATUS

    override fun payloadToPojo(payload: String): MessageData.MailRuBoxStatus {
        return fromJson<MailBoxStatusPayload>(payload) {
            MessageData.MailRuBoxStatus(
                MailBox(
                    header = mailBox.header,
                    messageCount = mailBox.messageCount,
                    userEmail = mailBox.userEmail,
                    senders = mailBox.senders.map { itemPayload ->
                        Sender(
                            type = itemPayload.type.toMailBoxItemType(),
                            avatar = itemPayload.avatar,
                            email = itemPayload.email,
                            name = itemPayload.name,
                            messageCount = itemPayload.messageCount,
                            payload = itemPayload.payload,
                            event = itemPayload.event
                        )
                    },
                    moreSenders = mailBox.moreSenders?.let { moreSendersPayload ->
                        MailBoxMoreSenders(
                            text = moreSendersPayload.text,
                            payload = moreSendersPayload.payload,
                            event = moreSendersPayload.event
                        )
                    }
                )
            )
        }
    }

    override fun pojoToPayload(data: MessageData.MailRuBoxStatus): String {
        return toJson(data) {
            MailBoxStatusPayload(
                MailBoxPayload(
                    header = mailBox.header,
                    messageCount = mailBox.messageCount,
                    userEmail = mailBox.userEmail,
                    senders = mailBox.senders.map { itemPayload ->
                        SenderPayload(
                            type = itemPayload.type.toIntType(),
                            avatar = itemPayload.avatar,
                            email = itemPayload.email,
                            name = itemPayload.name,
                            messageCount = itemPayload.messageCount,
                            payload = itemPayload.payload,
                            event = itemPayload.event
                        )
                    },
                    moreSenders = mailBox.moreSenders?.let { moreSendersPayload ->
                        MailBoxMoreSendersPayload(
                            text = moreSendersPayload.text,
                            payload = moreSendersPayload.payload,
                            event = moreSendersPayload.event
                        )
                    }
                )
            )
        }
    }

    private fun Sender.Type.toIntType(): Int {
        return when (this) {
            Sender.Type.SENDER -> 1
            Sender.Type.NEWSLETTER -> 2
        }
    }

    private fun Int.toMailBoxItemType(): Sender.Type {
        return when (this) {
            1 -> Sender.Type.SENDER
            2 -> Sender.Type.NEWSLETTER
            else -> throw IllegalArgumentException("Missing Mail box item type: $this")
        }
    }
}