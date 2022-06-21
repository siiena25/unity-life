package ru.mail.search.assistant.data.remote

import ru.mail.search.assistant.common.data.exception.ResultParsingException
import ru.mail.search.assistant.common.http.common.HttpMethod
import ru.mail.search.assistant.common.http.common.HttpRequest
import ru.mail.search.assistant.common.http.assistant.AssistantHttpClient
import ru.mail.search.assistant.common.http.assistant.Credentials
import ru.mail.search.assistant.common.util.getBoolean
import ru.mail.search.assistant.common.util.getObject
import ru.mail.search.assistant.entities.AssistantServerStatus

class RemoteDataSource(private val httpClient: AssistantHttpClient) {

    private companion object {

        private const val KEY_RESULT = "result"
    }

    fun getSessionUrl(
        path: String,
        credentials: Credentials,
        params: Map<String, String>
    ): HttpRequest {
        return httpClient.createRequest(HttpMethod.GET, path, credentials) {
            for (param in params) {
                addQueryParameter(param.key, param.value)
            }
        }
    }

    suspend fun checkServerAvailability(credentials: Credentials): AssistantServerStatus {
        val resultJson = httpClient.postWithResult("ready_check", credentials)
            .getJsonBody()
            ?.getObject(KEY_RESULT)
            ?: throw ResultParsingException("Missing result field")
        val isSuccess = resultJson.getBoolean("success", false)
        return if (isSuccess) {
            val isTosConfirmed = !resultJson.getBoolean("need_refresh_tos", false)
            return AssistantServerStatus.Available(isTosConfirmed)
        } else {
            AssistantServerStatus.NotAvailable
        }
    }
}
