package ru.mail.search.assistant.common.http.common

sealed class HttpBody {

    abstract val type: String?

    class Common(override val type: String?, val data: ByteArray) : HttpBody()

    class Form(val data: Map<String, String>) : HttpBody() {

        override val type: String = "application/x-www-form-urlencoded"
    }
}