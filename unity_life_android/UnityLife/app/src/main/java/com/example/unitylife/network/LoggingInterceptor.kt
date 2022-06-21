package com.example.unitylife.network

import com.example.unitylife.config.DeviceInfo
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response

class LoggingInterceptor : Interceptor {
    private val headers: Headers
    init {
        DeviceInfo.getInstance().apply {
            headers = Headers.headersOf(
                "App-Version", appVersion,
                "Os-Version", osVersion,
                "Os-Type", osType,
                "Device-ID", deviceId
            )
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        request.headers(headers)
        return chain.proceed(request.build())
    }
}