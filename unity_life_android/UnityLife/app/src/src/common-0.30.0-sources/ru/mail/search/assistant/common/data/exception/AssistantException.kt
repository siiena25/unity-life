package ru.mail.search.assistant.common.data.exception

open class AssistantException : RuntimeException {

    final override val message: String

    constructor(message: String) : super(message) {
        this.message = message
    }

    constructor(message: String, cause: Throwable) : super(message, cause) {
        this.message = message
    }
}