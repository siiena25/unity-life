package ru.mail.search.assistant.common.http.common

import android.net.Uri
import okhttp3.internal.toImmutableMap

class HttpRequestBuilder(
    private val method: HttpMethod,
    url: String
) {

    private companion object {

        private const val BODY_TYPE_JSON = "application/json; charset=utf-8"
    }

    val headers: MutableMap<String, String> = mutableMapOf()

    private val urlBuilder = Uri.parse(url).buildUpon()
    private var body: HttpBody? = null

    fun addQueryParameter(key: String, value: String) {
        urlBuilder.appendQueryParameter(key, value)
    }

    fun addBooleanParameter(key: String, value: Boolean) {
        addQueryParameter(key, if (value) "1" else "0")
    }

    fun setBody(type: String?, data: ByteArray) {
        body = HttpBody.Common(type, data)
    }

    fun setJsonBody(json: String?) {
        setBody(BODY_TYPE_JSON, json.orEmpty().toByteArray())
    }

    fun setFormData(data: Map<String, String>) {
        body = HttpBody.Form(data)
    }

    fun build(): HttpRequest {
        return HttpRequest(
            url = urlBuilder.toString(),
            method = method,
            headers = headers.toImmutableMap(),
            body = body
        )
    }
}