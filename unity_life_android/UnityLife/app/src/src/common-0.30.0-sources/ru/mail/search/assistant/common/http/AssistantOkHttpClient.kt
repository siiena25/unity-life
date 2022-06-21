package ru.mail.search.assistant.common.http

import okhttp3.OkHttpClient
import ru.mail.search.assistant.common.http.common.HttpClient
import ru.mail.search.assistant.common.http.common.HttpRequest
import ru.mail.search.assistant.common.http.common.ServerResponse

class AssistantOkHttpClient(defaultOkHttpClient: OkHttpClient? = null) : HttpClient {

    private val okHttpAdapter = createOkHttpAdapter(defaultOkHttpClient)

    override suspend fun execute(request: HttpRequest): ServerResponse {
        return okHttpAdapter.execute(request)
    }

    private fun createOkHttpAdapter(okHttpClient: OkHttpClient?): OkHttpAdapter {
        val builder = if (okHttpClient != null) {
            reuseOkHttpClient(okHttpClient)
        } else {
            OkHttpClient.Builder()
        }
        val client = builder.build()
        return OkHttpAdapter(client)
    }

    private fun reuseOkHttpClient(okHttpClient: OkHttpClient): OkHttpClient.Builder {
        return OkHttpClient.Builder().apply {
            dispatcher(okHttpClient.dispatcher)
            connectionPool(okHttpClient.connectionPool)
            interceptors() += okHttpClient.interceptors
            networkInterceptors() += okHttpClient.networkInterceptors
            eventListenerFactory(okHttpClient.eventListenerFactory)
        }
    }
}