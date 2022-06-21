package ru.mail.search.assistant.common.http.common

class HttpRequest(
    val url: String,
    val method: HttpMethod,
    val headers: Map<String, String> = emptyMap(),
    val body: HttpBody? = null
)