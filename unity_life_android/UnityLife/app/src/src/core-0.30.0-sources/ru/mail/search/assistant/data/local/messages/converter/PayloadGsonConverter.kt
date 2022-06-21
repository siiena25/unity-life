package ru.mail.search.assistant.data.local.messages.converter

import com.google.gson.Gson
import ru.mail.search.assistant.entities.message.MessageData

abstract class PayloadGsonConverter<T : MessageData, P> : MessageDataPayloadConverter<T> {

    protected val gson = Gson()

    protected inline fun <reified P> fromJson(
        json: String,
        convert: P.() -> T
    ): T {
        return gson.fromJson(json, P::class.java).convert()
    }

    protected inline fun <reified T : MessageData> toJson(
        data: T,
        convert: T.() -> P
    ): String {
        return gson.toJson(data.convert())
    }
}