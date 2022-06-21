package ru.mail.search.assistant.common.http.assistant

import ru.mail.search.assistant.common.data.remote.NetworkConfig
import ru.mail.search.assistant.common.http.AssistantOkHttpClient
import ru.mail.search.assistant.common.http.common.HttpClient
import ru.mail.search.assistant.common.http.common.HttpErrorHandler
import ru.mail.search.assistant.common.util.analytics.Analytics

class AssistantHttpClientFactory(
    private val networkConfig: NetworkConfig,
    private val httpClient: HttpClient? = null,
    private val credentialsProvider: SessionCredentialsProvider?,
    private val analytics: Analytics? = null
) {

    fun create(): AssistantHttpClient {
        val httpClient = httpClient ?: AssistantOkHttpClient()
        val deviceId = networkConfig.deviceIdProvider.deviceId
        val clientOutdatedCallback = networkConfig.clientOutdatedCallback
        val httpErrorHandler = HttpErrorHandler(credentialsProvider, clientOutdatedCallback)
        return AssistantHttpClient(
            httpClient,
            networkConfig.hostProvider,
            networkConfig.appVersionName,
            deviceId,
            httpErrorHandler,
            credentialsProvider,
            analytics
        )
    }
}