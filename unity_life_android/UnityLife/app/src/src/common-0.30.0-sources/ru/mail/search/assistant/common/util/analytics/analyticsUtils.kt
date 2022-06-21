package ru.mail.search.assistant.common.util.analytics

fun Analytics.timeDifference(time: Long): Long {
    return getCurrentTime() - time
}

fun Analytics.rememberCurrentTime(tag: String) {
    store.remember(tag, getCurrentTime())
}

fun Analytics.memorizedTimeDifference(tag: String): Long? {
    return store.obtain<Long>(tag)?.let { time -> timeDifference(time) }
}