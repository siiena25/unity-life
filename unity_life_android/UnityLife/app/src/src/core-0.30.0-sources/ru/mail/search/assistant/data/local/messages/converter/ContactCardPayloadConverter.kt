package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.ContactCardPayload
import ru.mail.search.assistant.entities.message.MessageData

internal class ContactCardPayloadConverter : PayloadGsonConverter<MessageData.ContactCard, ContactCardPayload>() {

    override val type: String get() = MessageTypes.CONTACT_CARD

    override fun payloadToPojo(payload: String): MessageData.ContactCard {
        return fromJson<ContactCardPayload>(payload) {
            MessageData.ContactCard(id, firstName, lastName, photoUri, phoneNumber)
        }
    }

    override fun pojoToPayload(data: MessageData.ContactCard): String {
        return toJson(data) {
            ContactCardPayload(id, firstName, lastName, photoUri, phoneNumber)
        }
    }
}