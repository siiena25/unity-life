package ru.mail.search.assistant.data.shuffle.data

import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider
import ru.mail.search.assistant.common.http.assistant.AssistantHttpClient
import ru.mail.search.assistant.common.http.assistant.setupJsonBody
import ru.mail.search.assistant.common.util.getBoolean
import ru.mail.search.assistant.common.util.getInt
import ru.mail.search.assistant.common.util.getObject
import ru.mail.search.assistant.data.shuffle.ShuffleSettingException

class ShuffleRemoteDataSource(
    private val sessionProvider: SessionCredentialsProvider,
    private val httpClient: AssistantHttpClient
) {

    private companion object {

        private const val PATH = "music/shuffle"
        private const val PARAM_SHUFFLE = "shuffle"
    }

    suspend fun isEnabled(): Boolean {
        return httpClient.getWithResult(PATH, sessionProvider.getCredentials())
            .getJsonBody()
            ?.getObject("result")
            ?.getInt(PARAM_SHUFFLE) == 1
    }

    suspend fun setEnabled(enabled: Boolean) {
        val isSuccess = httpClient.putWithResult(PATH, sessionProvider.getCredentials()) {
            setupJsonBody {
                addProperty(PARAM_SHUFFLE, enabled.toInt())
            }
        }
            .getJsonBody()
            ?.getObject("result")
            ?.getBoolean("success", false)
            ?: false

        if (!isSuccess) {
            throw ShuffleSettingException("Can't apply shuffle setting")
        }
    }

    private fun Boolean.toInt() = if (this) 1 else 0
}