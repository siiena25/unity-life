package ru.mail.search.assistant.data.local.messages.converter

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import ru.mail.search.assistant.common.util.getString
import ru.mail.search.assistant.common.util.toObject
import ru.mail.search.assistant.data.local.messages.MessageTypes
import ru.mail.search.assistant.entities.message.MessageData

internal class WinkPayloadConverter :
    MessageDataPayloadConverter<MessageData.Wink> {

    private companion object {

        private const val FIELD_TITLE = "title"
        private const val FIELD_SUBTITLE = "subtitle"
        private const val FIELD_BUTTON_TEXT = "button_text"
        private const val FIELD_LINK = "link"
    }

    override val type: String get() = MessageTypes.WINK

    private val jsonParser = JsonParser()

    override fun payloadToPojo(payload: String): MessageData.Wink {
        return jsonParser.parse(payload).toObject()!!.let { json ->
            MessageData.Wink(
                title = json.getString(FIELD_TITLE).orEmpty(),
                subtitle = json.getString(FIELD_SUBTITLE).orEmpty(),
                buttonText = json.getString(FIELD_BUTTON_TEXT).orEmpty(),
                link = json.getString(FIELD_LINK)
            )
        }
    }

    override fun pojoToPayload(data: MessageData.Wink): String {
        return JsonObject().apply {
            addProperty(FIELD_TITLE, data.title)
            addProperty(FIELD_SUBTITLE, data.subtitle)
            addProperty(FIELD_BUTTON_TEXT, data.buttonText)
            addProperty(FIELD_LINK, data.link)
        }.toString()
    }
}