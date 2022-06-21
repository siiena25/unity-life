package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.ContactsCardPayload
import ru.mail.search.assistant.entities.message.MessageData

internal class ContactsCardPayloadConverter : PayloadGsonConverter<MessageData.ContactsCard, ContactsCardPayload>() {

    override val type: String get() = MessageTypes.CONTACTS_CARD

    override fun payloadToPojo(payload: String): MessageData.ContactsCard {
        return fromJson<ContactsCardPayload>(payload) {
            val contacts = contacts.map {
                MessageData.ContactsCard.Contact(
                    it.id,
                    it.firstName,
                    it.lastName,
                    it.photoUri,
                    it.phoneNumber,
                )
            }
            MessageData.ContactsCard(hasMore, page, callbackEvent, contacts)
        }
    }

    override fun pojoToPayload(data: MessageData.ContactsCard): String {
        return toJson(data) {
            val contacts = contacts.map {
                ContactsCardPayload.Contact(
                    it.id,
                    it.firstName,
                    it.lastName,
                    it.photoUri,
                    it.phoneNumber,
                )
            }
            ContactsCardPayload(hasMore, page, callbackEvent, contacts)
        }
    }
}