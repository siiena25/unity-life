package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.ContactNumbersCardPayload
import ru.mail.search.assistant.entities.message.MessageData

internal class ContactNumbersCardPayloadConverter :
    PayloadGsonConverter<MessageData.ContactNumbersCard, ContactNumbersCardPayload>() {

    override val type: String get() = MessageTypes.CONTACT_PHONES_CARD

    override fun payloadToPojo(payload: String): MessageData.ContactNumbersCard {
        return fromJson<ContactNumbersCardPayload>(payload) {
            val numbers = numbers.map {
                MessageData.ContactNumbersCard.PhoneNumber(it.id, it.number, it.name)
            }
            MessageData.ContactNumbersCard(callbackEvent, contactId, firstName, lastName, avatarUri, numbers)
        }
    }

    override fun pojoToPayload(data: MessageData.ContactNumbersCard): String {
        return toJson(data) {
            val numbers = numbers.map {
                ContactNumbersCardPayload.PhoneNumber(it.id, it.number, it.name)
            }
            ContactNumbersCardPayload(callbackEvent, contactId, firstName, lastName, avatarUri, numbers)
        }
    }
}