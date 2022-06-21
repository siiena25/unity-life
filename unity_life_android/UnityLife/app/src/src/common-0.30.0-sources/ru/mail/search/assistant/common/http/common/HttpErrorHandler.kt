package ru.mail.search.assistant.common.http.common

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import ru.mail.search.assistant.common.data.remote.error.ClientOutdatedCallback
import ru.mail.search.assistant.common.http.assistant.Credentials
import ru.mail.search.assistant.common.http.assistant.SessionCredentialsProvider

internal class HttpErrorHandler(
    private val credentialsProvider: SessionCredentialsProvider?,
    private val clientOutdatedCallback: ClientOutdatedCallback?
) {

    private companion object {

        private const val CODE_CLIENT_OUTDATED = 4006
        private const val CODE_INVALID_SESSION = 4010
        private const val CODE_AUTH_FAILED = 4012
        private const val TOO_MANY_REQUESTS = 4040
    }

    suspend fun handle(
        statusCode: Int,
        message: String?,
        credentials: Credentials?
    ): HttpException {
        return when (statusCode) {
            CODE_CLIENT_OUTDATED -> {
                onClientOutdatedError()
                ClientOutdatedException(statusCode, message)
            }
            CODE_INVALID_SESSION, CODE_AUTH_FAILED -> {
                onSessionExpired(credentials)
                SessionExpiredException(statusCode, message)
            }
            TOO_MANY_REQUESTS -> {
                TooManyRequestsException(statusCode, message)
            }
            else -> {
                HttpException(statusCode, message)
            }
        }
    }

    private suspend fun onClientOutdatedError() {
        clientOutdatedCallback ?: return
        withContext(NonCancellable) {
            clientOutdatedCallback.onClientOutdatedError()
        }
    }

    private suspend fun onSessionExpired(credentials: Credentials?) {
        credentials ?: return
        credentialsProvider ?: return
        withContext(NonCancellable) {
            credentialsProvider.onSessionExpired(credentials)
        }
    }
}