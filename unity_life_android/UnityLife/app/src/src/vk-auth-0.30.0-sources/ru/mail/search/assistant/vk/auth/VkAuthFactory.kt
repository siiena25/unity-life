package ru.mail.search.assistant.vk.auth

import ru.mail.search.assistant.common.data.remote.NetworkConfig
import ru.mail.search.assistant.common.http.assistant.AssistantHttpClientFactory
import ru.mail.search.assistant.common.http.common.HttpClient
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.vk.auth.data.VkLoginDataSource
import ru.mail.search.assistant.vk.auth.operation.VkAuthCallback
import ru.mail.search.assistant.vk.auth.operation.VkAuthOperation

class VkAuthFactory(
    networkConfig: NetworkConfig,
    httpClient: HttpClient? = null,
    private val analytics: Analytics? = null
) {

    private val httpClient = AssistantHttpClientFactory(
        networkConfig,
        httpClient,
        credentialsProvider = null,
        analytics
    ).create()

    fun createAuthDataSource(): VkLoginDataSource {
        return VkLoginDataSource(httpClient, analytics)
    }

    fun createAuthSessionProvider(
        authCallback: VkAuthCallback,
        loginDataSource: VkLoginDataSource = createAuthDataSource()
    ): VkSessionProvider {
        val operation = VkAuthOperation(authCallback, loginDataSource)
        return VkSessionProvider(operation)
    }
}