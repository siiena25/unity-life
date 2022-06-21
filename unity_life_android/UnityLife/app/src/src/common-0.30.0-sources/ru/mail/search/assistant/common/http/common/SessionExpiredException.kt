package ru.mail.search.assistant.common.http.common

class SessionExpiredException(
    statusCode: Int,
    message: String?
) : HttpException(statusCode, message)