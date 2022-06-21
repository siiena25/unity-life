package ru.mail.search.assistant.common.http.assistant

import com.google.gson.JsonObject
import ru.mail.search.assistant.common.http.common.HttpRequestBuilder

inline fun HttpRequestBuilder.setupJsonBody(builder: JsonObject.() -> Unit) {
    val body = JsonObject()
    builder(body)
    setJsonBody(body.toString())
}