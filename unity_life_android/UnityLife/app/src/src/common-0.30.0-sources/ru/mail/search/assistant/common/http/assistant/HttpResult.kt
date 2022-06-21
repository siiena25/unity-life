package ru.mail.search.assistant.common.http.assistant

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import ru.mail.search.assistant.common.data.exception.ResultParsingException
import ru.mail.search.assistant.common.http.common.HttpException
import ru.mail.search.assistant.common.http.common.HttpRequest
import ru.mail.search.assistant.common.util.getObject
import ru.mail.search.assistant.common.util.toObject

sealed class HttpResult {

    abstract val request: HttpRequest

    data class Success(
        override val request: HttpRequest,
        val body: String? = null
    ) : HttpResult()

    data class Failure(
        override val request: HttpRequest,
        val error: HttpException,
        val body: String? = null
    ) : HttpResult()

    fun getBodyOrThrow(): String? {
        return when (this) {
            is Success -> body
            is Failure -> throw error
        }
    }

    fun getJsonBody(): JsonObject? {
        val body = getBodyOrThrow() ?: return null
        return runCatching { JsonParser.parseString(body) }.getOrNull()?.toObject()
    }

    fun requireResultJson(): JsonObject {
        val body = getBodyOrThrow() ?: throw ResultParsingException("Missing response body")
        return runCatching { JsonParser.parseString(body) }
            .getOrElse { error ->
                throw ResultParsingException("Failed to parse response body", error)
            }
            ?.toObject()
            ?.getObject("result")
            ?: throw ResultParsingException("Failed to parse response result")
    }
}