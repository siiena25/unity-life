package ru.mail.search.assistant.common.util

inline val String.Companion.EMPTY get() = ""

inline fun String.takeIfNotEmpty(): String? = takeIf { isNotEmpty() }
