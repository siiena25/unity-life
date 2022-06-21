package ru.mail.search.assistant.common.util

interface Logger {

    enum class Level {

        VERBOSE, DEBUG, WARNING, ERROR, INFO
    }

    fun log(level: Level, tag: String, message: String?, error: Throwable?)

    fun d(tag: String, message: String, error: Throwable? = null) {
        log(Level.DEBUG, tag, message, error)
    }

    fun v(tag: String, message: String, error: Throwable? = null) {
        log(Level.VERBOSE, tag, message, error)
    }

    fun i(tag: String, message: String, error: Throwable? = null) {
        log(Level.INFO, tag, message, error)
    }

    fun w(tag: String, message: String) {
        log(Level.WARNING, tag, message, null)
    }

    fun w(tag: String, error: Throwable, message: String? = null) {
        log(Level.WARNING, tag, message, error)
    }

    fun e(tag: String, error: Throwable) {
        log(Level.ERROR, tag, null, error)
    }

    fun e(tag: String, error: Throwable, message: String) {
        log(Level.ERROR, tag, message, error)
    }
}