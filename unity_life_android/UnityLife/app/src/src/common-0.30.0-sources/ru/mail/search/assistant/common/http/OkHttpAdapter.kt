package ru.mail.search.assistant.common.http

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import ru.mail.search.assistant.common.http.common.HttpBody
import ru.mail.search.assistant.common.http.common.HttpMethod
import ru.mail.search.assistant.common.http.common.HttpRequest
import ru.mail.search.assistant.common.http.common.ServerResponse
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OkHttpAdapter(private val okHttpClient: OkHttpClient) {

    private val emptyBody = ByteArray(0).toRequestBody()

    suspend fun execute(request: HttpRequest): ServerResponse {
        val okRequestBuilder = Request.Builder().apply {
            url(request.url)
            for ((key, value) in request.headers) {
                addHeader(key, value)
            }
            when (request.method) {
                HttpMethod.GET -> get()
                HttpMethod.POST -> post(getRequestBody(request))
                HttpMethod.PUT -> put(getRequestBody(request))
                HttpMethod.PATCH -> patch(getRequestBody(request))
                HttpMethod.DELETE -> delete(getRequestBody(request))
            }
        }
        val okRequest = okRequestBuilder.build()
        val stackTrace = Throwable().stackTrace
        return suspendCancellableCoroutine { continuation ->
            val call = okHttpClient.newCall(okRequest)
            continuation.invokeOnCancellation { call.cancel() }
            call.enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    val result = ServerResponse(response.code, response.message, body)
                    continuation.resume(result)
                }

                override fun onFailure(call: Call, e: IOException) {
                    if (continuation.isCancelled) return
                    e.stackTrace = stackTrace
                    continuation.resumeWithException(e)
                }
            })
        }
    }

    private fun getRequestBody(request: HttpRequest): RequestBody {
        return when (val body = request.body) {
            is HttpBody.Common -> {
                body.data.toRequestBody(body.type?.toMediaTypeOrNull())
            }
            is HttpBody.Form -> {
                val builder = FormBody.Builder()
                for ((key, value) in body.data) {
                    builder.add(key, value)
                }
                builder.build()
            }
            else -> {
                emptyBody
            }
        }
    }
}