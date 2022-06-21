package ru.mail.search.assistant.common.http.common

open class HttpException(val statusCode: Int, message: String?) : RuntimeException(message) {

    override fun getLocalizedMessage(): String? {
        val localizedMessage = super.getLocalizedMessage()
        return "($statusCode) $localizedMessage"
    }
}