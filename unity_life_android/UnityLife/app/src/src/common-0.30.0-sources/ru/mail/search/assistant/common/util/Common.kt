package ru.mail.search.assistant.common.util

inline fun <T> runIf(condition: Boolean, producer: () -> T): T? {
    return if (condition) producer.invoke() else null
}
