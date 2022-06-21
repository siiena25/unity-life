package ru.mail.search.assistant.common.http.common

class ClientOutdatedException(
    statusCode: Int,
    message: String?
) : HttpException(statusCode, message)