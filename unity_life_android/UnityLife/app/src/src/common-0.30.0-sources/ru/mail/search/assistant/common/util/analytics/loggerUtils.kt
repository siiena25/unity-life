package ru.mail.search.assistant.common.util.analytics

import ru.mail.search.assistant.common.data.exception.ResultParsingException
import ru.mail.search.assistant.common.http.common.HttpRequest
import ru.mail.search.assistant.common.util.analytics.event.NetworkError
import ru.mail.search.assistant.common.util.analytics.event.ParseError
import ru.mail.search.assistant.common.util.analytics.event.ServerError
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

private const val NETWORK_ERROR_CAUSE_TIMEOUT = "timeout"
private const val NETWORK_ERROR_CAUSE_IO = "IO"

fun Analytics.logNetworkError(route: String, error: Throwable) {
    val cause = when (error) {
        is UnknownHostException -> null
        is SocketTimeoutException -> NETWORK_ERROR_CAUSE_TIMEOUT
        is IOException -> NETWORK_ERROR_CAUSE_IO
        else -> error.javaClass.simpleName
    }
    log(NetworkError(route, cause))
}

fun Analytics.logServerApiError(route: String, code: Int, message: String) {
    log(ServerError(route, code, message))
}

fun Analytics.handleParsingError(request: HttpRequest, error: Throwable) {
    if (error is ResultParsingException) {
        log(ParseError(getRoute(request)))
    }
}

fun Analytics.logParsingError(request: HttpRequest) {
    log(ParseError(getRoute(request)))
}

fun Analytics.logParsingError(method: String, route: String) {
    log(ParseError(getRoute(method, route)))
}

private fun getRoute(request: HttpRequest): String {
    return getRoute(request.method.name, request.url)
}

private fun getRoute(method: String, route: String): String = buildString {
    append("[")
    append(method)
    append("]")
    append(route)
}

fun getErrorDescription(error: Throwable): String {
    return error.message ?: error.javaClass.simpleName
}