package ru.mail.search.assistant.vk.auth.data

import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.HttpUrl
import okhttp3.RequestBody.Companion.toRequestBody
import ru.mail.search.assistant.auth.common.domain.model.Credentials
import ru.mail.search.assistant.auth.common.domain.model.SessionType
import ru.mail.search.assistant.common.data.exception.AuthException
import ru.mail.search.assistant.common.data.exception.ResultParsingException
import ru.mail.search.assistant.common.data.remote.NetworkService
import ru.mail.search.assistant.common.util.*
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.common.util.analytics.logParsingError
import ru.mail.search.assistant.vk.auth.VkAuthorization
import java.util.*

class VkLoginDataSource(
    private val networkService: NetworkService,
    private val gson: Gson,
    private val jsonParser: JsonParser,
    private val analytics: Analytics?
) {

    fun exchangeSilentToken(uuid: String, token: String, appId: String): VkAuthorization {
        val request = getExchangeSilentTokenRequest(uuid, token, appId)
        return networkService.executeRequest(request)
            .safeMap(::parseVkAuthData)
            .getValueOrThrow()
    }

    fun login(data: VkAuthorization): Credentials {
        val registration = getRegistrationToken(data)
        if (registration.status?.toLowerCase(Locale.getDefault()) != "ready") {
            throw AuthException("Wrong state of registration token response")
        }
        if (registration.token == null) {
            throw AuthException("Missing registration token")
        }
        val sessionResponse = getSession(registration.token)
        if (sessionResponse.sessionId == null) {
            throw AuthException("Missing session id")
        }
        return Credentials(
            session = sessionResponse.sessionId.secure(),
            sessionType = SessionType.BASIC,
            secret = sessionResponse.sessionSecret?.secure()
        )
    }

    private fun parseVkAuthData(response: String): VkAuthorization {
        return jsonParser.parseAsObject(response)
            ?.getObject("result")
            ?.let { jsonElement ->
                VkAuthorization(
                    accessToken = jsonElement.getString("access_token")
                        ?: throw ResultParsingException("Missing token"),
                    userId = jsonElement.getLong("user_id")
                        ?: throw ResultParsingException("Missing user id")
                )
            } ?: throw ResultParsingException("Failed to parse vk auth data")
    }

    private fun getRegistrationToken(data: VkAuthorization): RegistrationResponse {
        val userId = data.userId.toString()
        val accessToken = data.accessToken
        return executeRequest(getVkRegistrationRequest(userId, accessToken))
    }

    private fun getSession(token: String): SessionResponse {
        return executeRequest(getSessionRequest(token))
    }

    private inline fun <reified T> executeRequest(request: NetworkService.HttpRequest): T {
        return executeRequest(request, T::class.java)
    }

    private fun <T> executeRequest(
        request: NetworkService.HttpRequest,
        model: Class<T>
    ): T {
        return networkService.executeRequest(request)
            .safeMap { json -> parseResult(json, model) }
            .doOnError { analytics?.logParsingError(request) }
            .getValueOrThrow()
    }

    private fun <T> parseResult(json: String, model: Class<T>): T {
        val resultJson = jsonParser.parseAsObject(json)
            ?.getObject("result")
            ?.toString()
            ?: throw ResultParsingException("Failed to parse auth response, missing result field")
        return gson.fromJson(resultJson, model)
    }

    private fun getExchangeSilentTokenRequest(
        uuid: String,
        token: String,
        appId: String
    ): NetworkService.HttpRequest {
        return networkService.urlBuilder("account/vk/exchange_silent_token")
            .addQueryParameter("token", token)
            .addQueryParameter("uuid", uuid)
            .addQueryParameter("app_id", appId)
            .build()
            .createPostRequest()
    }

    private fun getVkRegistrationRequest(
        userId: String,
        accessToken: String
    ): NetworkService.HttpRequest {
        return networkService.urlBuilder("registration/by_vk")
            .addQueryParameter("vk_user_id", userId)
            .addQueryParameter("vk_access_token", accessToken)
            .build()
            .createPostRequest()
    }

    private fun getSessionRequest(token: String): NetworkService.HttpRequest {
        return networkService.urlBuilder("registration/get_session")
            .addQueryParameter("reg_token", token)
            .addQueryParameter("with_secret", "1")
            .addQueryParameter("with_account_info", "0")
            .build()
            .createPostRequest()
    }

    private fun HttpUrl.createPostRequest(): NetworkService.HttpRequest {
        return NetworkService.HttpRequest(
            this,
            NetworkService.RequestType.POST,
            ByteArray(0).toRequestBody()
        )
    }
}
