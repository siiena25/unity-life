package ru.mail.search.assistant.data.exception

import ru.mail.search.assistant.common.data.exception.AssistantException

class MediaPlayerConnectException : AssistantException {

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)
}