package ru.mail.search.assistant.common.util

import android.util.Log

class LogCatLogger {

    fun print(priority: Int, tag: String, message: String?, error: Throwable?) {
        Log.println(priority, tag, format(message, error))
    }

    private fun format(message: String?, error: Throwable?): String {
        return if (error == null) {
            message.orEmpty()
        } else {
            return buildString {
                if (message != null) {
                    append(message)
                    append('\n')
                }
                append(Log.getStackTraceString(error))
            }
        }
    }
}