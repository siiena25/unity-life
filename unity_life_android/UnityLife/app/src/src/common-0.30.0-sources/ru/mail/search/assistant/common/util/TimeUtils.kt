package ru.mail.search.assistant.common.util

@Suppress("NOTHING_TO_INLINE")
object TimeUtils {

    const val MILLIS_IN_SECOND = 1_000L

    inline fun secondToMillis(seconds: Double): Long {
        return (seconds * MILLIS_IN_SECOND).toLong()
    }

    inline fun secondToMillis(seconds: Float): Long {
        return secondToMillis(seconds.toDouble())
    }

    inline fun millisToSeconds(millis: Long): Double {
        return millis.toDouble() / MILLIS_IN_SECOND
    }
}