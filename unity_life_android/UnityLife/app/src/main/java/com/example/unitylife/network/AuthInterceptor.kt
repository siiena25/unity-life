package com.example.unitylife.network

import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.http2.ConnectionShutdownException
import okio.IOException
import utils.SharedPreferencesStorage
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val storage: SharedPreferencesStorage,
) : Interceptor {

    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        storage.getAuthToken()?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        storage.getCoordinates().let { coordinates ->
            requestBuilder.addHeader("User-Location", coordinates)
        }

        try {
            val response = chain.proceed(requestBuilder.build())
            val bodyString = response.body!!.string()

            return response.newBuilder()
                .body(ResponseBody.create(response.body?.contentType(), bodyString))
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
            val msg: String
            when (e) {
                is SocketTimeoutException -> {
                    msg =
                        "Запрос выполнялся слишком долго, пришлось его отменить. Проверьте соединение и повторите попытку."
                }
                is UnknownHostException -> {
                    msg =
                        "Интернет-соединение отсутствует. Проверьте, что ваше устройство подключено к сети Интернет."
                }
                is ConnectionShutdownException -> {
                    msg = "Соединение прервано"
                }
                is IOException -> {
                    msg = "Сервер не отвечает. Пожалуйста, повторите попытку позже."
                }
                is IllegalStateException -> {
                    msg = "${e.message}"
                }
                else -> {
                    msg = "${e.message}"
                }
            }

            return Response.Builder()
                .request(requestBuilder.build())
                .protocol(Protocol.HTTP_1_1)
                .code(999)
                .message(msg)
                .body(ResponseBody.create(null, "{${e}}")).build()
        }
    }
}