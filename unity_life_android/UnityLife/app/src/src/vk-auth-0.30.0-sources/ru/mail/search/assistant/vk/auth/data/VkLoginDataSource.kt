package ru.mail.search.assistant.vk.auth.data

import com.google.gson.Gson
import com.google.gson.JsonObject
import ru.mail.search.assistant.common.data.exception.AuthException
import ru.mail.search.assistant.common.data.exception.ResultParsingException
import ru.mail.search.assistant.common.http.assistant.AssistantHttpClient
import ru.mail.search.assistant.common.http.assistant.Credentials
import ru.mail.search.assistant.common.http.assistant.HttpResult
import ru.mail.search.assistant.common.http.assistant.SessionType
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.common.util.analytics.logParsingError
import ru.mail.search.assistant.common.util.getLong
import ru.mail.search.assistant.common.util.getObject
import ru.mail.search.assistant.common.util.getString
import ru.mail.search.assistant.common.util.secure
import ru.mail.search.assistant.vk.auth.VkAuthorization
import java.util.*

class VkLoginDataSource(
    private val httpClient: AssistantHttpClient,
    private val analytics: Analytics?
) {

    private val gson = Gson()

    suspend fun exchangeSilentToken(uuid: String, token: String, appId: String): VkAuthorization {
        val result = httpClient.postWithResult("account/vk/exchange_silent_token") {
            addQueryParameter("token", token)
            addQueryParameter("uuid", uuid)
            addQueryParameter("app_id", appId)
        }
        return parseResult(result, ::parseVkAuthData)
    }

    suspend fun login(data: VkAuthorization): Credentials {
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

    suspend fun upgradeToken(
        credentials: Credentials,
        appId: String,
        phraseInfo: JsonObject,
    ): VkAuthorization {
        val result = httpClient.postWithResult("account/vk/upgrade_token", credentials) {
            addQueryParameter("app_id", appId)
            val body = JsonObject().apply {
                add("phrase_info", phraseInfo)
            }
            setJsonBody(body.toString())
        }
        return parseResult(result, ::parseVkAuthData)
    }

    private fun parseVkAuthData(result: HttpResult): VkAuthorization {
        return result.getJsonBody()
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

    private suspend fun getRegistrationToken(data: VkAuthorization): RegistrationResponse {
        val userId = data.userId.toString()
        val accessToken = data.accessToken
        val result = httpClient.postWithResult("registration/by_vk") {
            addQueryParameter("vk_user_id", userId)
            addQueryParameter("vk_access_token", accessToken)
        }
        return parseResult(result)
    }

    private suspend fun getSession(token: String): SessionResponse {
        val result = httpClient.postWithResult("registration/get_session") {
            addQueryParameter("reg_token", token)
            addQueryParameter("with_secret", "1")
            addQueryParameter("with_account_info", "0")
        }
        return parseResult(result)
    }

    private inline fun <T> parseResult(result: HttpResult, block: (HttpResult) -> T): T {
        return runCatching { block(result) }
            .onFailure { error ->
                if (error is ResultParsingException) {
                    analytics?.logParsingError(result.request)
                }
            }
            .getOrThrow()
    }

    private inline fun <reified T> parseResult(result: HttpResult): T {
        return runCatching { parseResult(result, T::class.java) }
            .onFailure { error ->
                if (error is ResultParsingException) {
                    analytics?.logParsingError(result.request)
                }
            }
            .getOrThrow()
    }

    private fun <T> parseResult(httpResult: HttpResult, model: Class<T>): T {
        val resultJson = httpResult.getJsonBody()
            ?.getObject("result")
            ?.toString()
            ?: throw ResultParsingException("Failed to parse auth response, missing result field")
        return gson.fromJson(resultJson, model)
    }
}
