package ru.mail.search.assistant.common.data.exception

class ResultParsingException : AssistantException {

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)
}

inline fun parsingError(message: Any): Nothing = throw ResultParsingException(message.toString())