package ru.mail.search.assistant.data.local.messages.converter

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.entities.message.MessageData

internal class IncomingTextPayloadConverter : MessageDataPayloadConverter<MessageData.IncomingText> {

    companion object {

        private const val JSON_PROPERTY_TEXT = "text"
    }

    override val type: String get() = MessageTypes.INCOMING_MESSAGE

    private val jsonParser = JsonParser()

    override fun payloadToPojo(payload: String): MessageData.IncomingText {
        return jsonParser.parse(payload).asJsonObject.let { jsonObject ->
            val text = jsonObject.getAsJsonPrimitive(JSON_PROPERTY_TEXT).asString
            MessageData.IncomingText(text)
        }
    }

    override fun pojoToPayload(data: MessageData.IncomingText): String {
        return JsonObject().apply {
            addProperty(JSON_PROPERTY_TEXT, data.text)
        }.toString()
    }
}