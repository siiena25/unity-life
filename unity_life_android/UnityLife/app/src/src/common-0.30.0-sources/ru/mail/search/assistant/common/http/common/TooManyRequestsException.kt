package ru.mail.search.assistant.common.http.common

class TooManyRequestsException(
    statusCode: Int,
    message: String?
) : HttpException(statusCode, message)