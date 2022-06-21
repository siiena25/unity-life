package ru.mail.search.assistant.data.local.auth

import ru.mail.search.assistant.common.data.exception.AssistantException

class CipherException : AssistantException {

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)
}