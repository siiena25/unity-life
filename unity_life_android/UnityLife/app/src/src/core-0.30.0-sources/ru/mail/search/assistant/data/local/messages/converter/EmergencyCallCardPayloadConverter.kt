package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.EmergencyCallCardPayload
import ru.mail.search.assistant.entities.message.MessageData

internal class EmergencyCallCardPayloadConverter :
    PayloadGsonConverter<MessageData.EmergencyCallCard, EmergencyCallCardPayload>() {

    override val type: String get() = MessageTypes.EMERGENCY_CALL_CARD

    override fun payloadToPojo(payload: String): MessageData.EmergencyCallCard {
        return fromJson<EmergencyCallCardPayload>(payload) {
            MessageData.EmergencyCallCard(phoneNumber)
        }
    }

    override fun pojoToPayload(data: MessageData.EmergencyCallCard): String {
        return toJson(data) {
            EmergencyCallCardPayload(phoneNumber)
        }
    }
}