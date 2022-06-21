package ru.mail.search.assistant.common.http.common

class ServerResponse(val statusCode: Int, val message: String?, val body: String?) {

    @Suppress("MagicNumber")
    fun isSuccess(): Boolean {
        return statusCode in 200..299
    }

    fun requireSuccess(): ServerResponse {
        if (!isSuccess()) {
            throw HttpException(statusCode, message)
        }
        return this
    }
}