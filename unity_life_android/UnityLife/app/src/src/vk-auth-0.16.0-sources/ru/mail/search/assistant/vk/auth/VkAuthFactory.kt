package ru.mail.search.assistant.vk.auth

import com.google.gson.Gson
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import ru.mail.search.assistant.common.data.remote.NetworkConfig
import ru.mail.search.assistant.common.data.remote.NetworkService
import ru.mail.search.assistant.common.util.analytics.Analytics
import ru.mail.search.assistant.vk.auth.data.VkLoginDataSource
import ru.mail.search.assistant.vk.auth.operation.VkAuthCallback
import ru.mail.search.assistant.vk.auth.operation.VkAuthOperation

class VkAuthFactory(
    private val networkConfig: NetworkConfig,
    private val okHttpClient: OkHttpClient,
    private val analytics: Analytics? = null
) {

    fun createAuthDataSource(): VkLoginDataSource {
        val networkService = NetworkService(
            networkConfig.hostProvider,
            okHttpClient,
            networkConfig.appVersionName,
            networkConfig.errorHandler,
            analytics,
            networkConfig.deviceIdProvider.deviceId
        )
        return VkLoginDataSource(networkService, Gson(), JsonParser(), analytics)
    }

    fun createAuthSessionProvider(
        authCallback: VkAuthCallback,
        loginDataSource: VkLoginDataSource = createAuthDataSource()
    ): VkSessionProvider {
        val operation = VkAuthOperation(authCallback, loginDataSource)
        return VkSessionProvider(operation)
    }
}