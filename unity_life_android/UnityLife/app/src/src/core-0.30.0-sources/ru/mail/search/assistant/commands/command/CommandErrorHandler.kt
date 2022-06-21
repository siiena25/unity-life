package ru.mail.search.assistant.commands.command

import kotlinx.coroutines.CancellationException
import ru.mail.search.assistant.common.data.exception.AuthException
import ru.mail.search.assistant.common.data.exception.ServerErrorException
import ru.mail.search.assistant.common.http.common.HttpException
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.isCausedByPoorNetworkConnection
import ru.mail.search.assistant.data.AssistantContextRepository
import ru.mail.search.assistant.data.exception.UserInputException
import java.net.SocketTimeoutException

class CommandErrorHandler(
    private val assistantRepository: AssistantContextRepository,
    private val logger: Logger?
) {

    private companion object {

        private const val TAG = "CommandErrorHandler"
    }

    fun onError(handledError: Throwable) {
        val error = if (handledError is CancellationException) {
            handledError.cause ?: handledError
        } else {
            handledError
        }
        if (error is CancellationException) return
        logError(error)
        val errorToThrow = handleNetworkError(error)
        assistantRepository.onCommandError(errorToThrow)
    }

    private fun logError(error: Throwable) {
        if (error is UserInputException) {
            logError(error.cause)
        } else if (error is AuthException) {
            logger?.w(TAG, error, "Command finished with error")
        } else if (!error.isCausedByPoorNetworkConnection()) {
            logger?.e(TAG, error, "Command finished with error")
        }
    }

    private fun handleNetworkError(error: Throwable): Throwable {
        return if (error is HttpException) {
            when (error.statusCode) {
                4010 -> AuthException(error.message.orEmpty())
                5003, 5004 -> SocketTimeoutException(error.message)
                else -> ServerErrorException(error.message.orEmpty())
            }
        } else {
            error
        }
    }
}