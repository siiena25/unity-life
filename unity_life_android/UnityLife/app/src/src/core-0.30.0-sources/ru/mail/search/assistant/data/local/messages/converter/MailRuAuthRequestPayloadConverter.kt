package ru.mail.search.assistant.data.local.messages.converter

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import ru.mail.search.assistant.common.util.getString
import ru.mail.search.assistant.common.util.toObject
import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.entities.message.MessageData

internal class MailRuAuthRequestPayloadConverter :
    MessageDataPayloadConverter<MessageData.MailRuAuthRequest> {

    private companion object {

        private const val FIELD_RETRY_EVENT = "retry_event"
        private const val FIELD_CALLBACK_DATA = "callback_data"
    }

    override val type: String get() = MessageTypes.MAIL_RU_AUTH_REQUEST

    private val jsonParser = JsonParser()

    override fun payloadToPojo(payload: String): MessageData.MailRuAuthRequest {
        return jsonParser.parse(payload).toObject()!!.let { json ->
            MessageData.MailRuAuthRequest(
                retryEvent = json.getString(FIELD_RETRY_EVENT)!!,
                callbackData = json.getString(FIELD_CALLBACK_DATA)
            )
        }
    }

    override fun pojoToPayload(data: MessageData.MailRuAuthRequest): String {
        return JsonObject().apply {
            addProperty(FIELD_RETRY_EVENT, data.retryEvent)
            addProperty(FIELD_CALLBACK_DATA, data.callbackData)
        }.toString()
    }
}