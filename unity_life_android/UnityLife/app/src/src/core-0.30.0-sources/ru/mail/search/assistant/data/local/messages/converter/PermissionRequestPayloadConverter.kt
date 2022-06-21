package ru.mail.search.assistant.data.local.messages.converter

import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.data.local.messages.converter.payload.PermissionRequestPayload
import ru.mail.search.assistant.entities.message.MessageData

internal class PermissionRequestPayloadConverter :
    PayloadGsonConverter<MessageData.PermissionRequest, PermissionRequestPayload>() {

    override val type: String get() = MessageTypes.PERMISSION_REQUEST

    override fun payloadToPojo(payload: String): MessageData.PermissionRequest {
        return fromJson<PermissionRequestPayload>(payload) {
            MessageData.PermissionRequest(text)
        }
    }

    override fun pojoToPayload(data: MessageData.PermissionRequest): String {
        return toJson(data) {
            PermissionRequestPayload(message)
        }
    }
}