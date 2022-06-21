package ru.mail.search.assistant.common.http.assistant

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import ru.mail.search.assistant.common.data.remote.HostProvider
import ru.mail.search.assistant.common.http.common.*
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.common.util.analytics.logNetworkError
import ru.mail.search.assistant.common.util.analytics.logServerApiError
import ru.mail.search.assistant.common.util.getInt
import ru.mail.search.assistant.common.util.getString
import ru.mail.search.assistant.common.util.toObject

/**
 * Internal common assistant sdk HTTP client
 */
class AssistantHttpClient internal constructor(
    private val httpClient: HttpClient,
    private val hostProvider: HostProvider,
    private val appVersion: String,
    private val deviceId: String,
    private val httpErrorHandler: HttpErrorHandler,
    private val credentialsProvider: SessionCredentialsProvider?,
    private val analytics: Analytics?
) {

    companion object {

        const val QUERY_KEY_SESSION_ID = "session_id"
        const val QUERY_KEY_DEVICE_ID = "device_id"
        const val QUERY_KEY_DEVICE_VERSION = "device_ver"
        const val HEADER_KEY_AUTHORIZATION = "Authorization"

        private const val HEADER_KEY_ACCEPT = "accept"
        private const val HEADER_VALUE_ACCEPT_JSON = "application/json"
    }

    suspend fun get(
        route: String,
        credentials: Credentials? = null,
        isAuthorized: Boolean = false,
        buildRequest: (HttpRequestBuilder.() -> Unit)? = null
    ) {
        execute(HttpMethod.GET, route, credentials, isAuthorized, buildRequest).getBodyOrThrow()
    }

    suspend fun getWithResult(
        route: String,
        credentials: Credentials? = null,
        isAuthorized: Boolean = false,
        buildRequest: (HttpRequestBuilder.() -> Unit)? = null
    ): HttpResult {
        return execute(HttpMethod.GET, route, credentials, isAuthorized, buildRequest)
    }

    suspend fun post(
        route: String,
        credentials: Credentials? = null,
        isAuthorized: Boolean = false,
        buildRequest: (HttpRequestBuilder.() -> Unit)? = null
    ) {
        execute(HttpMethod.POST, route, credentials, isAuthorized, buildRequest).getBodyOrThrow()
    }

    suspend fun postWithResult(
        route: String,
        credentials: Credentials? = null,
        isAuthorized: Boolean = false,
        buildRequest: (HttpRequestBuilder.() -> Unit)? = null
    ): HttpResult {
        return execute(HttpMethod.POST, route, credentials, isAuthorized, buildRequest)
    }

    suspend fun put(
        route: String,
        credentials: Credentials? = null,
        isAuthorized: Boolean = false,
        buildRequest: (HttpRequestBuilder.() -> Unit)? = null
    ) {
        execute(HttpMethod.PUT, route, credentials, isAuthorized, buildRequest).getBodyOrThrow()
    }

    suspend fun putWithResult(
        route: String,
        credentials: Credentials? = null,
        isAuthorized: Boolean = false,
        buildRequest: (HttpRequestBuilder.() -> Unit)? = null
    ): HttpResult {
        return execute(HttpMethod.PUT, route, credentials, isAuthorized, buildRequest)
    }

    suspend fun deleteWithResult(
        route: String,
        credentials: Credentials? = null,
        isAuthorized: Boolean = false,
        buildRequest: (HttpRequestBuilder.() -> Unit)? = null
    ): HttpResult {
        return execute(HttpMethod.DELETE, route, credentials, isAuthorized, buildRequest)
    }

    suspend fun patchWithResult(
        route: String,
        credentials: Credentials? = null,
        isAuthorized: Boolean = false,
        buildRequest: (HttpRequestBuilder.() -> Unit)? = null
    ): HttpResult {
        return execute(HttpMethod.PATCH, route, credentials, isAuthorized, buildRequest)
    }

    suspend fun execute(
        method: HttpMethod,
        route: String,
        credentials: Credentials? = null,
        isAuthorized: Boolean = false,
        buildRequest: (HttpRequestBuilder.() -> Unit)? = null
    ): HttpResult {
        val appliedCredentials = credentials ?: if (isAuthorized) requireCredentials() else null
        val url = getBaseUrl(route)
        val request = createRequestByUrl(method, url, appliedCredentials, buildRequest)
        return execute(url, request, appliedCredentials)
    }

    fun createRequest(
        method: HttpMethod,
        route: String,
        credentials: Credentials? = null,
        buildRequest: (HttpRequestBuilder.() -> Unit)? = null
    ): HttpRequest {
        val url = getBaseUrl(route)
        return createRequestByUrl(method, url, credentials, buildRequest)
    }

    private fun createRequestByUrl(
        method: HttpMethod,
        url: String,
        credentials: Credentials?,
        buildRequest: (HttpRequestBuilder.() -> Unit)? = null
    ): HttpRequest {
        val builder = HttpRequestBuilder(method, url)
        builder.addQueryParameter(QUERY_KEY_DEVICE_ID, deviceId)
        builder.addQueryParameter(QUERY_KEY_DEVICE_VERSION, appVersion)
        builder.headers[HEADER_KEY_ACCEPT] = HEADER_VALUE_ACCEPT_JSON
        credentials?.let { addSession(builder, credentials) }
        buildRequest?.invoke(builder)
        return builder.build()
    }

    private suspend fun execute(
        route: String,
        request: HttpRequest,
        credentials: Credentials?
    ): HttpResult {
        return runCatching { httpClient.execute(request) }
            .onFailure { error -> analytics?.logNetworkError(route, error) }
            .map { response -> handleResponse(request, route, response, credentials) }
            .getOrThrow()
    }

    private suspend fun handleResponse(
        request: HttpRequest,
        route: String,
        response: ServerResponse,
        credentials: Credentials?
    ): HttpResult {
        return if (response.isSuccess()) {
            HttpResult.Success(request, response.body)
        } else {
            val error = parseError(response, credentials)
            val message = error.message ?: "Missing message"
            analytics?.logServerApiError(route, error.statusCode, message)
            HttpResult.Failure(request, error, response.body)
        }
    }

    private suspend fun parseError(
        response: ServerResponse,
        credentials: Credentials?
    ): HttpException {
        return response.body
            ?.toJsonObjectOrNull()
            ?.let { resultJson ->
                val code = resultJson.getInt("code") ?: response.statusCode
                val message = resultJson.getString("reason") ?: response.message
                createError(code, message, credentials)
            }
            ?: HttpException(response.statusCode, response.message)
    }

    private suspend fun createError(
        statusCode: Int,
        message: String?,
        credentials: Credentials?
    ): HttpException {
        return httpErrorHandler.handle(statusCode, message, credentials)
    }

    private fun getBaseUrl(route: String): String {
        return buildString {
            append(hostProvider.hostUrl)
            if (route.isNotEmpty() && route.first() != '/') {
                append('/')
            }
            append(route)
        }
    }

    private fun addSession(builder: HttpRequestBuilder, credentials: Credentials) {
        builder.addQueryParameter(QUERY_KEY_SESSION_ID, credentials.session.raw)
        val secret = credentials.secret?.raw ?: return
        builder.headers[HEADER_KEY_AUTHORIZATION] = "Bearer $secret"
    }

    private suspend fun requireCredentials(): Credentials {
        credentialsProvider ?: throw IllegalStateException("Missing credentials provider")
        return credentialsProvider.getCredentials()
    }

    private fun String.toJsonObjectOrNull(): JsonObject? {
        return runCatching { JsonParser.parseString(this) }
            .getOrNull()
            ?.toObject()
    }
}